package UIMSTool;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import Course.Course;
import Course.CourseSchedule;

public class CourseJSONTransfer {

    public static Course[] courses;

    public static boolean transfer(JSONObject courseJSON){

        try {

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

                    try{
                        weekOddEven = timeBlock.getString("weekOddEven");
                    }catch (Exception e){
//                    e.printStackTrace();
                    }

                    CourseSchedule courseShedule = new CourseSchedule();
                    courseShedule.setStartWeek(beginWeek);
                    courseShedule.setEndWeek(endWeek);
                    courseShedule.setLessonWeek(dayOfWeek);
                    courseShedule.setLessonIndex(classSet);
                    courseShedule.setClassroomName(classroomName);

                    switch (weekOddEven.toUpperCase()){
                        case "":{
                            courseShedule.setTAG(0);
                            break;
                        }
                        case "E":{
                            //双周
                            courseShedule.setTAG(2);
                            break;
                        }
                        case "O":{
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
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
