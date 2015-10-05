package ele638.msiutimetable;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, null);
        final ListView mainlv = (ListView) view.findViewById(R.id.listview);
        final Week daysOfSelectedWeek = MainActivity.week.get(pageNumber);
        final MyListAdapter adapter = new MyListAdapter(daysOfSelectedWeek);
        mainlv.setAdapter(adapter);
        mainlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int dayPosition = position;
                final Day selectedDay = daysOfSelectedWeek.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                TextView header = (TextView) view.findViewById(R.id.day_text);
                View listheader = View.inflate(view.getContext(), R.layout.header, null);
                ((TextView) listheader.findViewById(R.id.day_text)).setText(header.getText().toString());
                View myView = View.inflate(view.getContext(), R.layout.singledaylayout, null);
                final ListView lv = (ListView) myView.findViewById(R.id.singlelistview);
                lv.addHeaderView(listheader, null, false);
                List<Subject> subjectsofselectedday = MainActivity.week.get(pageNumber).get(position).subjects;
                final SingleDayAdapter adapter1 = new SingleDayAdapter(subjectsofselectedday, header.getText().toString());
                lv.setAdapter(adapter1);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final int subPosition = position;
                        final Subject selectedSubject = selectedDay.get(position-1);
                        if (id != 0 && MainActivity.edit_mode) {
                            CharSequence[] items = {"Удалить", "Изменить"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Выберите действие");
                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            MainActivity.db.deleteSubject(selectedSubject, dayPosition-1 + pageNumber * 6);
                                            selectedDay.deleteSubject(selectedSubject);
                                            if (selectedDay.isEmpty()) {
                                                daysOfSelectedWeek.deleteDay(selectedDay);
                                                adapter.notifyDataSetChanged();
                                                mainlv.setAdapter(adapter);
                                            }
                                            lv.setAdapter(adapter1);
                                            mainlv.setAdapter(adapter);
                                            break;
                                    }
                                }
                            });
                            builder.create().show();
                        }
                    }
                });
                builder.setView(myView);
                builder.create().show();
            }
        });
        return view;
    }
}
