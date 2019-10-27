import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.SQLException;

public class C_receive extends Thread{
    Socket mysocket;
    Client client;

    C_receive(Socket s, Client c) {
        mysocket = s;
        client = c;
    }

    public void run() {
        try {
            ObjectInputStream is = new ObjectInputStream(mysocket.getInputStream());
            Messager Mess = null;
            while (true) {
                Mess = (Messager) is.readObject();
                if (Mess.operation.equals("1")) {
                    // 功能信息，刷新好友列表
                    if (Mess.cont.equals("refresh")) {
                        client.tree.clearSelection();
                        client.showFriends();
                    }
                    if (Mess.cont.equals("Alive")) {
                        client.talkAlive = true;
                    }
                    if (Mess.cont.equals("NotAlive")) {
                        client.talkAlive = false;
                    }
                }
                if (Mess.operation.equals("0")) {
                    // 普通信息，如果是当前点击的好友，就在聊天窗口显示新消息
                    // System.out.println(Mess.fromID + "->" + Mess.toID + ":" + Mess.cont); // BUG:为什么这里加上输出语句聊天窗口就会变成空的？？？
                    if (client.talkID.equals(Mess.fromID)) {
                        // 显示消息到聊天列表
                        client.AddnewTalk(Mess.fromID, Mess.time, Mess.cont);
                        // 接收方已读消息， 更新already
                        Function.updataalready("user" + Mess.toID, Mess.fromID);
                    } else {
                        client.showFriends();// 如果不是，则刷新好友列表（以显示未读取消息数量）
                    }
                }
                System.out.println(Mess.fromID + "->" + Mess.toID + ":" + Mess.cont);
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
