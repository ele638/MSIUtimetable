package ele638.msiutimetable;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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


    private final String[] daynames = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
    ScrollView scrollView;
    static ArrayList<ArrayList> week;
    static ArrayList<ArrayList> weekch;
    static ArrayList<ArrayList> weeknech;
    File msiu;
    ProgressDialog pd;

    static Handler handler;
    static final int EXIT_CODE = 0;
    static final int DOWNLOAD_CODE = 1;
    static final int DOWNLOADED_FILE = 2;
    static final int SELECTED = 3;

    public void init() {
        FirstInit.showCourseDialog(Parsing.readCourses(msiu.getAbsolutePath()));
        scrollView.addView(output(weekch));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ДАННЫЕ
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        msiu = new File(getApplicationInfo().dataDir + "/msiu.xls");

        pd = new ProgressDialog(this);
        pd.setTitle("Загрузка");
        pd.setMessage("Загружаем данные");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        FirstInit firstInit = new FirstInit();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case EXIT_CODE:
                        System.exit(0);
                        break;
                    case DOWNLOAD_CODE:
                        new Downloading(MainActivity.this, pd, msiu).execute();
                        break;
                    case DOWNLOADED_FILE:
                        FirstInit.showCourseDialog(Parsing.readCourses(msiu.getAbsolutePath()));
                        break;
                    case SELECTED:
                        init();
                        break;
                }
            }
        };


            firstInit.showDialog(msiu.getAbsolutePath(), this, pd, msiu);

    }


    private View output(ArrayList<ArrayList> week) {
        LayoutInflater inflater = this.getLayoutInflater();
        View mainview = inflater.inflate(R.layout.mainlayout, null, false);
        LinearLayout mainlayout = (LinearLayout) mainview.findViewById(R.id.mainlayout);
        for (int i = 0; i < week.size(); i++) {
            ArrayList<Subject> subjects = week.get(i);
            if (subjects.size() != 0) {
                View view = inflater.inflate(R.layout.day_layout, null, false);
                LinearLayout dayview = (LinearLayout) view.findViewById(R.id.dayview);
                View header = inflater.inflate(R.layout.header, dayview, false);
                ((TextView) header.findViewById(R.id.day_text)).setText(daynames[i]);
                dayview.addView(header);
                for (int j = 0; j < subjects.size(); j++) {
                    Subject s = subjects.get(j);
                    View element = inflater.inflate(R.layout.element_layout, dayview, false);
                    ((TextView) element.findViewById(R.id.time)).setText(s.time);
                    ((TextView) element.findViewById(R.id.subject)).setText(s.subject);
                    ((TextView) element.findViewById(R.id.teacher)).setText(s.teacher);
                    ((TextView) element.findViewById(R.id.place)).setText(s.place);
                    ((TextView) element.findViewById(R.id.type)).setText(s.type);
                    dayview.addView(element);
                }
                View separator = inflater.inflate(R.layout.separator, dayview, false);
                dayview.addView(separator);
                mainlayout.addView(dayview);
            }
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.weekch) {
            scrollView.removeAllViews();
            scrollView.addView(output(weekch));
        }
        if (id == R.id.weeknech) {
            scrollView.removeAllViews();
            scrollView.addView(output(weeknech));
        }
        if (id == R.id.change) {
            scrollView.removeAllViews();
            handler.sendEmptyMessage(DOWNLOADED_FILE);
        }

        return super.onOptionsItemSelected(item);
    }


}

