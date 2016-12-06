package ele638.msiutimetable;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<Subject> week;
    ScrollView scrollView;
    File msiu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ДАННЫЕ
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        msiu = new File(getApplicationInfo().dataDir + "/msiu.xls");

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Загрузка");
        pd.setMessage("Загружаем данные");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        FirstInit firstInit = new FirstInit();


        if (!msiu.exists()) {
            firstInit.showDialog(msiu.getAbsolutePath(), this, pd, msiu);
        } else {
            try {
                scrollView.addView(output(week));
            } catch (Exception e) {
                msiu.delete();
                firstInit.showDialog(msiu.getAbsolutePath(), this, pd, msiu);
            }
        }
    }

    private View output(ArrayList<Subject> subjects) {
        LayoutInflater inflater = this.getLayoutInflater();
        LinearLayout dayview, mainlayout;
        View mainview, view, header, element, separator;
        String day;
        mainview = inflater.inflate(R.layout.mainlayout, null, false);
        mainlayout = (LinearLayout) mainview.findViewById(R.id.mainlayout);
        int i = 0;
        Subject s = subjects.get(i);
        do {
            view = inflater.inflate(R.layout.day_layout, mainlayout, false);
            dayview = (LinearLayout) view.findViewById(R.id.dayview);
            header = inflater.inflate(R.layout.header, dayview, false);
            ((TextView) header.findViewById(R.id.day_text)).setText(s.day);
            separator = inflater.inflate(R.layout.separator, dayview, false);
            dayview.addView(header);
            dayview.addView(separator);
            day = s.day;
            while (day.equals(s.day)) {
                element = inflater.inflate(R.layout.element_layout, dayview, false);
                ((TextView) element.findViewById(R.id.time)).setText(s.time);
                ((TextView) element.findViewById(R.id.subject)).setText(s.subject);
                ((TextView) element.findViewById(R.id.teacher)).setText(s.teacher);
                ((TextView) element.findViewById(R.id.place)).setText(s.place);
                ((TextView) element.findViewById(R.id.aud)).setText(s.auditorium);
                ((TextView) element.findViewById(R.id.type)).setText(s.type);
                dayview.addView(element);
                separator = inflater.inflate(R.layout.separator, dayview, false);
                dayview.addView(separator);
                if (++i < subjects.size()) {
                    s = subjects.get(i);
                } else break;
            }
            mainlayout.addView(dayview);
        } while (i < subjects.size());
        return mainlayout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.reload:
                scrollView.removeAllViews();
                scrollView.addView(output(week));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}

