package ele638.msiutimetable;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.ArrayList;
import java.util.Arrays;

class Subject {
    private final static ArrayList<String> times = new ArrayList<>
            (Arrays.asList("09:00\n10:30", "10:40\n12:10",
                    "12:20\n13:50", "14:30\n16:00", "16:10\n17:40",
                    "17:50\n19:20"));
    final int position;
    final int parity;
    final String day;
    final String subject;
    final String time;
    final String teacher;
    final String type;
    final String place;
    final String auditorium;


    private Subject(String inDay, String inTime, String inSubject,
                    String inTeacher, String inType, String inPlace,
                    String inAud, String inParity) {
        this.day = inDay;
        this.subject = inSubject;
        this.teacher = inTeacher;
        this.type = inType;
        this.place = inPlace;
        this.time = inTime;
        this.position = times.indexOf(time);
        this.auditorium = inAud;
        switch (inParity) {
            case "Числитель":
                this.parity = 1;
                break;
            case "Знаменатель":
                this.parity = 2;
                break;
            default:
                this.parity = 0;
        }
    }

    static ArrayList<Subject> process(HSSFSheet mySheet) {
        String day = "", time, name, type, auditorium, place, teacher, parity = "";
        ArrayList<Subject> out = new ArrayList<>();
        int i = 1;
        do {
            HSSFRow myRow = mySheet.getRow(i);
            if (myRow.getCell(1).toString().equals("")) break;
            if (!myRow.getCell(0).toString().equals("")) day = myRow.getCell(0).toString();
            time = myRow.getCell(1).toString();
            name = myRow.getCell(2).toString();
            type = myRow.getCell(3).toString();
            auditorium = myRow.getCell(4).toString().replace(".0", "");
            place = myRow.getCell(5).toString();
            teacher = myRow.getCell(6).toString();
            if (myRow.getCell(7) != null) parity = myRow.getCell(7).toString();
            out.add(new Subject(day, time, name, teacher, type, place, auditorium, parity));
            i++;
        } while (true);
        return out;
    }

}
