package ele638.msiutimetable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ele638 on 23.09.15.
 */


class Downloading extends AsyncTask<String, Integer, Boolean>{

    final int EXIT_CODE = 0;
    final int DOWNLOAD_CODE = 1;
    final int DOWNLOADED_FILE = 2;
    Context context;
    ProgressDialog pd;
    File mfile;

    public Downloading(Context ctx, ProgressDialog inpd, File inFile){
        this.context=ctx;
        pd = inpd;
        mfile = inFile;
    }



    protected Boolean doInBackground(String... params) {
        Looper.prepare();
        try {
            //set the download URL, a url that points to a file on the internet
            //this is the file to be downloaded
            URL url = new URL("https://drive.google.com/uc?export=download&confirm=no_antivirus&id=0B8m98s_LEPQfVTJZcjFneW9BRlU");
            //create the new connection
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            //and connect!
            urlConnection.connect();
            //set the path where we want to save the file
            //in this case, going to save it on the root directory of the
            //sd card.
            //create a new file, specifying the path, and the filename
            //which we want to save the file as.
            File file = new File(mfile.getAbsolutePath());
            //this will be used to write the downloaded data into the file we created
            FileOutputStream fileOutput = new FileOutputStream(file);
            //this will be used in reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();
            //this is the total size of the file
            int totalSize = urlConnection.getContentLength();
            publishProgress(totalSize);
            //variable to store total downloaded bytes
            int downloadedSize = 0;
            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0; //used to store a temporary size of the buffer
            //now, read through the input buffer and write the contents to the file
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                //add the data in the buffer to the file in the file output stream (the file on the sd card
                fileOutput.write(buffer, 0, bufferLength);
                //add up the size so we know how much is downloaded
                downloadedSize += bufferLength;
                //this is where you would do something to report the prgress, like this maybe
                publishProgress((int) (downloadedSize * 100 / totalSize));
            }
            //close the output stream when done
            fileOutput.close();
//catch some possible errors...
        } catch (MalformedURLException e) {
            errorAlert();
            Looper.loop();
        } catch (IOException e) {
            errorAlert();
            Looper.loop();
        } catch (Exception e) {
            errorAlert();
            Looper.loop();
        }
        return false;
    }

    public void errorAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alertDialog;
        builder.setTitle("Ошибка загрузки");
        builder.setMessage("Произошла ошибка загрузки файла, проверьте соединение с интернетом и перезапустите приложение");
        builder.setCancelable(false);
        builder.setNegativeButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.handler.sendEmptyMessage(EXIT_CODE);
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        pd.setIndeterminate(false);
        pd.setMax(100);
        pd.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        pd.dismiss();
        MainActivity.handler.sendEmptyMessage(DOWNLOADED_FILE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.show();

    }
}