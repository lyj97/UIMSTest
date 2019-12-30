package com.lu.mydemo.UIMSTool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.lu.mydemo.UIMS.UIMS;
import com.lu.mydemo.Utils.Course.Course;
import com.lu.mydemo.Utils.Course.CourseSchedule;
import com.lu.mydemo.Utils.Course.MySubject;
import com.lu.mydemo.Utils.Database.MyCourseDBHelper;
import com.lu.mydemo.Utils.Thread.MyThreadController;

public class CourseJSONTransfer {

    public static Course[] courses;
    public static ArrayList<MySubject> courseList;

    public static List<Exception> exceptionList = new ArrayList<>();

    public static boolean transfer(JSONObject courseJSON) {


        JSONArray json_courses = courseJSON.getJSONArray("value");

        JSONObject teachClassMaster;

        JSONArray lessonSchedules;
        JSONArray lessonTeachers;
        JSONObject teacher;
        String teacherName;
        JSONObject lessonSegment;
        String courName;

        JSONObject timeBlock;
        int classSet;
        int dayOfWeek;
        int beginWeek;
        int endWeek;
        int[] start_end;
        String weekOddEven = "";
        JSONObject classroom;
        String classroomName;

        courses = new Course[json_courses.size()];

        for (int i = 0; i < json_courses.size(); i++) {

            teachClassMaster = json_courses.getJSONObject(i).getJSONObject("teachClassMaster");

            lessonSegment = teachClassMaster.getJSONObject("lessonSegment");
            lessonSchedules = teachClassMaster.getJSONArray("lessonSchedules");
            lessonTeachers = teachClassMaster.getJSONArray("lessonTeachers");

            courName = lessonSegment.getString("fullName");

            teacherName = lessonTeachers.getJSONObject(0).getJSONObject("teacher").getString("name");

            Course course = new Course();
            course.setId(lessonSegment.getInt("lssgId"));
            course.setCourseName(courName);
            course.setLessionTeacher(teacherName);

            CourseSchedule[] courseShedules = new CourseSchedule[lessonSchedules.size()];

            for (int j = 0; j < lessonSchedules.size(); j++) {

                timeBlock = lessonSchedules.getJSONObject(j).getJSONObject("timeBlock");
                classroom = lessonSchedules.getJSONObject(j).getJSONObject("classroom");
                classroomName = classroom.getString("fullName");

                classSet = timeBlock.getInt("classSet");
                dayOfWeek = timeBlock.getInt("dayOfWeek");
                beginWeek = timeBlock.getInt("beginWeek");
                endWeek = timeBlock.getInt("endWeek");

                try {
                    weekOddEven = timeBlock.getString("weekOddEven");
                } catch (Exception e) {
//                    e.printStackTrace();
                }

                CourseSchedule courseShedule = new CourseSchedule();
                courseShedule.setStartWeek(beginWeek);
                courseShedule.setEndWeek(endWeek);
                courseShedule.setLessonWeek(dayOfWeek);
                courseShedule.setLessonIndex(classSet);
                courseShedule.setClassroomName(classroomName);

                switch (weekOddEven.toUpperCase()) {
                    case "": {
                        courseShedule.setTAG(0);
                        break;
                    }
                    case "E": {
                        //双周
                        courseShedule.setTAG(2);
                        break;
                    }
                    case "O": {
                        //单周
                        courseShedule.setTAG(1);
                        break;
                    }
                }

                courseShedules[j] = courseShedule;
                weekOddEven = "";

            }

            course.setCourseShedule(courseShedules);
            courses[i] = course;

        }

        return true;

    }

