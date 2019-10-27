import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

class Client{
    String ID, Nick;
    Connection conn;
    Statement stmt;
    String talkID;
    Socket socket;
    DefaultListModel TalklistModel;
    ObjectOutputStream os;
    JTree tree;
    boolean talkAlive;

    Client(String id) throws SQLException, IOException {
        this.ID = id;
        this.conn = Function.GetConnect("maomao_db");
        this.stmt = conn.createStatement();
        this.Nick = getNick(ID);
        this.talkID = "0";
        this.socket = new Socket(InetAddress.getLocalHost(), 1234);
        this.os = new ObjectOutputStream(this.socket.getOutputStream());
        os.writeObject(new Messager(this.ID, "0", "1", "init", ""));
        os.flush();
    }

    String getNick(String ID) throws SQLException {
        String sql = "select nick from users where id = \'" + ID + "\' ";
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            return rs.getString(1);
        }else
            return "NULL";
    }

    void changeNick(String newNick) throws SQLException {
        String sql = "update users set nick = \'" + newNick + "\' where Id = \'" + ID + "\'";
        stmt.executeUpdate(sql);
        this.Nick = getNick(this.ID);
        JOptionPane.showMessageDialog(null, "修改昵称成功！！","恭喜", JOptionPane.INFORMATION_MESSAGE);
    }

    String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        return sdf.format(date);
    }

    void AddFriend(String FriID) throws SQLException, NoSuchAlgorithmException {
        if (!FriID.matches("[0-9]{1,16}")) {
            JOptionPane.showMessageDialog(null,"ID 格式非法，请参考登录界面的tips～","Error!",JOptionPane.ERROR_MESSAGE);
            return ;
        }

        if (Function.IF_MATCH(Function.IF_USER_EXIST(FriID), Function.GetMD5("++++++++++++++++++++++++++++"))) {
            JOptionPane.showMessageDialog(null,"找不到这个小伙伴呢QAQ","Error!",JOptionPane.ERROR_MESSAGE);
            return ;
        }

        // 分别为两用户添加好友
        Connection conn = Function.GetConnect("user" + this.ID);
        Statement stmt = conn.createStatement();

        String sql = "select * from Friends where id = " + FriID;

        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            JOptionPane.showMessageDialog(null, "你已经添加了这个小伙伴呢，快去找TA聊天吧～", "Warning", JOptionPane.INFORMATION_MESSAGE);
            return ;
        }
        // 插入好友表，创建与该好友的聊天记录表

        String time = getNowTime();

        sql = "insert into Friends(id, lastest, already, cate) values ("+ FriID + ", 0, 0, 0)";
        stmt.executeUpdate(sql);

        sql = "create table talk_user" + FriID + "(time char(30), id int(30), content char(200))";
        stmt.executeUpdate(sql);

        sql = "insert into talk_user" + FriID + "(time, id, content) values (\'"+ time +"\', 0, \'已添加好友，快来聊天吧～\')";
        stmt.executeUpdate(sql);

        conn = Function.GetConnect("user" + FriID);
        stmt = conn.createStatement();

        if (FriID == this.ID) return ;

        sql = "insert into Friends(id, lastest, already, cate) values ("+ this.ID + ", 0, 0, 0)";
        stmt.executeUpdate(sql);

        sql = "create table talk_user" + this.ID + "(time char(30), id int(30), content char(200))";
        stmt.executeUpdate(sql);

        sql = "insert into talk_user" + this.ID + "(time, id, content) values (\'"+ time +"\', 0, \'已添加好友，快来聊天吧～\')";
        stmt.executeUpdate(sql);

        JOptionPane.showMessageDialog(null, "添加成功～", "Congratulation！", JOptionPane.INFORMATION_MESSAGE);
    }

    void setTree(JTree t) {
        this.tree = t;
    }

    void showFriends() throws SQLException {
        DefaultMutableTreeNode newnode = null;
        DefaultTreeModel dtm = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
        root = root.getNextNode();

        Connection conn = Function.GetConnect("user" + this.ID);
        Statement stmt = conn.createStatement();
        String sql = "select * from Friends";

        ResultSet rs = stmt.executeQuery(sql);

        root.removeAllChildren();
        dtm.reload();
        while (rs.next()) {
            String friID = rs.getString(1);
            int rest = (rs.getInt(2) - rs.getInt(3));
            String cont = "";
            if (rest != 0) {
                cont = "(* " + rest + ")";
            }
            newnode = new DefaultMutableTreeNode(cont + getNick(friID) + " ( " + friID + " ) ");
            dtm.insertNodeInto(newnode, root, root.getChildCount());
            tree.expandPath(new TreePath(dtm.getPathToRoot(newnode.getParent())));
        }
    }

    void setTalklistModel(DefaultListModel model) {
        this.TalklistModel = model;
    }

    void showAllTalk(String FriID) throws SQLException {
        Connection conn = Function.GetConnect("user" + this.ID);
        Statement stmt = conn.createStatement();

        String sql = "select * from talk_user" + FriID;
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String time = rs.getString(1);
            String uid = rs.getString(2);
            String cont = rs.getString(3);
            AddnewTalk(uid, time, cont);
        }
        // System.out.println("Success");
    }

    void AddnewTalk(String fromID, String time, String cont) throws SQLException {
        TalklistModel.addElement(getNick(fromID) + " (" + time + ") :");
        TalklistModel.addElement("        " + cont);
        TalklistModel.addElement(" ");
    }

    void setTalkID(String id) {
        this.talkID = id;
    }

    void SendMessage(Messager Mess) throws IOException{
        os.writeObject(Mess);
        os.flush();
    }

    void IfAlive(String toID) throws IOException {
        os.writeObject(new Messager(this.ID, talkID, "1", "IfAlive", ""));
        os.flush();
    }
}
