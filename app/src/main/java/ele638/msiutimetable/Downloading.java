package ele638.msiutimetable;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class Downloading extends AsyncTask<String, Integer, Boolean>{

    private ProgressDialog pd;
    private File mfile;

    Downloading(ProgressDialog inpd, File inFile) {
        pd = inpd;
        mfile = inFile;
    }


    protected Boolean doInBackground(String... params) {
        try {
            URL url = new URL("http://ele638.ru/msiu.xls");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            File file = new File(mfile.getAbsolutePath());
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            publishProgress(totalSize);
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                publishProgress((int) (downloadedSize * 100 / totalSize));
            }
            fileOutput.close();
        } catch (Exception e) {
            Log.e("Download", e.toString());
            e.printStackTrace();
        }
        return false;
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
        FirstInit.showGroupDialog(Parsing.readGroups(mfile.getAbsolutePath()));
        pd.dismiss();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.show();

    }
}