package ele638.msiutimetable;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ele638 on 28.09.15.
 */
public class MyListAdapter implements ListAdapter {

    List<Day> days;
    List<Day> alldays;
    List<Boolean> weekends;

    MyListAdapter(List<Day> inalldays) {
        List<Day> out = new ArrayList<>();
        this.alldays = inalldays;
        weekends = new ArrayList<>();
        for (int i = 0; i < alldays.size(); i++) {
            if (alldays.get(i).isEmpty()) {
                weekends.add(true);
            } else {
                out.add(alldays.get(i));
                weekends.add(false);
            }
        }
        this.days = out;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public Object getItem(int position) {
        return days.get(position);
    }

    @Override
    public long getItemId(int position) {
        return days.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = days.get(position).setView(vi);
        int i = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2;
        if (alldays.indexOf(days.get(position)) == i) {
            try {
                v = days.get(position).setCurrentView(vi);
                Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.slide_in_right);
                v.startAnimation(animation);
                return v;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.slide_in_right);
        v.startAnimation(animation);

        return v;
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return days.size();
    }

    @Override
    public boolean isEmpty() {
        return days.isEmpty();
    }
}