    public synchronized static boolean insertCourseTime(Context context, MySubject subject){
        final MyCourseDBHelper dbHelper = new MyCourseDBHelper(context, "Course_DB", null, 1);
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            dbHelper.insertCourseTime(db, subject);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public synchronized static boolean updateCourse(Context context, MySubject subject){
        final MyCourseDBHelper dbHelper = new MyCourseDBHelper(context, "Course_DB", null, 1);
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            dbHelper.updateCourse(db, subject);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public synchronized static boolean deleteCourse(Context context, MySubject subject){
        final MyCourseDBHelper dbHelper = new MyCourseDBHelper(context, "Course_DB", null, 1);
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            dbHelper.deleteCourse(db, subject);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public synchronized static boolean transferCourseList(Context context){
        return transferCourseList(context, null, false);
    }

    public synchronized static boolean transferCourseList(Context context, JSONObject courseJSON){
        return transferCourseList(context, courseJSON, false);
    }

    public synchronized static boolean transferCourseList(Context context, JSONObject courseJSON, boolean forceFlush){
        if(!forceFlush && courseList!= null && courseList.size() > 0) {
            Log.w("CourseJSONTransfer", "Ignored course load!");
            return true;
        }
        if(courseJSON == null) {
            courseJSON = UIMS.getCourseJSON();
            if(courseJSON == null) {
                exceptionList.add(new IllegalAccessException("CourseJSON is NULL!"));
                return false;
            }
        }
        final MyCourseDBHelper dbHelper = new MyCourseDBHelper(context, "Course_DB", null, 1);
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        courseList = dbHelper.getAllCourse(db);
        if(courseList == null || courseList.size() == 0) {//尝试重新加载
            if(courseList != null) Log.e("CourseJSONTransfer", "Err when get course from db!");
            transferCourseList(UIMS.getCourseJSON());
            //TODO DB TEST
            MyThreadController.commit(new Runnable() {
                @Override
                public void run() {
                    dbHelper.saveAll(db, courseList, true);
                    Log.i("CourseJSONTransfer", "Course:" + dbHelper.getAllCourse(db));
                    db.close();
                }
            });
        }
        else db.close();
        return true;
    }

    public synchronized static boolean transferCourseList(JSONObject courseJSON){
        return transferCourseList(courseJSON, false);
    }

    public synchronized static boolean transferCourseList(JSONObject courseJSON, boolean forceFlush) {

        if (!forceFlush && courseList != null && courseList.size() > 0) return true;

        courseList = new ArrayList<>();

        JSONArray json_courses = courseJSON.getJSONArray("value");

        JSONObject teachClassMaster;

        JSONArray lessonSchedules;
        JSONArray lessonTeachers;
        JSONObject teacher;
        String teacherName;
        JSONObject lessonSegment;
        String courName;

        JSONObject timeBlock;
        int classSet;
        int dayOfWeek;
        int beginWeek;
        int endWeek;
        int[] start_end;
        String weekOddEven = "";
        JSONObject classroom;
        String classroomName;

        for (int i = 0; i < json_courses.size(); i++) {

            teachClassMaster = json_courses.getJSONObject(i).getJSONObject("teachClassMaster");

            lessonSegment = teachClassMaster.getJSONObject("lessonSegment");
            lessonSchedules = teachClassMaster.getJSONArray("lessonSchedules");
            lessonTeachers = teachClassMaster.getJSONArray("lessonTeachers");

            courName = lessonSegment.getString("fullName");

            teacherName = lessonTeachers.getJSONObject(0).getJSONObject("teacher").getString("name");

            for (int j = 0; j < lessonSchedules.size(); j++) {

                MySubject subject = new MySubject();
                subject.setId(lessonSegment.getInt("lssgId"));
                subject.setName(courName);
                subject.setTeacher(teacherName);

                timeBlock = lessonSchedules.getJSONObject(j).getJSONObject("timeBlock");
                classroom = lessonSchedules.getJSONObject(j).getJSONObject("classroom");
                classroomName = classroom.getString("fullName");

                classSet = timeBlock.getInt("classSet");
                dayOfWeek = timeBlock.getInt("dayOfWeek");
                beginWeek = timeBlock.getInt("beginWeek");
                endWeek = timeBlock.getInt("endWeek");

                try {
                    weekOddEven = timeBlock.getString("weekOddEven");
                } catch (Exception e) {
//                    e.printStackTrace();
                }

                start_end = ClassSetConvert.mathStartEnd(classSet);

                subject.setWeekList(getWeekList(beginWeek, endWeek, weekOddEven));
                subject.setWeekRange(beginWeek, endWeek, weekOddEven, dayOfWeek);
                subject.setDay(dayOfWeek);
                subject.setRoom(classroomName);
                subject.setStart(start_end[0]);
                subject.setStep(start_end[1] - start_end[0] + 1);
                subject.setStepRange();

                courseList.add(subject);
                weekOddEven = "";

            }

        }

        return true;

    }

    public static List<Integer> getWeekList(int beginWeek, int endWeek, String weekOddEven){
        List<Integer> weekList=new ArrayList<>();

        switch (weekOddEven.toUpperCase()){
            case "":{
                for(int i=beginWeek; i<=endWeek; i++){
                    weekList.add(i);
                }
                break;
            }
            case "E":{
                //双周
                for(int i=beginWeek; i<=endWeek; i++){
                    if(i % 2 == 0) weekList.add(i);
                }
                break;
            }
            case "O":{
                //单周
                for(int i=beginWeek; i<=endWeek; i++){
                    if(i % 2 != 0) weekList.add(i);
                }
                break;
            }
        }

        return weekList;
    }

    public static List<Exception> getExceptionList() {
        return exceptionList;
    }
}
