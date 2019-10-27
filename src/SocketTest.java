import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

// 测试socket模块功能，模拟第一个客户端
public class SocketTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(InetAddress.getLocalHost(), 1234);

        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        os.writeObject(new Messager("1", "0", "1", "init",""));
        os.flush();

//        ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
//        Messager Mess = (Messager) is.readObject();
//        System.out.println(Mess.cont);
//
//        os.writeObject(new Messager("1", "3", "0", "hi"));
//        os.flush();
//        C_receive c1 = new C_receive(socket);
//        c1.start();

    }
}
