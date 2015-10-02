package ele638.msiutimetable;

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

    public List<Day> getAllDays() {
        return days;
    }


}
