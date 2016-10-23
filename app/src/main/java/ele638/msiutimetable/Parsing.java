package ele638.msiutimetable;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by ele638 on 23.09.15.
 */
public class Parsing {
    private static Workbook myWorkBook;
    private static ArrayList<String> groupnames;

    public static void openFile(File filename) {
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
        }
        try {
            // Creating Input Stream
            File file = new File(filename.getAbsolutePath());
            FileInputStream myInput = new FileInputStream(file.getAbsolutePath());
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            myWorkBook = new HSSFWorkbook(myFileSystem);
        } catch (Exception e) {
            String a = e.toString();
            Log.e("WTFuck", a);
        }
    }

    public static ArrayList<ArrayList> readCourses() {
        ArrayList<ArrayList> out = new ArrayList<>();
        ArrayList<String> och = new ArrayList<>();
        ArrayList<String> vech = new ArrayList<>();
        for (int i = 0, k = 0; i < myWorkBook.getNumberOfSheets(); i++) {
            if (k == 0) {
                och.add(myWorkBook.getSheetName(i));
                k = 1;
            } else {
                vech.add(myWorkBook.getSheetName(i).replace("(2)", " (вечер)"));
                k = 0;
            }
        }
        out.add(och);
        out.add(vech);
        return out;
    }

    public static ArrayList<String> readGroups(int inCourse) {
        groupnames = new ArrayList<>();
        HSSFRow myRow = (HSSFRow) myWorkBook.getSheetAt(inCourse).getRow(0);
        for (int i = 2; i < myRow.getLastCellNum(); i += 2) {
            groupnames.add(myRow.getCell(i).toString().replace(".0", ""));
        }
        return groupnames;
    }

    public static void readExcelFile(Context ctx) {
        HSSFSheet mySheet = (HSSFSheet) myWorkBook.getSheetAt(MainActivity.SAVED_COURSE);
        processMSIU(ctx, mySheet, MainActivity.SAVED_EVENING, MainActivity.SAVED_GROUP);
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    public static void processMSIUSingleDay(int day, int pageNumber) {
        HSSFSheet mySheet = (HSSFSheet) myWorkBook.getSheetAt(MainActivity.SAVED_COURSE);
        int groupnum = MainActivity.SAVED_GROUP;
        int time = MainActivity.SAVED_EVENING;
        MainActivity.db.deleteDay(day + 6*pageNumber);
        if (time == 0) {
            int rowPos = 1 + 24 * day;
            for (int j = 0; j < 6; j++) {
                Boolean flag = (exept(mySheet.getRow(rowPos), groupnum) == 1);
                if (!flag)
                    MainActivity.db.addSubject(analyze(j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum), day + pageNumber * 6);
                rowPos += 4;
            }
        } else {
            int rowPos = 1 + 8 * day;
            for (int j = 0; j < (day == 5 ? 6 : 2); j++) {
                Boolean flag = (exept(mySheet.getRow(rowPos), groupnum) == 1);
                if (!flag)
                    MainActivity.db.addSubject(analyze(6 + j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum), day + pageNumber * 6);
                rowPos += 4;
            }
        }
    }


    public static void processMSIU(Context ctx, HSSFSheet mySheet, int time, int groupnum) {
        int rowPos = 1;
        MainActivity.db = new DatabaseHandler(ctx, MainActivity.SAVED_BASENAME);
        MainActivity.db.deleteAll();
        //Парсер для дневной формы обучения
        if (time == 0) {
            //Идем по всей неделе
            for (int i = 0; i < 6; i++) {
                //Идем по дню
                for (int j = 0; j < 6; j++) {
                    //Идем по часам, сначала первые две строчки - четные дни
                    for (int k = 0; k < 2; k++) {
                        Boolean flag = (exept(mySheet.getRow(rowPos), groupnum) == 1);
                        if (!flag) {
                            switch (k) {
                                case 0:
                                    MainActivity.db.addSubject(analyze(j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum), i);
                                    Log.d("NORM", "Добавлена пара в четный день");
                                    break;
                                case 1:
                                    MainActivity.db.addSubject(analyze(j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum), 6 + i);
                                    Log.d("NORM", "Добавлена пара в нечетный день");
                                    break;
                            }
                        }
                        rowPos += 2;
                    }
                }
            }
        } else
        //Парсер для вечерки
        {
            //Идем по будням
            for (int i = 0; i < 5; i++) {
                //Будни вечерки
                for (int j = 0; j < 2; j++) {
                    //Идем по часам, сначала первые две строчки - четные дни
                    for (int k = 0; k < 2; k++) {
                        Boolean flag = (exept(mySheet.getRow(rowPos), groupnum) == 1);
                        if (!flag) {
                            switch (k) {
                                case 0:
                                    MainActivity.db.addSubject(analyze(6 + j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum), i);
                                    Log.d("NORM", "Добавлена пара в четный день");
                                    break;
                                case 1:
                                    MainActivity.db.addSubject(analyze(6 + j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum), 6 + i);
                                    Log.d("NORM", "Добавлена пара в нечетный день");
                                    break;
                            }
                        }
                        rowPos += 2;
                    }
                }
            }
            //Суббота вечерки
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 2; k++) {
                    Boolean flag = (exept(mySheet.getRow(rowPos), groupnum) == 1);
                    if (!flag) {
                        switch (k) {
                            case 0:
                                MainActivity.db.setSubject(analyze(j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum), 5);
                                Log.d("NORM", "Добавлена пара в четный день");
                                break;
                            case 1:
                                MainActivity.db.setSubject(analyze(j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum), 11);
                                Log.d("NORM", "Добавлена пара в нечетный день");
                                break;
                        }
                    }
                    rowPos += 2;
                }
            }
        }
    }

    protected static int exept(HSSFRow row1, int cell) {
        String test;
        //Код 1 - нет наименования предмета
        try {
            test = row1.getCell(cell).toString();
        } catch (Exception e) {
            return 1;
        }
        return (test == "" ? 1 : 0);
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
        //задаем предмет
        return new Subject(getVal(myRow, cell), getVal(myRow2, cell), getVal(myRow2, cell + 1), getVal(myRow, cell + 1).replace(".0", ""), j);
    }
}
