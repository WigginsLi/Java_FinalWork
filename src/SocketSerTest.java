import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;

class Server extends Thread{
    private Socket mysocket;
    static HashMap<String, Socket> ha = new HashMap<>();
    static HashMap<String, ObjectOutputStream> osha = new HashMap<>();

    Server(Socket s) {
        mysocket = s;
    }

    public void run() {
        try {
            ObjectInputStream is = new ObjectInputStream(mysocket.getInputStream());
            ObjectOutputStream os = new ObjectOutputStream(mysocket.getOutputStream());
            ObjectOutputStream nextos = null;
            Messager Mess;

            while (true) {
                Mess = (Messager) is.readObject();
                // 在控制台输出往来消息
                System.out.println(Mess.fromID+ "->" + Mess.toID + " : " + Mess.cont);
                // 功能消息，进行各种操作
                if (Mess.operation.equals("1")) {
                    // 用map映射ID和socket、outputstream。当map中存在该ID使表明其在线，离线时销毁。
                    if (Mess.cont.equals("init")) {ha.put(Mess.fromID, mysocket); osha.put(Mess.fromID, os);}
                    if (Mess.cont.equals("stop")) {ha.remove(Mess.fromID);osha.remove(Mess.fromID); break;}
                    // 得到对方是否在线的状态
                    if (Mess.cont.equals("IfAlive")) {
                        os.writeObject(new Messager("0", Mess.fromID, "1", ha.containsKey(Mess.toID)?"Alive":"NotAlive", ""));
                        os.flush();
                    }
                    // 刷新对方的好友列表
                    if (Mess.cont.equals("refresh")) {
                        // 之所以要把outputstream记录下来是因为如果在这里new一个的话就会重复输出一些奇怪字符，接收端就会报错
                        nextos = osha.get(Mess.toID);
                        nextos.writeObject(new Messager("0", Mess.toID, "1", "refresh", ""));
                        nextos.flush();
                    }
                }
                // 普通消息，把消息转发给对方客户端
                if (Mess.operation.equals("0")) {
                    if (ha.containsKey(Mess.toID)) {
                        nextos = osha.get(Mess.toID);
                        nextos.writeObject(Mess);
                        nextos.flush();
                    }
                    // 更新数据库
                    // 更新最新消息数
                    Function.updatalastest("user" + Mess.fromID, Mess.toID);
                    Function.updatalastest("user" + Mess.toID, Mess.fromID);
                    // 发送方已读消息，所以更新已读数量到最新的消息数量
                    Function.updataalready("user" + Mess.fromID, Mess.toID);
                    // 更新聊天内容记录
                    Function.updateTalk(Mess);
                    // 通知对方更新好友列表
                    nextos = osha.get(Mess.toID);
                    nextos.writeObject(new Messager("0", Mess.toID, "1", "refresh", ""));
                    nextos.flush();
                }
                if (Mess.fromID == null) break;
            }
        }catch (IOException | ClassNotFoundException | SQLException e) {}
    }
}

public class SocketSerTest {
    public static void main(String[] args) throws IOException {
        ServerSocket myserver = new ServerSocket(1234);

        while (true) {
            Socket mysocket = myserver.accept();
            Server ser = new Server(mysocket);
            ser.start();
        }

    }
}
