package ele638.msiutimetable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.ArrayList;

/**
 * Created by ele638 on 23.09.15.
 */
public class FirstInit {
    static protected AlertDialog alert;

    //Алерт отсутствия файла
    public static void showDialog(Context context) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("Нет файла с расписанием");
        alertBuilder.setMessage("Скачать файл с расписанием? (Потребуется соединение с интернетом, будет скачано около 400 Кб");
        alertBuilder.setCancelable(false);
        alertBuilder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.handler.sendEmptyMessage(MainActivity.EXIT_CODE);
            }
        });
        alertBuilder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
                MainActivity.handler.sendEmptyMessage(MainActivity.DOWNLOAD_CODE);
            }
        });
        alert = alertBuilder.create();
        alert.show();
    }

    //Алерт выбора типа обучения (вечернее, дневное)
    public static void showTimeDialog(final Context context) {
        CharSequence[] items = {"Дневная", "Вечерняя",};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Выберите форму обучения");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                MainActivity.SAVED_TIME = item;
                showCourseDialog(context, item);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showCourseDialog(final Context context, int item) {
        ArrayList<String> courses = Parsing.readCourses().get(item);
        CharSequence[] items = courses.toArray(new CharSequence[courses.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Выберите курс");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                MainActivity.SAVED_COURSE = item * 2 + MainActivity.SAVED_TIME;
                showGroupDialog(context);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public static void showGroupDialog(Context context) {
        ArrayList<String> courses = Parsing.readGroups(MainActivity.SAVED_COURSE);
        final CharSequence[] items = courses.toArray(new CharSequence[courses.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Выберите группу");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                MainActivity.SAVED_BASENAME = items[item].toString();
                MainActivity.SAVED_GROUP = 2 + item * 2;
                MainActivity.title = items[item].toString();
                MainActivity.handler.sendEmptyMessage(MainActivity.SELECTED);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
