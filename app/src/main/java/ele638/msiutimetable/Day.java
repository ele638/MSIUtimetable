package ele638.msiutimetable;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ele638 on 27.09.15.
 */
public class Day {

    public static String[] daynames = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
    public List<Subject> subjects;
    String dayName;
    int id;

    Day(int namePosition) {
        dayName = daynames[namePosition];
        subjects = new ArrayList<>();
        id = this.hashCode();
    }

    public void add(Subject inSubject) {
        subjects.add(inSubject);
    }

    public int getIndex(Subject subject) {
        return subjects.indexOf(subject);
    }


    public int size() {
        return subjects.size();
    }

    public Subject get(int i) {
        return subjects.get(i);
    }

    public int getId() {
        return id;
    }

    public void deleteSubject(Subject subject) {
        subjects.set(subjects.indexOf(subject), null);
    }

    public boolean isEmpty() {
        return subjects.isEmpty();
    }

    public View setCurrentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.day_layout, null, false);
        CardView dayview = (CardView) view.findViewById(R.id.card);
        dayview.setCardBackgroundColor(Color.rgb(195, 255, 228));
        LinearLayout layout = (LinearLayout) dayview.findViewById(R.id.card_layout);
        View header = inflater.inflate(R.layout.header, dayview, false);
        TextView title = (TextView) header.findViewById(R.id.day_text);
        title.setText(dayName);
        String current_time = String.format("%02d", Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", Calendar.getInstance().get(Calendar.MINUTE));
        layout.addView(header, 0);
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i) == null) {
                View element = inflater.inflate(R.layout.empty_element_layout, null, false);
                TextView time = (TextView) element.findViewById(R.id.time);
                time.setText(Subject.getTime(i)[0] + "\n" + Subject.getTime(i)[1]);
                layout.addView(element, i + 1);
            } else {
                if (subjects.get(i).isCurrent(current_time)) {
                    layout.addView(subjects.get(i).setCurrentView(inflater), i + 1);
                } else {
                    layout.addView(subjects.get(i).setView(inflater), i + 1);
                }
            }
        }
        return view;
    }

    public View setView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.day_layout, null, false);
        CardView dayview = (CardView) view.findViewById(R.id.card);
        LinearLayout layout = (LinearLayout) dayview.findViewById(R.id.card_layout);
        View header = inflater.inflate(R.layout.header, dayview, false);
        TextView title = (TextView) header.findViewById(R.id.day_text);
        title.setText(dayName);
        layout.addView(header, 0);
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i) == null) {
                View element = inflater.inflate(R.layout.empty_element_layout, null, false);
                TextView time = (TextView) element.findViewById(R.id.time);
                time.setText(Subject.getTime(i)[0] + "\n" + Subject.getTime(i)[1]);
                layout.addView(element, i + 1);
            } else {
                layout.addView(subjects.get(i).setView(inflater), i + 1);
            }
        }
        return view;
    }
}

