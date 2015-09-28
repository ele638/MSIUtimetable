package ele638.msiutimetable;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public View setView(LayoutInflater inflater, ViewGroup container) {
        View element = inflater.inflate(R.layout.element_layout, container, false);
        ((TextView) element.findViewById(R.id.time)).setText(this.time);
        ((TextView) element.findViewById(R.id.subject)).setText(this.subject);
        ((TextView) element.findViewById(R.id.subject)).setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.TEXT_SIZE);
        ((TextView) element.findViewById(R.id.teacher)).setText(this.teacher);
        ((TextView) element.findViewById(R.id.place)).setText(this.place);
        ((TextView) element.findViewById(R.id.place)).setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.TEXT_SIZE);
        ((TextView) element.findViewById(R.id.type)).setText(this.type);
        ((TextView) element.findViewById(R.id.type)).setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.TEXT_SIZE);
        return element;
    }

    public boolean isEmpty() {
        return subject == "";
    }
}
