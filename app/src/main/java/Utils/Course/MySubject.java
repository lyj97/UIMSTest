package Utils.Course;

import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleEnable;

import java.util.Comparator;
import java.util.List;

/**
 * 自定义实体类需要实现ScheduleEnable接口并实现getSchedule()
 *
 * @see ScheduleEnable#getSchedule()
 */
public class MySubject extends Schedule implements ScheduleEnable {

    public static final String[] week = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    public static Comparator<MySubject> timeComparater = new Comparator<MySubject>() {
        @Override
        public int compare(MySubject o1, MySubject o2) {
            if(o1.day != o2.day){
                return o1.day - o2.day;
            }
            else {
                return o1.start - o2.start;
            }
        }
    };

    private int db_id;

    private int id=0;

    /**
     * 课程名
     */
    private String name;

    //无用数据
    private String time;

    /**
     * 教室
     */
    private String room;

    /**
     * 教师
     */
    private String teacher;

    /**
     * 第几周至第几周上
     */
    private List<Integer> weekList;

    /**
     * 单双周标识
     */
    private String weekOddEven;

    /**
     * 周-自然表示
     */
    private String weekRange;

    /**
     * 开始上课的节次
     */
    private int start;

    /**
     * 节-自然表示
     */
    private String stepRange;

    /**
     * 上课节数
     */
    private int step;

    /**
     * 周几上
     */
    private int day;

    private String term;

    /**
     *  一个随机数，用于对应课程的颜色
     */
    private int colorRandom = 0;

    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public MySubject() {
        // TODO Auto-generated constructor stub
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTerm() {
        return term;
    }

    public MySubject(String term,String name, String room, String teacher, List<Integer> weekList, int start, int step, int day, int colorRandom,String time) {
        super();
        this.term=term;
        this.name = name;
        this.room = room;
        this.teacher = teacher;
        this.weekList=weekList;
        this.start = start;
        this.step = step;
        this.day = day;
        this.colorRandom = colorRandom;
        this.time=time;
    }

    public int getDb_id() {
        return db_id;
    }

    public void setDb_id(int db_id) {
        this.db_id = db_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setWeekList(List<Integer> weekList) {
        this.weekList = weekList;
    }

    public List<Integer> getWeekList() {
        return weekList;
    }

    public String getWeekOddEven() {
        return weekOddEven;
    }

    public void setWeekOddEven(String weekOddEven) {
        this.weekOddEven = weekOddEven;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getColorRandom() {
        return colorRandom;
    }

    public void setColorRandom(int colorRandom) {
        this.colorRandom = colorRandom;
    }

    public String getWeekRange() {
        return weekRange;
    }

    public void setWeekRange(int beginWeek, int endWeek, String weekOddEven, int dayOfWeek){
        if(weekOddEven == null) weekOddEven = "";
        if(beginWeek == endWeek) weekRange = "第" + beginWeek + "周";
        else weekRange = "第" + beginWeek + "~" + endWeek + "周";
        switch (weekOddEven.toUpperCase()){
            case "":{
                break;
            }
            case "E":{
                //双周
                weekRange += "[双周]";
                break;
            }
            case "O":{
                //单周
                weekRange += "[单周]";
                break;
            }
        }
        weekRange += " " + week[dayOfWeek];
    }

    public String getStepRange() {
        return stepRange;
    }

    public void setStepRange() {
        if(step == 1) stepRange = "第" + start + "节";
        else stepRange = "第" + start + "~" + (start + step - 1) + "节";
    }

    @Override
    public Schedule getSchedule() {
        Schedule schedule=new Schedule();
        schedule.setDay(getDay());
        schedule.setName(getName());
        schedule.setRoom(getRoom());
        schedule.setStart(getStart());
        schedule.setStep(getStep());
        schedule.setTeacher(getTeacher());
        schedule.setWeekList(getWeekList());
        schedule.setColorRandom(2);
        return schedule;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "MySubject{" +
                "db_id=" + db_id +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", room='" + room + '\'' +
                ", teacher='" + teacher + '\'' +
                ", weekList=" + weekList +
                ", weekOddEven='" + weekOddEven + '\'' +
                ", weekRange='" + weekRange + '\'' +
                ", start=" + start +
                ", stepRange='" + stepRange + '\'' +
                ", step=" + step +
                ", day=" + day +
                ", term='" + term + '\'' +
                ", colorRandom=" + colorRandom +
                ", url='" + url + '\'' +
                '}';
    }
}