package ele638.msiutimetable;

import android.os.Environment;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

class Parsing {

    static ArrayList<String> readGroups(String filename) {
        ArrayList<String> out = new ArrayList<>();
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return out;
        }
        try {
            // Creating Input Stream
            File file = new File(filename);
            FileInputStream myInput = new FileInputStream(file.getAbsolutePath());
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            Workbook myWorkBook = new HSSFWorkbook(myFileSystem);

            /** We now need something to iterate through the cells.**/
            for (int i = 0; i < myWorkBook.getNumberOfSheets(); i++) {
                out.add(myWorkBook.getSheetName(i));
            }

        } catch (Exception e) {
            String a = e.toString();
            Log.e("WTFuck", a);
        }

        return out;
    }

    static ArrayList<Subject> readExcelFile(String filename, int group) {
        ArrayList<Subject> out = new ArrayList<>();
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return out;
        }
        try {
            // Creating Input Stream
            File file = new File(filename);
            FileInputStream myInput = new FileInputStream(file.getAbsolutePath());
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            Workbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = (HSSFSheet) myWorkBook.getSheetAt(group);

            /** We now need something to iterate through the cells.**/
            out = Subject.process(mySheet);
        } catch (Exception e) {
            String a = e.toString();
            Log.e("WTFuck", a);
        }


        return out;
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }
}
