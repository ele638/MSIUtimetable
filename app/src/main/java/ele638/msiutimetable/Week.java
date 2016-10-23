package ele638.msiutimetable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ele638 on 27.09.15.
 */
public class Week {

    public List<Day> days;

    Week() {
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

    public void updateDay(Day day, int daypos){
        days.set(daypos, day);
    }

    public int size(){return days.size();}

    public ArrayList<String> getSubjectsList(){
        ArrayList<String> out = new ArrayList<>();
        for (int i=0; i<size(); i++){
            for (int j=0; j<days.get(i).size(); j++){
                if (days.get(i).get(j) != null) {
                    if (!out.contains(days.get(i).get(j).subject)) out.add(days.get(i).get(j).subject);
                }
            }
        }
        return out;
    }
    public ArrayList<String> getTeachersList(){
        ArrayList<String> out = new ArrayList<>();
        for (int i=0; i<size(); i++){
            for (int j=0; j<days.get(i).size(); j++){
                if (days.get(i).get(j) != null) {
                    if (!out.contains(days.get(i).get(j).teacher)) out.add(days.get(i).get(j).teacher);
                }
            }
        }
        return out;
    }
    public ArrayList<String> getTypesList(){
        ArrayList<String> out = new ArrayList<>();
        for (int i=0; i<size(); i++){
            for (int j=0; j<days.get(i).size(); j++){
                if (days.get(i).get(j) != null) {
                    if (!out.contains(days.get(i).get(j).type)) out.add(days.get(i).get(j).type);
                }
            }
        }
        return out;
    }

    public boolean isEmpty() {
        return days.isEmpty();
    }


}
