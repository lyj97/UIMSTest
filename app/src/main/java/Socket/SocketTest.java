package Socket;

import net.sf.json.JSONObject;

import java.io.PrintWriter;
import java.net.Socket;

import UIMS.Address;

public class SocketTest {

    static boolean isConnect = false;
    static Socket socket;
    static PrintWriter writer;

    public static boolean connect(){
        try{
            if(isConnect) socket.close();
            socket = new Socket(Address.myHost, Address.myPort);
            writer = new PrintWriter(socket.getOutputStream());
            isConnect = true;
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sent(String string){
        try{
            writer.println(string);
            writer.flush();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean disconnect(){
        try{
            socket.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static Socket getSocket() {
        return socket;
    }

    public static boolean isConnect() {
        return isConnect;
    }

    public static void main(String[] args) {
        connect();
        JSONObject info = new JSONObject();
        info.put("id", "54160907");
        info.put("pass", "225577");
        JSONObject object = new JSONObject();
        object.put("type", "UserLogin");
        object.put("info", info);
        sent(object.toString());
        disconnect();
    }

}
