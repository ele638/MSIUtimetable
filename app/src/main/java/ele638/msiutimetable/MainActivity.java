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
import android.support.v4.app.FragmentStatePagerAdapter;
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

    //Коды для handler'а
    static final int EXIT_CODE = 0;
    static final int DOWNLOAD_CODE = 1;
    static final int DOWNLOADED_FILE = 2;
    static final int SELECTED = 3;
    //Наполнение SharedPreferences
    static int SAVED_EVENING;
    static int SAVED_COURSE;
    static int SAVED_GROUP;
    static int TEXT_SIZE = 14;
    static boolean INITIALIZED;
    static String title;
    static String SAVED_BASENAME;
    static boolean edit_mode = false;
    SharedPreferences sPref;
    SharedPreferences.Editor ed;
    //Данные класса
    static ArrayList<Week> week;
    static Handler handler;
    static DatabaseHandler db;
    static int current_week;
    //Различные view и прочий хлам
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    File msiu;
    ProgressDialog pd;


    //Метод запуска после сборки/обновления всех данных

    public void init() {
        //Заполняем БД
        db = new DatabaseHandler(getApplicationContext(), SAVED_BASENAME);
        //Заполняем ArrayList'ы
        week = db.getWeeks();
        //Если при присвоении адаптера возник сбой, то запустить первоначальную инициализацию
        try{viewPager.setAdapter(pagerAdapter);} catch (Exception e){FirstInit.showDialog(this);
        }
        //Устанавливаем кастомный шрифт
        CustomFont.setCustomFont(this, viewPager);
        //Устанавливаем viewPager на текущую неделю
        viewPager.setCurrentItem((current_week % 2), true);
    }

    //Сохранение данных в sharedPreferences
    public void saveParam() {
        INITIALIZED = true;
        ed.putInt("Time", SAVED_EVENING);
        ed.putInt("Course", SAVED_COURSE);
        ed.putInt("Group", SAVED_GROUP);
        ed.putInt("TextSize", TEXT_SIZE);
        ed.putBoolean("init", INITIALIZED);
        ed.putString("titleName", title);
        ed.putString("title", title);
        ed.putString("BaseName", SAVED_BASENAME);
        ed.commit();
    }

    //Загрузка данных из sharedPreferences
    public void paramLoad() {
        SAVED_BASENAME = sPref.getString("BaseName", "0");
        SAVED_EVENING = sPref.getInt("Time", 0);
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
                        Parsing.readExcelFile(getApplicationContext());
                        init();
                        break;
                }
            }
        };

        INITIALIZED = sPref.getBoolean("init", false);
        if (!INITIALIZED) {
            FirstInit.showDialog(MainActivity.this);
        } else {
            paramLoad();
            setTitle(sPref.getString("title", "MSIU timetable"));
            try {
                //Открываем файл
                Parsing.openFile(msiu);
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

        if (id == R.id.setdefault) {
            db.deleteAll();
            handler.sendEmptyMessage(SELECTED);
            edit_mode=false;
        }
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

        if (id == R.id.editMode) {

            if (item.isChecked()) {
                week = db.getWeeks();
                viewPager.setAdapter(pagerAdapter);
                CustomFont.setCustomFont(this, viewPager);
                edit_mode = false;
                item.setChecked(false);
            } else {
                week = db.getAllWeeks(MainActivity.SAVED_EVENING);
                viewPager.setAdapter(pagerAdapter);
                CustomFont.setCustomFont(this, viewPager);
                edit_mode = true;
                item.setChecked(true);
            }

        }
        return super.onOptionsItemSelected(item);
    }


    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

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

