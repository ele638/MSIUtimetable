package ele638.msiutimetable;

import android.os.Environment;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by ele638 on 23.09.15.
 */
public class Parsing {

    public static ArrayList<String> readCourses(String filename){
        ArrayList<String> out = new ArrayList<String>();
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

            for (int i=0; i<myWorkBook.getNumberOfSheets(); i++){
                out.add(myWorkBook.getSheetName(i));
            }

        } catch (Exception e) {
            String a = e.toString();
            Log.e("WTFuck", a);
        }

        return out;
    }

    public static ArrayList<String> readGroups(String filename, int inCourse) {
        ArrayList<String> out = new ArrayList<String>();
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
            HSSFSheet mySheet = (HSSFSheet) myWorkBook.getSheetAt(inCourse);

            /** We now need something to iterate through the cells.**/
            HSSFRow myRow = mySheet.getRow(0);
            for (int i=2; i<myRow.getLastCellNum(); i+=2){
                out.add(myRow.getCell(i).toString().replace(".0", ""));
            }

        } catch (Exception e) {
            String a = e.toString();
            Log.e("WTFuck", a);
        }

        return out;
    }

    public static ArrayList<ArrayList> readExcelFile(String filename,int course, int group) {
        ArrayList<ArrayList> out = new ArrayList<ArrayList>();
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
            HSSFSheet mySheet = (HSSFSheet) myWorkBook.getSheetAt(course);

            /** We now need something to iterate through the cells.**/
            out = Subject.process(mySheet, group);
        } catch (Exception e) {
            String a = e.toString();
            Log.e("WTFuck", a);
        }

        return out;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}
