package ele638.msiutimetable;

import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ele638 on 27.09.15.
 */
public class Week {

    int weekType;
    List<Day> days;

    Week(int inweekType) {
        weekType = inweekType;
        days = new ArrayList<>();
    }

    public void add(Day inDay) {
        days.add(inDay);
    }

    public Day get(int i) {
        return days.get(i);
    }

    public int size() {
        return days.size();
    }

    public List<Day> getDays() {
        return days;
    }

    public List<View> getDaysView(LayoutInflater inflater) {
        List<View> lv = new ArrayList<>();
        for (int i = 0; i < days.size(); i++) {
            if (!(days.get(i).isEmpty())) lv.add(days.get(i).setView(inflater, i));
        }
        return lv;
    }
}
