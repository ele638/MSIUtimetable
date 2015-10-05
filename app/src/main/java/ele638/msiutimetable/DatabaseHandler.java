package ele638.msiutimetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String
            KEY_ID = "id",
            KEY_SUBJECT = "subject",
            KEY_TEACHER = "teacher",
            KEY_TYPE = "type",
            KEY_PLACE = "place";


    // Инициализация базы. Вызывается, когда нужно работать с базой. Пример: DatabaseHandler db = new DatabaseHandler(getApplicationContext(), "141132");
    public DatabaseHandler(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Служебный метод.
    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 0; i < 12; i++) {
            String query = "CREATE TABLE " + getTableName(i) + " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SUBJECT + " TEXT," + KEY_TEACHER + " TEXT," + KEY_TYPE + " TEXT," + KEY_PLACE + " TEXT)";
            Log.d("QUERY", query);
            db.execSQL(query);
        }
    }

    //Служебный метод.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = 0; i < 12; i++)
            db.execSQL("DROP TABLE IF EXISTS " + getTableName(i));
        onCreate(db);
    }

    //Добавить объект subject в день day. Дни считаются от 1 до 12 включительно, т.е. 6 дней числителя, за ними 6 дней знаменателя. Порядковый номер предмета будет взят
    //из самого subject, и будет преобразован в номер от одного до 8 включительно (так он хранится только в базе).
    //Этот метод можно вызвать только в том случае, если в базе на этой позиции еще НЕТ записи. Иначе ничего не произойдет.
    public void addSubject(Subject subject, int day) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, subject.getOrder());
        values.put(KEY_SUBJECT, subject.subject);
        values.put(KEY_TEACHER, subject.teacher);
        values.put(KEY_TYPE, subject.type);
        values.put(KEY_PLACE, subject.place);
        db.insert(getTableName(day), null, values);

        db.close();
    }

    //Сделать объект типа Subject на основе записи в базе. Если запись пустая, будет возвращен null.
    public Subject getSubject(int day, int order) {
        SQLiteDatabase db = getReadableDatabase();
        Subject subject;
        //Cursor cursor = db.query(getTableName(day), new String[] { KEY_ID, KEY_SUBJECT, KEY_TEACHER, KEY_TYPE, KEY_PLACE}, KEY_ID + "=?", new String[] { String.valueOf(order) }, null, null, null, null );
        Cursor cursor = db.rawQuery("SELECT * FROM " + getTableName(day) + " WHERE id=" + Integer.toString(order) + ";", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        try {
            subject = new Subject(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(0));
        } catch (Exception e) {
            Log.d("NO DATA", "Нет записи в этой строчке");
            subject = null;
        }
        db.close();
        cursor.close();
        return subject;
    }

    //ОБНОВИТЬ запись в дне day. Полностью аналогична addSubject, но будет работать только если запись УЖЕ есть.
    public void updateSubject(Subject subject, int day) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, subject.getOrder());
        values.put(KEY_SUBJECT, subject.subject);
        values.put(KEY_TEACHER, subject.teacher);
        values.put(KEY_TYPE, subject.type);
        values.put(KEY_PLACE, subject.place);
        db.update(getTableName(day), values, KEY_ID + "=?", new String[]{String.valueOf(subject.getOrder())});
        db.close();
    }


    //Универсальная функция записи. Обновит запись или добавит новую, если ее еще не было.
    public void setSubject(Subject subject, int day) {
        if (getSubject(day, subject.getOrder()) == null)
            addSubject(subject, day);
        else
            updateSubject(subject, day);
    }

    //Получить объект типа Day из базы. Аргумент day от 1 до 12 включительно.
    public Day getDay(int day) {
        int _day = day < 6 ? day : day - 6;
        Day d = new Day(_day);
        Subject subject;
        for (int i = 0; i < 8; i++) {
            if ((subject = getSubject(day, i)) != null)
                d.add(subject);
        }
        if (d.isEmpty())
            return null;
        else
            return d;
    }

    public Day getAllDay(int day) {
        int _day = day < 6 ? day : day - 6;
        Day d = new Day(_day);
        for (int i = 0; i < 8; i++) d.add(getSubject(day, i));
        return d;
    }

    public Week getWeek(int weekType) {
        int i = weekType * 6;
        int j = i + 6;
        Week week = new Week(weekType);
        Day day;
        for (; i < j; i++)
            if ((day = getDay(i)) != null)
                week.add(day);
        if (week.isEmpty())
            return null;
        else
            return week;
    }

    public Week getAllWeek(int weekType) {
        int i = weekType * 6;
        int j = i + 6;
        Week week = new Week(weekType);
        for (; i < j; i++) week.add(getAllDay(i));
        return week;
    }


    public ArrayList<Week> getWeeks() {
        ArrayList<Week> out = new ArrayList<>();
        out.add(getWeek(0));
        out.add(getWeek(1));
        return out;
    }

    public ArrayList<Week> getAllWeeks() {
        ArrayList<Week> out = new ArrayList<>();
        out.add(getAllWeek(0));
        out.add(getAllWeek(1));
        return out;
    }

    //Полностью удалить запись order из дня day.
    public void deleteSubject(int day, int order) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + getTableName(day) + " WHERE id=" + order + ";");
        db.close();
    }

    public void deleteSubject(Subject subject, int day) {
        deleteSubject(day, subject.getOrder());
    }

    //Удалить все записи в дне day целиком.
    public void deleteDay(int day) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + getTableName(day) + ";");
        db.close();
    }

    //Удалить содержимое всей базы.
    public void deleteAll() {
        for (int i = 0; i < 12; i++)
            deleteDay(i);
    }

    //Служебная функция.
    private String getTableName(int day) {
        return "DAY" + Integer.toString(day);
    }
}