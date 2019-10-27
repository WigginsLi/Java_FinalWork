import java.awt.EventQueue;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LoginUI {
    private JFrame frame;
    private JTextField textField1;
    private JPasswordField textField2;

    public static void main(String[] args) {
        LoginUI frame = new LoginUI();
        frame.frame.setVisible(true);
    }

    /**
     * Create the application.
     */
    public LoginUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 400, 300);
        frame.setLocation(750,400);
        frame.setResizable(false);
        frame.setTitle("喵呜 V1.0.1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel label = new JLabel("帐号:");
        label.setBounds(26, 108, 61, 16);
        frame.getContentPane().add(label);

        JLabel label_1 = new JLabel("密码:");
        label_1.setBounds(26, 151, 61, 16);
        frame.getContentPane().add(label_1);

        textField1 = new JTextField();
        textField1.setBounds(99, 103, 130, 26);
        frame.getContentPane().add(textField1);
        textField1.setColumns(10);

        textField2 = new JPasswordField();
        textField2.setBounds(99, 151, 130, 21);
        frame.getContentPane().add(textField2);

        JButton btnNewButton = new JButton("New button");
        frame.getContentPane().add(btnNewButton);



        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon("/Users/wile/Desktop/default.jpg"));
        lblNewLabel.setBounds(26, 6, 83, 85);
        frame.getContentPane().add(lblNewLabel);

        //======================== Button =================

        JButton btnTips = new JButton("Tips~");
        btnTips.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"帐号只能是数字，\n" +
                        "密码只能是字母数字和下划线(不超过16位)哦～");
            }
        });
        btnTips.setBounds(264, 196, 117, 29);
        frame.getContentPane().add(btnTips);

        JButton Button1 = new JButton("登录");
        Button1.setBounds(264, 146, 117, 29);
        Button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ID = textField1.getText();
                char[] PW = textField2.getPassword();
                //判断id和pw的格式是否合法
                if (!Function.CheckStringValid(ID, PW)) return ;
                //判断是否存在用户并判断密码是否匹配
                try {
                    // System.out.println(new String(PW) + " " + new String(Function.GetMD5(new String(PW))));
                    if (Function.IF_MATCH(Function.GetMD5(new String(PW)), Function.IF_USER_EXIST(ID))) {
                        Button1.setText("Loading...");
                        JOptionPane.showMessageDialog(null,"登录成功～","Congratulation!!!",JOptionPane.INFORMATION_MESSAGE);
                        // 创建主窗口，并将登录窗口隐藏
                        MainForm mainForm = new MainForm(ID);
                        mainForm.frame.setVisible(true);
                        frame.setVisible(false);
                    }else {
                        JOptionPane.showMessageDialog(null,"登录失败，该用户不存在或密码不正确 QAQ","Error!",JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException | NoSuchAlgorithmException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        frame.getContentPane().add(Button1);

        JButton Button2 = new JButton("注册");
        Button2.setBounds(264, 103, 117, 29);
        Button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ID = textField1.getText();
                char[] PW = textField2.getPassword();

                if (!Function.CheckStringValid(ID, PW)) return ;

                try {
                    // 判断是否已存在该用户
                    if (!Function.IF_MATCH(Function.IF_USER_EXIST(ID), Function.GetMD5("++++++++++++++++++++++++++++"))) {
                        JOptionPane.showMessageDialog(null,"该ID已被注册 QAQ","Error!",JOptionPane.ERROR_MESSAGE);
                    }else {
                        // 数据库插入新记录，(ID，nick，pw(MD5加密))
                        Connection conn = Function.GetConnect("maomao_db");
                        Statement stmt = conn.createStatement();

                        String sql = "create database user" + ID.toString();
                        stmt.executeUpdate(sql);

                        // 创建用户个人数据库（记录好友及聊天信息）
                        Function.AddFritable("user"+ID.toString());

                        sql = "insert into users(id, nick, password) values (" + ID + ",'新来的小伙伴～',\'"+ new String(Function.GetMD5(new String(PW)))+"\')";
                        stmt.executeUpdate(sql);

                        JOptionPane.showMessageDialog(null,"注册成功!!可以去登录啦～","Congratulation!!!",JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException | NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                }
            }
        });
        frame.getContentPane().add(Button2);
    }
}


