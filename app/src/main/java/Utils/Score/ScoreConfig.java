package Utils.Score;

import android.content.Context;
import android.content.SharedPreferences;

public class ScoreConfig {

    static SharedPreferences sp;

    static boolean isCJCXEnable = true;
    static boolean isUIMSEnable = true;

    public static void loadScoreConfig(Context context){
        if(sp == null){
            sp = context.getSharedPreferences("ScoreConfig", Context.MODE_PRIVATE);
            isCJCXEnable = sp.getBoolean("CJCXEnable", true);
            isUIMSEnable = sp.getBoolean("UIMSEnable", true);
        }
    }

    public static void setCJCXEnable(Context context, boolean enable){
        if(sp == null){
            sp = context.getSharedPreferences("ScoreConfig", Context.MODE_PRIVATE);
        }
        try {
            ScoreConfig.isCJCXEnable = enable;
            sp.edit().putBoolean("CJCXEnable", ScoreConfig.isCJCXEnable).apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void setUIMSEnable(Context context, boolean enable){
        if(sp == null){
            sp = context.getSharedPreferences("ScoreConfig", Context.MODE_PRIVATE);
        }
        try {
            ScoreConfig.isUIMSEnable = enable;
            sp.edit().putBoolean("UIMSEnable", ScoreConfig.isUIMSEnable).apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isIsCJCXEnable() {
        return isCJCXEnable;
    }

    public static boolean isIsUIMSEnable() {
        return isUIMSEnable;
    }
}
