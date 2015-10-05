package ele638.msiutimetable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ele638 on 27.09.15.
 */
public class Week {

    public List<Day> days;
    int weekType;

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

    public void deleteDay(Day day) {
        days.set(days.indexOf(day), null);
    }

    public List<Day> getAllDays() {
        return days;
    }

    public boolean isEmpty() {
        return days.isEmpty();
    }


}
