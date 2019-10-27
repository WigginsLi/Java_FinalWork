import javax.swing.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

class Function {
    // 连接数据库
    static Connection GetConnect(String DB) throws SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String DB_URL = "jdbc:mysql://localhost:3306/" + DB + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String DB_USER = "root";
        String DB_PASS = "mysqlroot";

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // 判断用户是否存在数据库表中，并返回其密码
    static char[] IF_USER_EXIST(String ID) throws SQLException, NoSuchAlgorithmException {
        Connection conn = GetConnect("maomao_db");
        Statement stmt = conn.createStatement();

        String sql = "select password from users where id = " + ID;

        ResultSet rs = stmt.executeQuery(sql);
        if(rs.next()) {
            return rs.getString(1).toCharArray();
        }else {
            return GetMD5("++++++++++++++++++++++++++++");
        }

    }

    // 比较两char数组是否相等
    static boolean IF_MATCH(char[] A, char[] B) throws NoSuchAlgorithmException {

        if (A.length != B.length) return false;
        else {
            for (int i = 0; i < A.length; i++) {
                if (A[i] != B[i]) return false;
            }
            return true;
        }
    }

    // 检查字符串是否满足格式的条件
    static boolean CheckStringValid(String ID, char[] PW) {
        if (!ID.matches("[0-9]{1,16}")) {
            JOptionPane.showMessageDialog(null,"ID 格式非法，请参考下面的tips～","Error!",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!(new String(PW)).matches("[0-9A-Za-z_]{1,16}")) {
            JOptionPane.showMessageDialog(null,"密码 格式非法，请参考下面的tips～","Error!",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // 得到字符串的MD5值
    static char[] GetMD5(String text) throws NoSuchAlgorithmException {
        String plainText = text;
        MessageDigest md5 = MessageDigest.getInstance("md5");
        byte[] cipherData = md5.digest(plainText.getBytes());
        StringBuilder builder = new StringBuilder();
        for(byte cipher : cipherData) {
            String toHexStr = Integer.toHexString(cipher & 0xff);
            builder.append(toHexStr.length() == 1 ? "0" + toHexStr : toHexStr);
        }
        return builder.toString().toCharArray();
    }

    // 在当前数据库中建立friends表，记录好友
    static void AddFritable(String DB) throws SQLException {
        Connection conn = GetConnect(DB);
        Statement stmt = conn.createStatement();

        String sql = null;
        sql = "create table Friends (id char(100), lastest int(30), already int(30), cate int(30))";
        stmt.executeUpdate(sql);

    }

    // 更新最新的聊天消息数量
    static void updatalastest(String DB,String toID) throws SQLException {
        Connection con = GetConnect(DB);
        Statement stmt = con.createStatement();

        String sql = "select lastest from Friends where id = " + toID;
        ResultSet rs = stmt.executeQuery(sql);

        rs.next();
        int now = rs.getInt(1);

        sql = "update Friends set lastest = " + (now+1) + " where id = " + toID;
        stmt.executeUpdate(sql);
    }

    // 更新已读消息数等于最新消息数
    static void updataalready(String DB, String toID) throws SQLException {
        Connection con = GetConnect(DB);
        Statement stmt = con.createStatement();

        String sql = "select lastest from Friends where id = " + toID;
        ResultSet rs = stmt.executeQuery(sql);

        rs.next();
        int now = rs.getInt(1);

        sql = "update Friends set already = " + now + " where id = " + toID;
        stmt.executeUpdate(sql);
    }

    // 更新聊天内容记录
    static void updateTalk(Messager Mess) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        String sql;

        conn = GetConnect("user" + Mess.fromID);
        stmt = conn.createStatement();

        sql = "insert into talk_user" + Mess.toID + "(time, id, content) values (\'"+ Mess.time +"\', " + Mess.fromID + ", \'" + Mess.cont + "\')";
        stmt.executeUpdate(sql);

        conn = GetConnect("user" + Mess.toID);
        stmt = conn.createStatement();

        sql = "insert into talk_user" + Mess.fromID + "(time, id, content) values (\'"+ Mess.time +"\', " + Mess.fromID + ", \'" + Mess.cont + "\')";
        stmt.executeUpdate(sql);
    }
}