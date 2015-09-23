package ele638.msiutimetable;

import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.ArrayList;

/**
 * Created by ele638 on 15.09.15.
 */
public class Subject {
    private final String times[] = {"09:00\n10:30", "10:40\n12:10", "12:20\n13:50", "14:30\n16:00", "16:10\n17:40", "17:50\n19:20"};
    public final String subject;
    public final String time;
    public final String teacher;
    public final String type;
    public final String place;


    public Subject(int count, String inSubject, String inTeacher, String inType, String inPlace) {
        subject = inSubject;
        teacher = inTeacher;
        type = inType;
        place = inPlace;
        time = times[count];
    }

    public static ArrayList<ArrayList> process(HSSFSheet mySheet, int groupnum) {
        int rowPos = 1;
        int cellPos = groupnum;
        ArrayList<ArrayList> weekdaych = new ArrayList<ArrayList>();
        ArrayList<ArrayList> weekdaynech = new ArrayList<ArrayList>();
        ArrayList<ArrayList> week = new ArrayList<ArrayList>();
        //Идем по всей неделе
        for (int i = 0; i < 6; i++) {
            //Идем по дням
            ArrayList<Subject> daych = new ArrayList<Subject>();
            ArrayList<Subject> daynech = new ArrayList<Subject>();
            HSSFRow myRow = mySheet.getRow(cellPos);
            for (int j = 0; j < 6; j++) {
                myRow = mySheet.getRow(rowPos);
                //Идем по часам, сначала первые две строчки - четные дни
                for (int k = 0; k < 2; k++) {
                    Boolean flag = (exept(mySheet.getRow(rowPos), cellPos) == 1);
                    if (!flag) {
                        switch (k) {
                            case 0:
                                daych.add(daych.size(), analyze(j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), cellPos));
                                Log.d("NORM", "Добавлена пара в четный день");
                                break;
                            case 1:
                                daynech.add(daynech.size(), analyze(j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), cellPos));
                                Log.d("NORM", "Добавлена пара в нечетный день");
                                break;
                        }
                    }
                    rowPos += 2;
                }
            }
            weekdaych.add(weekdaych.size(), daych);
            Log.d("NORM", "Записан " + weekdaych.size() + "-й четный день");
            weekdaynech.add(weekdaynech.size(), daynech);
            Log.d("NORM", "Записан " + weekdaynech.size() + "-й нечетный день");
        }
        week.add(weekdaych);
        week.add(weekdaynech);
        return week;
    }

    protected static int exept(HSSFRow row1, int cell) {
        String test;
        //Код 1 - нет наименования предмета
        try {
            test = row1.getCell(cell).toString();
        } catch (Exception e) {
            return 1;
        }
        return (test=="" ? 1 : 0);
    }

    protected static String getVal(HSSFRow myRow, int index) {
        String test;
        try {
            test = myRow.getCell(index).toString();
        } catch (Exception e) {
            return "";
        }
        return test;
    }

    public static Subject analyze(int j, HSSFRow myRow, HSSFRow myRow2, int cell) {
        String subject = "";
        String teacher = "";
        String type = "";
        String place = "";
        //задаем предмет
        subject = getVal(myRow, cell);
        place = getVal(myRow, cell+1).replace(".0", "");
        teacher = getVal(myRow2, cell);
        type = getVal(myRow2, cell+1);
        return new Subject(j, subject, teacher, type, place);
    }
}
