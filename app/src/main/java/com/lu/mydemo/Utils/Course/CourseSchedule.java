package com.lu.mydemo.Utils.Course;

public class CourseSchedule {
    private int startWeek;  //开始周
    private int endWeek;    //结束周
    private int TAG;           //全-0、单-1、双-2 周标志
    private int lessonWeek;   //星期几上课
    private int lessonIndex;  //课程节数
    private String classroomName; //教室

    public int getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    public int getTAG() {
        return TAG;
    }

    public void setTAG(int TAG) {
        this.TAG = TAG;
    }

    public int getLessonWeek() {
        return lessonWeek;
    }

    public void setLessonWeek(int lessionWeek) {
        this.lessonWeek = lessionWeek;
    }

    public int getLessonIndex() {
        return lessonIndex;
    }

    public void setLessonIndex(int lessionIndex) {
        this.lessonIndex = lessionIndex;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    @Override
    public String toString() {
        return "\nCourseSchedule{" +
                "startWeek=" + startWeek +
                ", endWeek=" + endWeek +
                ", TAG=" + TAG +
                ", lessonWeek=" + lessonWeek +
                ", lessonIndex=" + lessonIndex +
                ", classroomName='" + classroomName + '\'' +
                "}\n";
    }
}
