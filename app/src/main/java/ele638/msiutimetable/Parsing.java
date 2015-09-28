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

    public static ArrayList<String> readCourses(File filename) {
        ArrayList<String> out = new ArrayList<>();
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return out;
        }
        try {
            // Creating Input Stream
            File file = new File(filename.getAbsolutePath());
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

    public static ArrayList<String> readGroups(File filename, int inCourse) {
        ArrayList<String> out = new ArrayList<>();
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return out;
        }
<<<<<<< HEAD
        try {
            // Creating Input Stream
            File file = new File(filename.getAbsolutePath());
            FileInputStream myInput = new FileInputStream(file.getAbsolutePath());
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
=======
        return out;
    }

    public static ArrayList<Week> readExcelFile() {
        HSSFSheet mySheet = (HSSFSheet) myWorkBook.getSheetAt(MainActivity.SAVED_COURSE);
        return processMSIU(mySheet, MainActivity.SAVED_TIME, MainActivity.SAVED_GROUP);
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }
>>>>>>> origin/master

            // Create a workbook using the File System
            Workbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = (HSSFSheet) myWorkBook.getSheetAt(inCourse);

<<<<<<< HEAD
            /** We now need something to iterate through the cells.**/
            HSSFRow myRow = mySheet.getRow(0);
            for (int i=2; i<myRow.getLastCellNum(); i+=2){
                out.add(myRow.getCell(i).toString().replace(".0", ""));
            }
=======
    public static ArrayList<Week> processMSIU(HSSFSheet mySheet, int time, int groupnum) {
        int rowPos = 1;
        Week weekdaych = new Week(0);
        Week weekdaynech = new Week(1);
        ArrayList<Week> week = new ArrayList<>();
        //Парсер для дневной формы обучения
        if (time == 0) {
            //Идем по всей неделе
            for (int i = 0; i < 6; i++) {
                //Идем по дням
                Day daych = new Day(i);
                Day daynech = new Day(i);
                for (int j = 0; j < 6; j++) {
                    //Идем по часам, сначала первые две строчки - четные дни
                    for (int k = 0; k < 2; k++) {
                        Boolean flag = (exept(mySheet.getRow(rowPos), groupnum) == 1);
                        if (!flag) {
                            switch (k) {
                                case 0:
                                    daych.add(analyze(j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum));
                                    Log.d("NORM", "Добавлена пара в четный день");
                                    break;
                                case 1:
                                    daynech.add(analyze(j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum));
                                    Log.d("NORM", "Добавлена пара в нечетный день");
                                    break;
                            }
                        }
                        rowPos += 2;
                    }
                }
                weekdaych.add(daych);
                weekdaynech.add(daynech);
            }
            week.add(weekdaych);
            week.add(weekdaynech);
            return week;
        } else
        //Парсер для вечерки
        {
            //Идем по будням
            for (int i = 0; i < 5; i++) {
                Day daych = new Day(i);
                Day daynech = new Day(i);
                //Будни вечерки
                for (int j = 0; j < 2; j++) {
                    //Идем по часам, сначала первые две строчки - четные дни
                    for (int k = 0; k < 2; k++) {
                        Boolean flag = (exept(mySheet.getRow(rowPos), groupnum) == 1);
                        if (!flag) {
                            switch (k) {
                                case 0:
                                    daych.add(analyze((6 + j), mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum));
                                    Log.d("NORM", "Добавлена пара в четный день");
                                    break;
                                case 1:
                                    daynech.add(analyze((6 + j), mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum));
                                    Log.d("NORM", "Добавлена пара в нечетный день");
                                    break;
                            }
                        }
                        rowPos += 2;
                    }
                }
                weekdaych.add(daych);
                weekdaynech.add(daynech);
            }
            //Суббота вечерки
            Day daych = new Day(5);
            Day daynech = new Day(5);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 2; k++) {
                    Boolean flag = (exept(mySheet.getRow(rowPos), groupnum) == 1);
                    if (!flag) {
                        switch (k) {
                            case 0:
                                daych.add(analyze(j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum));
                                Log.d("NORM", "Добавлена пара в четный день");
                                break;
                            case 1:
                                daynech.add(analyze(j, mySheet.getRow(rowPos), mySheet.getRow(rowPos + 1), groupnum));
                                Log.d("NORM", "Добавлена пара в нечетный день");
                                break;
                        }
                    }
                    rowPos += 2;
                }
            }
            weekdaych.add(daych);
            weekdaynech.add(daynech);
            week.add(weekdaych);
            week.add(weekdaynech);
            return week;
        }
    }
>>>>>>> origin/master

        } catch (Exception e) {
            String a = e.toString();
            Log.e("WTFuck", a);
        }

        return out;
    }

    public static ArrayList<ArrayList> readExcelFile(File filename, int group, int course) {
        ArrayList<ArrayList> out = new ArrayList<>();
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return out;
        }
        try {
            // Creating Input Stream
            File file = new File(filename.getAbsolutePath());
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
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }
}
