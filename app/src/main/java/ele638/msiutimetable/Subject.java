package ele638.msiutimetable;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

public class Subject {
    public static String times[][] = {
            {"09:00", "10:30"},
            {"10:40", "12:10"},
            {"12:20", "13:50"},
            {"14:30", "16:00"},
            {"16:10", "17:40"},
            {"17:50", "19:20"},
            {"18:30", "20:00"},
            {"20:10", "21:40"}
    };

    public String subject;
    public String time[];
    public String teacher;
    public String type;
    public String place;
    public int id;


    public Subject(String inSubject, String inTeacher, String inType, String inPlace, int inTime) {
        subject = inSubject;
        teacher = inTeacher;
        type = inType;
        place = inPlace;
        time = times[inTime];
        id = this.hashCode();
    }

    public static String[] getTime(int order) {
        return times[order];
    }

    public boolean isCurrent(String inTime) {
        return (inTime.compareTo(time[0]) > 0 && inTime.compareTo(time[1]) < 0);
    }

    public View setView(LayoutInflater inflater) {
        View element = inflater.inflate(R.layout.element_layout, null, false);
        ((TextView) element.findViewById(R.id.time)).setText(this.time[0] + "\n" + this.time[1]);
        ((TextView) element.findViewById(R.id.subject)).setText(this.subject);
        ((TextView) element.findViewById(R.id.subject)).setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.TEXT_SIZE);
        ((TextView) element.findViewById(R.id.teacher)).setText(this.teacher);
        ((TextView) element.findViewById(R.id.place)).setText(this.place);
        ((TextView) element.findViewById(R.id.place)).setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.TEXT_SIZE);
        ((TextView) element.findViewById(R.id.type)).setText(this.type);
        ((TextView) element.findViewById(R.id.type)).setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.TEXT_SIZE);
        return element;
    }

    public View setCurrentView(LayoutInflater inflater) {
        View element = inflater.inflate(R.layout.element_layout, null, false);
        element.setBackgroundColor(Color.GREEN);
        ((TextView) element.findViewById(R.id.time)).setText(this.time[0] + "\n" + this.time[1]);
        ((TextView) element.findViewById(R.id.subject)).setText(this.subject);
        ((TextView) element.findViewById(R.id.subject)).setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.TEXT_SIZE);
        ((TextView) element.findViewById(R.id.teacher)).setText(this.teacher);
        ((TextView) element.findViewById(R.id.place)).setText(this.place);
        ((TextView) element.findViewById(R.id.place)).setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.TEXT_SIZE);
        ((TextView) element.findViewById(R.id.type)).setText(this.type);
        ((TextView) element.findViewById(R.id.type)).setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.TEXT_SIZE);
        return element;
    }

    public int getId() {
        return id;
    }

    //Несколько служебных функций.
    public int getOrder() {
        return Arrays.asList(times).indexOf(time);
    }

    public String toString() {
        return String.format("%s, %s, %s, %s, %s - %s.", subject, teacher, type, place, time[0], time[1]);
    }
}
