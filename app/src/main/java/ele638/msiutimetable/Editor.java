package ele638.msiutimetable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ele638 on 09.10.15.
 */
public class Editor {

    Week daysOfSelectedWeek;
    Day selectedDay;
    ListView mainlv, lv;
    MyListAdapter adapter;
    SingleDayAdapter adapter1;
    LayoutInflater inflater;
    Context ctx;
    AlertDialog day, edit, confirm;
    int dayPosition;
    int subPosition;
    int pageNumber;




    public View makeMainListView(LayoutInflater infl, Week inWeek, int inpageNumber) {
        inflater = infl;
        pageNumber = inpageNumber;
        daysOfSelectedWeek = inWeek;
        View view = inflater.inflate(R.layout.fragment_week, null);
        mainlv = (ListView) view.findViewById(R.id.listview);
        adapter = new MyListAdapter(daysOfSelectedWeek, pageNumber);
        mainlv.setAdapter(adapter);
        mainlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MainActivity.edit_mode) {
                    lv = makeDaySelectedListView(view, position);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            editionSubject(position, id, view);
                        }
                    });
                }
            }
        });
        return view;
    }

    public ListView makeDaySelectedListView(View view, int position) {
        dayPosition = position;
        selectedDay = daysOfSelectedWeek.get(position);
        ctx = view.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        TextView header = (TextView) view.findViewById(R.id.day_text);
        View listheader = View.inflate(ctx, R.layout.header, null);
        ((TextView) listheader.findViewById(R.id.day_text)).setText(header.getText().toString());
        View myView = View.inflate(ctx, R.layout.singledaylayout, null);
        lv = (ListView) myView.findViewById(R.id.singlelistview);
        lv.addHeaderView(listheader, null, false);
        List<Subject> subjectsofselectedday = selectedDay.subjects;
        adapter1 = new SingleDayAdapter(subjectsofselectedday, header.getText().toString());
        lv.setAdapter(adapter1);
        Button reset = (Button) myView.findViewById(R.id.resetbutton);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
                builder1.setTitle("Восстановить значения?");
                builder1.setMessage("Вы уверены?");
                builder1.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirm.dismiss();
                    }
                });
                builder1.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Parsing.processMSIUSingleDay(dayPosition, pageNumber);
                        daysOfSelectedWeek.updateDay(MainActivity.db.getAllDay(dayPosition + 6 * pageNumber, MainActivity.SAVED_EVENING), dayPosition);
                        selectedDay = daysOfSelectedWeek.get(dayPosition);
                        adapter1.updateDataSet(selectedDay.subjects);
                        mainlv.setAdapter(adapter);
                        day.dismiss();
                        confirm.dismiss();
                        Toast.makeText(ctx, "Значения восстановлены", Toast.LENGTH_SHORT).show();
                    }
                });
                confirm = builder1.create();
                confirm.show();
            }
        });
        Button close = (Button) myView.findViewById(R.id.dayclose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day.dismiss();
            }
        });
        builder.setView(myView);
        day = builder.create();
        day.show();
        return lv;
    }

    public void editionSubject(int position, long id, final View view){
        subPosition = position;
        final Subject selectedSubject = selectedDay.get(position-1);
        if (id != 0 && MainActivity.edit_mode) {
            CharSequence[] items = {"Удалить", "Изменить"};
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Выберите действие");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            MainActivity.db.deleteSubject(selectedSubject, dayPosition + (pageNumber * 6));
                            selectedDay.deleteSubject(selectedSubject);
                            if (selectedDay.isEmpty()) {
                                daysOfSelectedWeek.deleteDay(selectedDay);
                                adapter.notifyDataSetChanged();
                                mainlv.setAdapter(adapter);
                            }
                            lv.setAdapter(adapter1);
                            mainlv.setAdapter(adapter);
                            break;
                        case 1:
                            final AlertDialog.Builder editBuilder = new AlertDialog.Builder(ctx);
                            editBuilder.setTitle("Изменение предмета");
                            View editView = inflater.inflate(R.layout.edit_subject_layout, null);
                            editBuilder.setView(editView);
                            ArrayList<String> subjectsList = daysOfSelectedWeek.getSubjectsList();
                            ArrayList<String> teachersList = daysOfSelectedWeek.getTeachersList();
                            ArrayList<String> typeList = daysOfSelectedWeek.getTypesList();
                            ArrayAdapter<String> subjspinadapter = new ArrayAdapter<String>(ctx, R.layout.support_simple_spinner_dropdown_item, subjectsList);
                            ArrayAdapter<String> teachspinadapter = new ArrayAdapter<String>(ctx, R.layout.support_simple_spinner_dropdown_item, teachersList);
                            ArrayAdapter<String> typespinadapter = new ArrayAdapter<String>(ctx, R.layout.support_simple_spinner_dropdown_item, typeList);
                            final Spinner subjspin = (Spinner) editView.findViewById(R.id.subj_select_spin);
                            final Spinner teachspin = (Spinner) editView.findViewById(R.id.teacher_select_spin);
                            final Spinner typespin = (Spinner) editView.findViewById(R.id.type_select_spin);
                            subjspin.setAdapter(subjspinadapter);
                            teachspin.setAdapter(teachspinadapter);
                            typespin.setAdapter(typespinadapter);
                            subjspin.setSelection(getIndex(subjspin, selectedSubject.subject));
                            teachspin.setSelection(getIndex(teachspin, selectedSubject.teacher));
                            typespin.setSelection(getIndex(typespin, selectedSubject.type));
                            final EditText aud = (EditText) editView.findViewById(R.id.aud);
                            aud.setText(selectedSubject.place);
                            Button cancel = (Button) editView.findViewById(R.id.cancel);
                            Button save = (Button) editView.findViewById(R.id.save);
                            edit = editBuilder.create();
                            edit.show();
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    edit.dismiss();
                                }
                            });
                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    selectedSubject.subject = subjspin.getSelectedItem().toString();
                                    selectedSubject.teacher = teachspin.getSelectedItem().toString();
                                    selectedSubject.type = typespin.getSelectedItem().toString();
                                    selectedSubject.place = aud.getText().toString();
                                    MainActivity.db.updateSubject(selectedSubject, 6*pageNumber + dayPosition);
                                    lv.setAdapter(adapter1);
                                    mainlv.setAdapter(adapter);
                                    edit.dismiss();
                                    day.dismiss();
                                }
                            });
                            break;
                    }
                }
            });
            builder.create().show();
        }else if(MainActivity.edit_mode){
            CharSequence[] items = {"Добавить"};
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Выберите действие");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final AlertDialog.Builder editBuilder = new AlertDialog.Builder(ctx);
                    editBuilder.setTitle("Добавление предмета");
                    View editView = inflater.inflate(R.layout.edit_subject_layout, null);
                    editBuilder.setView(editView);
                    ArrayList<String> subjectsList = daysOfSelectedWeek.getSubjectsList();
                    ArrayList<String> teachersList = daysOfSelectedWeek.getTeachersList();
                    ArrayList<String> typeList = daysOfSelectedWeek.getTypesList();
                    ArrayAdapter<String> subjspinadapter = new ArrayAdapter<String>(ctx, R.layout.support_simple_spinner_dropdown_item, subjectsList);
                    ArrayAdapter<String> teachspinadapter = new ArrayAdapter<String>(ctx, R.layout.support_simple_spinner_dropdown_item, teachersList);
                    ArrayAdapter<String> typespinadapter = new ArrayAdapter<String>(ctx, R.layout.support_simple_spinner_dropdown_item, typeList);
                    final Spinner subjspin = (Spinner) editView.findViewById(R.id.subj_select_spin);
                    final Spinner teachspin = (Spinner) editView.findViewById(R.id.teacher_select_spin);
                    final Spinner typespin = (Spinner) editView.findViewById(R.id.type_select_spin);
                    subjspin.setAdapter(subjspinadapter);
                    teachspin.setAdapter(teachspinadapter);
                    typespin.setAdapter(typespinadapter);
                    final EditText aud = (EditText) editView.findViewById(R.id.aud);
                    Button cancel = (Button) editView.findViewById(R.id.cancel);
                    Button save = (Button) editView.findViewById(R.id.save);
                    edit = editBuilder.create();
                    edit.show();
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            edit.dismiss();
                        }
                    });
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Subject add = new Subject(subjspin.getSelectedItem().toString(), teachspin.getSelectedItem().toString(), typespin.getSelectedItem().toString(), aud.getText().toString(), subPosition-1);
                            MainActivity.db.addSubject(add, 6*pageNumber + dayPosition);
                            selectedDay.subjects.set(subPosition-1, add);
                            lv.setAdapter(adapter1);
                            mainlv.setAdapter(adapter);
                            edit.dismiss();
                            day.dismiss();
                        }
                    });
                }
            });
            edit = builder.create();
            edit.show();
        }
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
}
