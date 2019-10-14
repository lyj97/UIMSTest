package Utils.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import Utils.Course.MySubject;

public class MyCourseDBHelper extends SQLiteOpenHelper {

    private static String TABLE_NAME = "course";

    public MyCourseDBHelper(@Nullable Context context, @Nullable String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //执行有更新行为的sql语句
        db.execSQL("CREATE Table " + TABLE_NAME + " (" +
                "id integer primary key autoincrement, " +
                "course_id integer, " +
                "course_name varchar(20), " +
                "room varchar(20), " +
                "teacher varchar(5), " +
                "week_list varchar(30), " +
                "weekOddEven varchar(1), " +
                "start_index integer, " +
                "step integer, " +
                "day integer, " +
                "term varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<MySubject> getAllCourse(SQLiteDatabase db){
        ArrayList<MySubject> list = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY course_id", null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast() && (cursor.getString(1) != null)){
                MySubject subject = new MySubject();
                subject.setDb_id(cursor.getInt(0));
                subject.setId(cursor.getInt(1));
                subject.setName(cursor.getString(2));
                subject.setRoom(cursor.getString(3));
                subject.setTeacher(cursor.getString(4));
                subject.setWeekList(getWeekList(cursor.getString(5)));
                subject.setWeekOddEven(cursor.getString(6));
                subject.setStart(cursor.getInt(7));
                subject.setStep(cursor.getInt(8));
                subject.setDay(cursor.getInt(9));
                subject.setTerm(cursor.getString(10));
                subject.setWeekRange(subject.getWeekList().get(0), subject.getWeekList().get(subject.getWeekList().size() - 1), subject.getWeekOddEven(), subject.getDay());
                subject.setStepRange();
                list.add(subject);
                cursor.moveToNext();
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public void saveAll(SQLiteDatabase db, List<MySubject> list, boolean deletePrevious){
        if(list == null || !(list.size() > 0)) return;
        if(deletePrevious) db.execSQL("DELETE FROM " + TABLE_NAME);
        for(MySubject subject : list){
            db.execSQL("INSERT INTO " + TABLE_NAME +
                    " (course_id, course_name, room, teacher, week_list, weekOddEven, start_index, step, day, term) " +
                    "VALUES " +
                    "(\"" +
                    subject.getId() + "\",\"" +
                    subject.getName() + "\",\"" +
                    subject.getRoom() + "\",\"" +
                    subject.getTeacher() + "\",\"" +
                    transWeekList(subject.getWeekList()) + "\",\"" +
                    subject.getWeekOddEven() + "\",\"" +
                    subject.getStart() + "\",\"" +
                    subject.getStep() + "\",\"" +
                    subject.getDay() + "\",\"" +
                    subject.getTerm() + "\")");
        }
    }

    public String transWeekList(List<Integer> list){
        StringBuilder builder = new StringBuilder();
        for(int num : list){
            builder.append(num).append("-");
        }
        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    public ArrayList<Integer> getWeekList(String str){
        String[] numStrs = str.split("-");
        if(!(numStrs.length > 0)) return null;
        ArrayList<Integer> list = new ArrayList<>();
        for(String s : numStrs){
            list.add(Integer.parseInt(s));
        }
        return list;
    }

}
