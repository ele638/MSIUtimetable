package ele638.msiutimetable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sPref;
    SharedPreferences.Editor ed;
    static int SAVED_COURSE;
    static int SAVED_GROUP;
    static int SAVED_WEEK;
    static boolean INITIALIZED;


    private final String[] daynames = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
    ScrollView scrollView;
    static ArrayList<ArrayList> week;
    File msiu;
    ProgressDialog pd;

    static Handler handler;
    static final int EXIT_CODE = 0;
    static final int DOWNLOAD_CODE = 1;
    static final int DOWNLOADED_FILE = 2;
    static final int SELECTED = 3;

    public void init() {
        week = Parsing.readExcelFile(msiu, SAVED_GROUP, SAVED_COURSE);
        scrollView.addView(output(week.get(SAVED_WEEK)));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        }

        //ДАННЫЕ
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        msiu = new File(getApplicationInfo().dataDir + "/msiu.xls");
        sPref = getSharedPreferences("CONFIG", MODE_PRIVATE);
        ed = sPref.edit();
        pd = new ProgressDialog(this);
        pd.setTitle("Загрузка");
        pd.setMessage("Загружаем данные");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
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
                        FirstInit.showCourseDialog(msiu, MainActivity.this);
                        break;
                    case SELECTED:
                        INITIALIZED = true;
                        ed.putInt("Course", SAVED_COURSE);
                        ed.putInt("Group", SAVED_GROUP);
                        ed.putBoolean("init", INITIALIZED);
                        ed.putInt("Week", SAVED_WEEK);
                        ed.commit();
                        init();
                        break;
                }
            }
        };
        INITIALIZED = sPref.getBoolean("init", false);

        if (!INITIALIZED) {
            FirstInit.showDialog(MainActivity.this);
        } else {
            SAVED_COURSE = sPref.getInt("Course", 0);
            SAVED_GROUP = sPref.getInt("Group", 0);
            SAVED_WEEK = sPref.getInt("Week", 0);
            init();
        }

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
            SAVED_WEEK = 0;
            ed.putInt("Week", SAVED_WEEK);
            ed.commit();
            scrollView.addView(output(week.get(SAVED_WEEK)));
        }
        if (id == R.id.weeknech) {
            scrollView.removeAllViews();
            SAVED_WEEK = 1;
            ed.putInt("Week", SAVED_WEEK);
            ed.commit();
            scrollView.addView(output(week.get(SAVED_WEEK)));
        }
        if (id == R.id.change) {
            scrollView.removeAllViews();
            SAVED_GROUP = 0;
            SAVED_COURSE = 0;
            INITIALIZED = false;
            FirstInit.showCourseDialog(msiu, this);
        }
        if (id == R.id.about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            builder.setTitle("О программе");
            builder.setView(R.layout.about_layout);
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }


}

