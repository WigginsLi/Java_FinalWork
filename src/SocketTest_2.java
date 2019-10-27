import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

// 模拟第二个客户端
public class SocketTest_2 {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(InetAddress.getLocalHost(), 1234);

        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream is = new ObjectInputStream(socket.getInputStream());

        os.writeObject(new Messager("2", "0", "1", "init",""));
        // os.writeObject(new Messager("2", "3", "0", "hello","2016/01/01"));
        // os.flush();
//        os.writeObject(new Messager("2", "3", "1", "refresh"));
//        os.flush();

        Messager Mess = (Messager) is.readObject();

        System.out.println(Mess.cont);

    }
}