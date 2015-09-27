package ele638.msiutimetable;

/**
 * Created by ele638 on 15.09.15.
 */
public class Subject {
    public static String times[] = {
            "09.00\n10.30",
            "10.40\n12.10",
            "12.20\n13.50",
            "14.30\n16.00",
            "16.10\n17.40",
            "17.50\n19.20",
            "18.30\n20.00",
            "20.10\n21.40"};


    public String subject;
    public String time;
    public String teacher;
    public String type;
    public String place;


    public Subject(String inSubject, String inTeacher, String inType, String inPlace, int inTime) {
        subject = inSubject;
        teacher = inTeacher;
        type = inType;
        place = inPlace;
        time = times[inTime];
    }


}
