package ele638.msiutimetable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sPref;
    SharedPreferences.Editor ed;
    static int SAVED_TIME;
    static int SAVED_COURSE;
    static int SAVED_GROUP;
    static int SAVED_WEEK;
    static int TEXT_SIZE = 14;
    static boolean INITIALIZED;
    static String title;


    private final String[] daynames = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
    private final String[] weeks = {"Четная неделя", "Нечетная неделя"};
    static String[] timeList;
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
        scrollView.removeAllViews();
        Parsing.openFile(msiu);
        week = Parsing.readExcelFile();
        scrollView.addView(output(week.get(SAVED_WEEK), TEXT_SIZE));
        CustomFont.setCustomFont(this, scrollView);
    }

    public void saveParam() {
        INITIALIZED = true;
        ed.putInt("Time", SAVED_TIME);
        ed.putInt("Course", SAVED_COURSE);
        ed.putInt("Group", SAVED_GROUP);
        ed.putInt("Week", SAVED_WEEK);
        ed.putInt("TextSize", TEXT_SIZE);
        ed.putBoolean("init", INITIALIZED);
        ed.putString("titleName", title);
        ed.putString("title", title + " - " + weeks[SAVED_WEEK]);
        ed.commit();
    }

    public void paramLoad() {
        SAVED_TIME = sPref.getInt("Time", 0);
        SAVED_COURSE = sPref.getInt("Course", 0);
        SAVED_GROUP = sPref.getInt("Group", 0);
        SAVED_WEEK = sPref.getInt("Week", 0);
        TEXT_SIZE = sPref.getInt("TextSize", 14);
        title = sPref.getString("titleName", "MSIU");
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
                        Parsing.openFile(msiu);
                        FirstInit.showTimeDialog(MainActivity.this);
                        break;
                    case SELECTED:
                        saveParam();
                        setTitle(title + " - " + weeks[SAVED_WEEK]);
                        init();
                        break;
                }
            }
        };
        paramLoad();
        INITIALIZED = sPref.getBoolean("init", false);
        if (!INITIALIZED) {
            FirstInit.showDialog(MainActivity.this);
        } else {
            paramLoad();
            setTitle(sPref.getString("title", "MSIU timetable"));
            try {
                init();
            } catch (Exception e) {
                FirstInit.showDialog(MainActivity.this);
            }

        }

    }


    private View output(ArrayList<ArrayList> week, int textSize) {
        LayoutInflater inflater = this.getLayoutInflater();
        View mainview = inflater.inflate(R.layout.mainlayout, null, false);
        LinearLayout mainlayout = (LinearLayout) mainview.findViewById(R.id.mainlayout);
        for (int i = 0; i < week.size(); i++) {
            ArrayList<Subject> subjects = week.get(i);
            if (subjects.size() != 0) {
                View view = inflater.inflate(R.layout.day_layout, null, false);
                CardView cardView = (CardView) view.findViewById(R.id.card);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout dayview = new LinearLayout(MainActivity.this);
                dayview.setLayoutParams(lparams);
                dayview.setOrientation(LinearLayout.VERTICAL);
                View header = inflater.inflate(R.layout.header, dayview, false);
                ((TextView) header.findViewById(R.id.day_text)).setText(daynames[i]);
                dayview.addView(header);
                for (int j = 0; j < subjects.size(); j++) {
                    Subject s = subjects.get(j);
                    View element = inflater.inflate(R.layout.element_layout, dayview, false);
                    ((TextView) element.findViewById(R.id.time)).setText(s.time);
                    ((TextView) element.findViewById(R.id.subject)).setText(s.subject);
                    ((TextView) element.findViewById(R.id.subject)).setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                    ((TextView) element.findViewById(R.id.teacher)).setText(s.teacher);
                    ((TextView) element.findViewById(R.id.place)).setText(s.place);
                    ((TextView) element.findViewById(R.id.place)).setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                    ((TextView) element.findViewById(R.id.type)).setText(s.type);
                    ((TextView) element.findViewById(R.id.type)).setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                    dayview.addView(element);
                }
                cardView.addView(dayview);
                mainlayout.addView(cardView);
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
            ed.putString("titleName", title);
            ed.putString("title", title + " - " + weeks[SAVED_WEEK]);
            ed.commit();
            setTitle(title + " - " + weeks[SAVED_WEEK]);
            scrollView.addView(output(week.get(SAVED_WEEK), TEXT_SIZE));
        }
        if (id == R.id.weeknech) {
            scrollView.removeAllViews();
            SAVED_WEEK = 1;
            ed.putInt("Week", SAVED_WEEK);
            ed.putString("titleName", title);
            ed.putString("title", title + " - " + weeks[SAVED_WEEK]);
            ed.commit();
            setTitle(title + " - " + weeks[SAVED_WEEK]);
            scrollView.addView(output(week.get(SAVED_WEEK), TEXT_SIZE));
        }
        if (id == R.id.change) {
            scrollView.removeAllViews();
            SAVED_GROUP = 0;
            SAVED_COURSE = 0;
            INITIALIZED = false;
            FirstInit.showTimeDialog(this);
        }
        if (id == R.id.about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            builder.setTitle("О программе");
            View view = View.inflate(this, R.layout.about_layout, null);
            builder.setView(view);
            builder.create().show();
        }
        if (id == R.id.textSize) {
            CharSequence[] items = {"Маленький", "Средний", "Большой"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Выберите размер шрифта");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0:
                            TEXT_SIZE = 14;
                            ed.putInt("TextSize", TEXT_SIZE);
                            ed.commit();
                            init();
                            break;
                        case 1:
                            TEXT_SIZE = 18;
                            ed.putInt("TextSize", TEXT_SIZE);
                            ed.commit();
                            init();
                            break;
                        case 2:
                            TEXT_SIZE = 22;
                            ed.putInt("TextSize", TEXT_SIZE);
                            ed.commit();
                            init();
                            break;
                        default:
                            TEXT_SIZE = 14;
                            ed.putInt("TextSize", TEXT_SIZE);
                            ed.commit();
                            init();
                            break;
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }


}

