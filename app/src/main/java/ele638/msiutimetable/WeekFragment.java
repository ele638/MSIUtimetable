package ele638.msiutimetable;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeekFragment extends Fragment {
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber = 0;

    static WeekFragment newInstance(int page) {
        WeekFragment pageFragment = new WeekFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, null);
        ListView lv = (ListView) view.findViewById(R.id.listview);
        List<Day> alldays = MainActivity.week.get(pageNumber).getAllDays();
        MyListAdapter adapter = new MyListAdapter(alldays);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final long dayID = id;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                TextView header = (TextView) view.findViewById(R.id.day_text);
                View listheader = View.inflate(view.getContext(), R.layout.header, null);
                ((TextView) listheader.findViewById(R.id.day_text)).setText(header.getText().toString());
                View myView = View.inflate(view.getContext(), R.layout.singledaylayout, null);
                ListView lv = (ListView) myView.findViewById(R.id.singlelistview);
                lv.addHeaderView(listheader, null, false);
                SingleDayAdapter adapter1 = new SingleDayAdapter(MainActivity.week.get(pageNumber).get(position).subjects, header.getText().toString());
                lv.setAdapter(adapter1);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("TAGS", "Day ID is " + dayID);
                        Log.d("TAGS", "Subject ID is" + id);
                    }
                });
                builder.setView(myView);
                builder.create().show();
            }
        });
        return view;
    }
}
