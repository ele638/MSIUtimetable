package ele638.msiutimetable;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ele638 on 27.09.15.
 */
public class Day {

    public static String[] daynames = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
    List<Subject> subjects;
    String dayName;

    Day(int namePosition) {
        dayName = daynames[namePosition];
        subjects = new ArrayList<>();
    }

    public void add(Subject inSubject) {
        subjects.add(inSubject);
    }

    public int size() {
        return subjects.size();
    }

    public Subject get(int i) {
        return subjects.get(i);
    }

    public boolean isEmpty() {
        return subjects.isEmpty();
    }


    public View setView(LayoutInflater inflater, int dayName) {
        View view = inflater.inflate(R.layout.day_layout, null, false);
        CardView dayview = (CardView) view.findViewById(R.id.card);
        LinearLayout layout = (LinearLayout) dayview.findViewById(R.id.card_layout);
        View header = inflater.inflate(R.layout.header, dayview, false);
        TextView title = (TextView) header.findViewById(R.id.day_text);
        title.setText(daynames[dayName]);
        layout.addView(header, 0);
        for (int i = 0; i < subjects.size(); i++) {
            layout.addView(subjects.get(i).setView(inflater, dayview), i + 1);
        }
        return view;
    }
}

