package UIMSTool;

import java.util.HashMap;

public class ClassSetConvert {

    private static HashMap<Integer, Integer> lessonIndex_lessonValue = new HashMap<>();
    private static HashMap<Integer, int[]> lessonValue_lessonIndex = new HashMap<>();

    public ClassSetConvert(){

        lessonIndex_lessonValue.put(1, getTwoNthPower(1));
        lessonIndex_lessonValue.put(2, getTwoNthPower(2));
        lessonIndex_lessonValue.put(3, getTwoNthPower(3));
        lessonIndex_lessonValue.put(4, getTwoNthPower(4));
        lessonIndex_lessonValue.put(5, getTwoNthPower(5));
        lessonIndex_lessonValue.put(6, getTwoNthPower(6));
        lessonIndex_lessonValue.put(7, getTwoNthPower(7));
        lessonIndex_lessonValue.put(8, getTwoNthPower(8));
        lessonIndex_lessonValue.put(9, getTwoNthPower(9));
        lessonIndex_lessonValue.put(10, getTwoNthPower(10));
        lessonIndex_lessonValue.put(11, getTwoNthPower(11));

    }

    public static int[] mathStartEnd(int lessonValue){
        if(lessonValue_lessonIndex.containsKey(lessonValue)){
            return lessonValue_lessonIndex.get(lessonValue);
        }
        int nowIessonValue;
        for(int s=1; s<=11; s++){
            for(int e=s; e<=11; e++){
                nowIessonValue = getIndexSum(s,e);
                lessonValue_lessonIndex.put(nowIessonValue, new int[]{s,e});
                if(nowIessonValue == lessonValue){
                    return new int[]{s,e};
                }
            }
        }
        return null;
    }

    private static int getIndexSum(int start, int end){
        int value = 0;
        for(int i=start; i<=end; i++){
            try {
                value += lessonIndex_lessonValue.get(i);
            }
            catch (Exception e){
                System.out.println("i:\t" + i);
                System.out.println(lessonIndex_lessonValue);
                e.printStackTrace();
                System.exit(-1);
            }
        }
        return value;
    }

    private static int getTwoNthPower(int n){
        int value = 1;
        for(int i=0; i<n; i++){
            value *= 2;
        }
        return value;
    }

}
