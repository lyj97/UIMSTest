package com.lu.mydemo.UIMS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class UIMSTest {

    static String user;
    static String pass;

    static UIMS uims;

    public static void main(String[] args) {
        user = "54160907";
        pass = "225577";

        uims = new UIMS(user, pass);
        System.out.println("正在连接到UIMS教务系统...");
        if (uims.connectToUIMS()) {
            System.out.println("正在登录...");
            if (uims.login()) {
                System.out.println("正在加载用户信息...");
                if (uims.getCurrentUserInfo()) {
                    showAlert("", "欢迎您, " + uims.getNickName() + " ." + "\n" +
                            "您是UIMS系统第 " + uims.getLoginCounter() + " 位访问者.");
                    if (uims.getUserInformation()) ;
                    else {
                        showResponse("Login failed!");
                        return;
                    }
                    if (!uims.getTermArray()) {
                        showResponse("Login failed!");
                        return;
                    }
                    HashSet<String> names = uims.getClassStudentName();
                    System.out.println(names);
                    ArrayList<String> list = uims.post_pingjiao_information();
                    System.out.println(list);
                    String puzzle = uims.post_pingjiao(list.get(0)).getString("puzzle");
                    String[] parts = puzzle.split("_");
                    ArrayList<String> answer = getAnswer(parts, names, puzzle.length());
                    System.out.println(answer);
                    if(answer.size() == 1){
                        String ans = null;
                        char[] name_chars = answer.get(0).toCharArray();
                        char[] puzzle_chars = puzzle.toCharArray();
                        for(int i=0; i<name_chars.length; i++){
                            if(name_chars[i] != puzzle_chars[i]) ans = name_chars[i] + "";
                        }
                        if(ans != null) uims.post_pingjiao_tijiao(list.get(0), ans);
                    }
//                    if (uims.getScoreStatistics()) {
//                        showResponse("查询成绩统计成功！");
//                        showLoading("查询成绩中，请稍侯...");
//                        if (uims.getRecentScore()) {
//                            showResponse("查询成绩成功！");
//                        } else {
//                            showResponse("Login failed!");
//                            return;
//                        }
//                        if (uims.getTermArray()) {
//                            showResponse("查询学期列表成功！");
//                        } else {
//                            showResponse("Login failed!");
//                            return;
//                        }
//                        if (uims.getCourseHistory("135")) {
//                            showResponse("查询历史选课成功！(term: 135)");
//                        } else {
//                            showResponse("Login failed!");
//                            return;
//                        }
//                        if (uims.getCourseHistory("134")) {
//                            showResponse("查询历史选课成功！(term: 134)");
//                        } else {
//                            showResponse("Login failed!");
//                            return;
//                        }
//                        if (uims.getCourseHistory("133")) {
//                            showResponse("查询历史选课成功！(term: 133)");
//                        } else {
//                            showResponse("Login failed!");
//                            return;
//                        }
//                    } else {
//                        showResponse("Login failed!");
//                    }
                } else {
                    showWarningAlert("", "登录失败，请检查用户名和密码是否正确.\n\n" +
                            "教务账号：\t您的教学号\n" +
                            "教务密码：\t默认密码为身份证号后六位");
                }
            } else {
                showWarningAlert("", "登录失败，请检查是否连接校园网！");
            }
        } else {
            showResponse("Login failed!");
        }
    }

    public static ArrayList<String> getAnswer(String[] parts, HashSet<String> names, int length){
        System.out.println("parts:\t" + Arrays.asList(parts));
        ArrayList<String> answer = new ArrayList<>();
        boolean match = true;
        for(String name : names){
            for(int i=0; i<parts.length; i++){
                if(!name.contains(parts[i])){
                    match = false;
                    break;
                }
            }
            if(match && length == name.length()){
                System.out.println("matched:\t" + name);
                answer.add(name);
            }
            match = true;
        }
        return answer;
    }

    public static void showAlert(final String message) {
        System.out.println(message);
    }

    public static void showAlert(final String title, final String message) {
        System.out.println(title + ":\t" + message);
    }

    public static void showWarningAlert(final String message) {
        System.out.println(message);
    }

    public static void showWarningAlert(final String title, final String message) {
        System.out.println(title + ":\t" + message);
    }

    public static void showLoading(final String message) {
        System.out.println(message);
    }

    public static void showLoading(final String title, final String message) {
        System.out.println(title + ":\t" + message);
    }

    public static void showResponse(final String message) {
        System.out.println(message);
    }

    public static void showResponse(final String title, final String message) {
        System.out.println(title + ":\t" + message);
    }

}
