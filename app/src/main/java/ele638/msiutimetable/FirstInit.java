package ele638.msiutimetable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ele638 on 23.09.15.
 */
public class FirstInit {

    protected Boolean flag;
    protected String path;
    protected AlertDialog alert;
    protected static Context ctx;
    protected ProgressDialog pd;
    protected static File mfile;

    public void showDialog(String inPath, Context context, ProgressDialog inPD, File infile) {
        ctx = context;
        path = inPath;
        pd = inPD;
        mfile = infile;

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("Нет файла с расписанием");
        alertBuilder.setMessage("Скачать файл с расписанием? (Потребуется соединение с интернетом, будет скачано около 400 Кб");
        alertBuilder.setCancelable(false);
        alertBuilder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        alertBuilder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
                new Downloading(ctx, pd, mfile).execute(path);

            }
        });
        alert = alertBuilder.create();
        alert.show();
    }

    public static void showGroupDialog(ArrayList<String> list){
        CharSequence[] items = list.toArray(new CharSequence[list.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Выберите группу");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Parsing.readExcelFile(mfile.getAbsolutePath(), (2+item*2));
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
