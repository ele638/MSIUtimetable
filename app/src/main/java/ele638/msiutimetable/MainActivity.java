package ele638.msiutimetable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    static final int EXIT_CODE = 0;
    static final int DOWNLOAD_CODE = 1;
    static final int DOWNLOADED_FILE = 2;
    static final int SELECTED = 3;
    static int SAVED_TIME;
    static int SAVED_COURSE;
    static int SAVED_GROUP;
    static int TEXT_SIZE = 14;
    static boolean INITIALIZED;
    static String title;
    static ArrayList<Week> week;
    static Week selected_week;
    static Handler handler;
    SharedPreferences sPref;
    SharedPreferences.Editor ed;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    File msiu;
    ProgressDialog pd;
    int current_week;

    public void init() {
        Parsing.openFile(msiu);
        week = Parsing.readExcelFile();
        selected_week = week.get(current_week % 2);
        viewPager.setAdapter(pagerAdapter);
        CustomFont.setCustomFont(this, viewPager);
        viewPager.setCurrentItem((current_week % 2), true);
        
    }

    public void saveParam() {
        INITIALIZED = true;
        ed.putInt("Time", SAVED_TIME);
        ed.putInt("Course", SAVED_COURSE);
        ed.putInt("Group", SAVED_GROUP);
        ed.putInt("TextSize", TEXT_SIZE);
        ed.putBoolean("init", INITIALIZED);
        ed.putString("titleName", title);
        ed.putString("title", title);
        ed.commit();
    }

    public void paramLoad() {
        SAVED_TIME = sPref.getInt("Time", 0);
        SAVED_COURSE = sPref.getInt("Course", 0);
        SAVED_GROUP = sPref.getInt("Group", 0);
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
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        msiu = new File(getApplicationInfo().dataDir + "/msiu.xls");
        current_week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) % 2;
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
                        setTitle(title);
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
        if (id == R.id.change) {
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


    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return WeekFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {
            String out;
            switch (position) {
                case 0:
                    out = "Четная неделя";
                    if (current_week == 0) out += " (текущая неделя)";
                    return out;
                case 1:
                    out = "Нечетная неделя";
                    if (current_week == 1) out += " (текущая неделя)";
                    return out;
            }
            return null;
        }
    }
}

