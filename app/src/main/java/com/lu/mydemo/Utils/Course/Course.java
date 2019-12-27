package com.lu.mydemo.Utils.Course;

import java.util.Arrays;

public class Course {
    private int id;               //课程ID
    private String courseName;    //课程名称
    private String lessionTeacher;//授课教师
    private CourseSchedule[] courseSchedule;//课程时间块

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getLessionTeacher() {
        return lessionTeacher;
    }

    public void setLessionTeacher(String lessionTeacher) {
        this.lessionTeacher = lessionTeacher;
    }

    public CourseSchedule[] getCourseShedule() {
        return courseSchedule;
    }

    public void setCourseShedule(CourseSchedule[] courseShedule) {
        this.courseSchedule = courseShedule;
    }

    @Override
    public String toString() {
        return "com.lu.mydemo.Utils.Course{" +
                "id=" + id +
                ", courseName='" + courseName + '\'' +
                ", lessonTeacher='" + lessionTeacher + '\'' +
                ", courseSchedule=" + Arrays.toString(courseSchedule) +
                "}\n";
    }
}
