package Utils.Rom;

import java.io.IOException;

//检测EMUI
public final class EMUIUtils {

    private static final String KEY_EMUI_VERSION_CODE = "ro.build.hw_emui_api_level";

    public static boolean isEMUI() {
        try {
            //BuildProperties 是一个工具类，下面会给出代码
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_EMUI_VERSION_CODE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }
}
