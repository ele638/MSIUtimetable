package ele638.msiutimetable;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.Calendar;
import java.util.List;

/**
 * Created by ele638 on 28.09.15.
 */
public class MyListAdapter extends BaseAdapter implements ListAdapter {

    List<Day> days;
    List<Day> alldays;
    int pageNumber;

    MyListAdapter(Week inDays, int pageNumber) {
        this.days = inDays.days;
        this.pageNumber = pageNumber;
        this.alldays = MainActivity.db.getAllWeeks(MainActivity.SAVED_EVENING).get(pageNumber).days;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
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
        View v;
        Calendar day = Calendar.getInstance();
        int currday = day.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        if (MainActivity.current_week == pageNumber && alldays.get(currday).dayName == days.get(position).dayName ){
            v = days.get(position).setCurrentView(vi);
        }else {
            v = days.get(position).setView(vi);
        }
        Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.slide_in_right);
        v.startAnimation(animation);
        return v;
    }


    @Override
    public int getItemViewType(int position) { return 0; }

    @Override
    public int getViewTypeCount() {
        return days.size();
    }

    @Override
    public boolean isEmpty() {
        return days.isEmpty();
    }
}
