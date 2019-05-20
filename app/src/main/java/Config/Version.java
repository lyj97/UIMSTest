package Config;

public class Version {

    private static int versionMajor = 1;
    private static int versionMinor = 2;
    private static int versionPatch = 8;

    public static String getVersionName(){
        return versionMajor + "." + versionMinor + "." + versionPatch;
    }

    public static int getVersionCode(){
        return versionMajor * 10000 + versionMinor * 100 + versionPatch;
    }

}
