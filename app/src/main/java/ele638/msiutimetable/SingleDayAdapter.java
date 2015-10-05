package ele638.msiutimetable;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ele638 on 29.09.15.
 */
public class SingleDayAdapter extends BaseAdapter implements ListAdapter {

    List<Subject> subjectList;
    String dayname;

    SingleDayAdapter(List inSubjects, String dayname) {
        this.subjectList = inSubjects;
        this.dayname = dayname;
    }

    @Override
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
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return subjectList.size();
    }

    @Override
    public Object getItem(int position) {
        return subjectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (subjectList.get(position) == null) {
            return 0;
        }
        return subjectList.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (subjectList.get(position) == null) {
            View element = inflater.inflate(R.layout.empty_element_layout, null, false);
            TextView time = (TextView) element.findViewById(R.id.time);
            time.setText(Subject.getTime(position)[0] + "\n" + Subject.getTime(position)[1]);
            return element;
        } else {
            return subjectList.get(position).setView(inflater);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return subjectList.size();
    }

    @Override
    public boolean isEmpty() {
        return subjectList.isEmpty();
    }
}
