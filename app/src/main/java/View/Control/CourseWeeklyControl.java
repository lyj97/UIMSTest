package View.Control;

import android.content.Context;
import android.view.View;

import com.lu.mydemo.R;

import java.util.List;

import Utils.Course.MySubject;

public class CourseWeeklyControl {

    private Context context;
    private View view;

    public CourseWeeklyControl(View view, Context context){
        this.context = context;
        this.view = view;
    }

    public void init(List<MySubject> courseList){
        for(MySubject course : courseList){
            int end_pos = course.getStart() + course.getStep() - 1;
            switch (course.getDay()){
                case 1 :{
                    switch (course.getStart()){
                        case 1 :{
                            view.findViewById(R.id.course_weekly_1_1).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(1 == end_pos) break;
                        }
                        case 2 :{
                            view.findViewById(R.id.course_weekly_1_2).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(2 == end_pos) break;
                        }
                        case 3 :{
                            view.findViewById(R.id.course_weekly_1_3).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(3 == end_pos) break;
                        }
                        case 4 :{
                            view.findViewById(R.id.course_weekly_1_4).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(4 == end_pos) break;
                        }
                        case 5 :{
                            view.findViewById(R.id.course_weekly_1_5).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(5 == end_pos) break;
                        }
                        case 6 :{
                            view.findViewById(R.id.course_weekly_1_6).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(6 == end_pos) break;
                        }
                        case 7 :{
                            view.findViewById(R.id.course_weekly_1_7).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(7 == end_pos) break;
                        }
                        case 8 :{
                            view.findViewById(R.id.course_weekly_1_8).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(8 == end_pos) break;
                        }
                        case 9 :{
                            view.findViewById(R.id.course_weekly_1_9).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(9 == end_pos) break;
                        }
                    }
                    break;
                }
                case 2 :{
                    switch (course.getStart()){
                        case 1 :{
                            view.findViewById(R.id.course_weekly_2_1).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(1 == end_pos) break;
                        }
                        case 2 :{
                            view.findViewById(R.id.course_weekly_2_2).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(2 == end_pos) break;
                        }
                        case 3 :{
                            view.findViewById(R.id.course_weekly_2_3).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(3 == end_pos) break;
                        }
                        case 4 :{
                            view.findViewById(R.id.course_weekly_2_4).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(4 == end_pos) break;
                        }
                        case 5 :{
                            view.findViewById(R.id.course_weekly_2_5).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(5 == end_pos) break;
                        }
                        case 6 :{
                            view.findViewById(R.id.course_weekly_2_6).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(6 == end_pos) break;
                        }
                        case 7 :{
                            view.findViewById(R.id.course_weekly_2_7).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(7 == end_pos) break;
                        }
                        case 8 :{
                            view.findViewById(R.id.course_weekly_2_8).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(8 == end_pos) break;
                        }
                        case 9 :{
                            view.findViewById(R.id.course_weekly_2_9).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(9 == end_pos) break;
                        }
                    }
                    break;
                }
                case 3 :{
                    switch (course.getStart()){
                        case 1 :{
                            view.findViewById(R.id.course_weekly_3_1).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(1 == end_pos) break;
                        }
                        case 2 :{
                            view.findViewById(R.id.course_weekly_3_2).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(2 == end_pos) break;
                        }
                        case 3 :{
                            view.findViewById(R.id.course_weekly_3_3).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(3 == end_pos) break;
                        }
                        case 4 :{
                            view.findViewById(R.id.course_weekly_3_4).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(4 == end_pos) break;
                        }
                        case 5 :{
                            view.findViewById(R.id.course_weekly_3_5).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(5 == end_pos) break;
                        }
                        case 6 :{
                            view.findViewById(R.id.course_weekly_3_6).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(6 == end_pos) break;
                        }
                        case 7 :{
                            view.findViewById(R.id.course_weekly_3_7).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(7 == end_pos) break;
                        }
                        case 8 :{
                            view.findViewById(R.id.course_weekly_3_8).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(8 == end_pos) break;
                        }
                        case 9 :{
                            view.findViewById(R.id.course_weekly_3_9).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(9 == end_pos) break;
                        }
                    }
                    break;
                }
                case 4 :{
                    switch (course.getStart()){
                        case 1 :{
                            view.findViewById(R.id.course_weekly_4_1).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(1 == end_pos) break;
                        }
                        case 2 :{
                            view.findViewById(R.id.course_weekly_4_2).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(2 == end_pos) break;
                        }
                        case 3 :{
                            view.findViewById(R.id.course_weekly_4_3).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(3 == end_pos) break;
                        }
                        case 4 :{
                            view.findViewById(R.id.course_weekly_4_4).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(4 == end_pos) break;
                        }
                        case 5 :{
                            view.findViewById(R.id.course_weekly_4_5).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(5 == end_pos) break;
                        }
                        case 6 :{
                            view.findViewById(R.id.course_weekly_4_6).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(6 == end_pos) break;
                        }
                        case 7 :{
                            view.findViewById(R.id.course_weekly_4_7).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(7 == end_pos) break;
                        }
                        case 8 :{
                            view.findViewById(R.id.course_weekly_4_8).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(8 == end_pos) break;
                        }
                        case 9 :{
                            view.findViewById(R.id.course_weekly_4_9).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(9 == end_pos) break;
                        }
                    }
                    break;
                }
                case 5 :{
                    switch (course.getStart()){
                        case 1 :{
                            view.findViewById(R.id.course_weekly_5_1).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(1 == end_pos) break;
                        }
                        case 2 :{
                            view.findViewById(R.id.course_weekly_5_2).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(2 == end_pos) break;
                        }
                        case 3 :{
                            view.findViewById(R.id.course_weekly_5_3).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(3 == end_pos) break;
                        }
                        case 4 :{
                            view.findViewById(R.id.course_weekly_5_4).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(4 == end_pos) break;
                        }
                        case 5 :{
                            view.findViewById(R.id.course_weekly_5_5).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(5 == end_pos) break;
                        }
                        case 6 :{
                            view.findViewById(R.id.course_weekly_5_6).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(6 == end_pos) break;
                        }
                        case 7 :{
                            view.findViewById(R.id.course_weekly_5_7).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(7 == end_pos) break;
                        }
                        case 8 :{
                            view.findViewById(R.id.course_weekly_5_8).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(8 == end_pos) break;
                        }
                        case 9 :{
                            view.findViewById(R.id.course_weekly_5_9).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(9 == end_pos) break;
                        }
                    }
                    break;
                }
                case 6 :{
                    switch (course.getStart()){
                        case 1 :{
                            view.findViewById(R.id.course_weekly_6_1).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(1 == end_pos) break;
                        }
                        case 2 :{
                            view.findViewById(R.id.course_weekly_6_2).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(2 == end_pos) break;
                        }
                        case 3 :{
                            view.findViewById(R.id.course_weekly_6_3).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(3 == end_pos) break;
                        }
                        case 4 :{
                            view.findViewById(R.id.course_weekly_6_4).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(4 == end_pos) break;
                        }
                        case 5 :{
                            view.findViewById(R.id.course_weekly_6_5).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(5 == end_pos) break;
                        }
                        case 6 :{
                            view.findViewById(R.id.course_weekly_6_6).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(6 == end_pos) break;
                        }
                        case 7 :{
                            view.findViewById(R.id.course_weekly_6_7).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(7 == end_pos) break;
                        }
                        case 8 :{
                            view.findViewById(R.id.course_weekly_6_8).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(8 == end_pos) break;
                        }
                        case 9 :{
                            view.findViewById(R.id.course_weekly_6_9).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(9 == end_pos) break;
                        }
                    }
                    break;
                }
                case 7 :{
                    switch (course.getStart()){
                        case 1 :{
                            view.findViewById(R.id.course_weekly_7_1).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(1 == end_pos) break;
                        }
                        case 2 :{
                            view.findViewById(R.id.course_weekly_7_2).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(2 == end_pos) break;
                        }
                        case 3 :{
                            view.findViewById(R.id.course_weekly_7_3).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(3 == end_pos) break;
                        }
                        case 4 :{
                            view.findViewById(R.id.course_weekly_7_4).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(4 == end_pos) break;
                        }
                        case 5 :{
                            view.findViewById(R.id.course_weekly_7_5).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(5 == end_pos) break;
                        }
                        case 6 :{
                            view.findViewById(R.id.course_weekly_7_6).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(6 == end_pos) break;
                        }
                        case 7 :{
                            view.findViewById(R.id.course_weekly_7_7).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(7 == end_pos) break;
                        }
                        case 8 :{
                            view.findViewById(R.id.course_weekly_7_8).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(8 == end_pos) break;
                        }
                        case 9 :{
                            view.findViewById(R.id.course_weekly_7_9).setBackground(context.getDrawable(R.drawable.background_course_weekly_item_active));
                            if(9 == end_pos) break;
                        }
                    }
                    break;
                }
            }
        }
    }

}
