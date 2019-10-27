import com.mysql.cj.protocol.Message;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainForm {

    JFrame frame;
    private JTextField txt;
    private JTextField textField;
    Client cl;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainForm window = new MainForm("1");
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainForm(String ID) throws SQLException, IOException {
        this.cl = new Client(ID);
        initialize(ID);
    }



    /**
     * Initialize the contents of the frame.
     */
    private void initialize(String ID) throws SQLException {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setToolTipText("");
        tabbedPane.setBounds(22, 78, 234, 437);
        frame.getContentPane().add(tabbedPane);

        JTree tree = new JTree();
        tree.setModel(new DefaultTreeModel(
                new DefaultMutableTreeNode("列表") {
                    {
                        DefaultMutableTreeNode node_1;
                        node_1 = new DefaultMutableTreeNode("好友");
                        add(node_1);
                        node_1 = new DefaultMutableTreeNode("群聊(功能未开放)");
                        node_1.add(new DefaultMutableTreeNode("绝密开车群"));
                        add(node_1);
                    }
                }
        ));


        tabbedPane.addTab("我的好友们~", null, tree, null);

        textField = new JTextField();
        textField.setBounds(32, 514, 130, 26);
        frame.getContentPane().add(textField);
        textField.setColumns(10);

        JButton button_4 = new JButton("添加好友");
        button_4.setBounds(159, 514, 97, 29);
        frame.getContentPane().add(button_4);

        JPanel panel = new JPanel();
        tabbedPane.addTab("通知 *3", null, panel, null);
        panel.setLayout(null);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        JList list = new JList();
        list.setBounds(6, 6, 201, 393);
        list.setCellRenderer(renderer);
        list.setModel(new AbstractListModel() {
            String[] values = new String[] {"本功能未实现QAQ", "喵喵(1000)  申请添加你为好友~", "user2(1002) 申请添加你为好友~", "useruser2(1003) 申请添加你为好友~"};
            public int getSize() {
                return values.length;
            }
            public Object getElementAt(int index) {
                return values[index];
            }
        });
        panel.add(list);

        JButton button_1 = new JButton("同意所选");
        button_1.setBounds(16, 398, 101, 29);
        panel.add(button_1);

        JButton button_2 = new JButton("拒绝所选");
        button_2.setBounds(112, 398, 101, 29);
        panel.add(button_2);

        JLabel lblNewLabel = new JLabel(" (ID : " + cl.ID +")");
        lblNewLabel.setFont(new Font("Courier", Font.PLAIN, 13));
        lblNewLabel.setIcon(new ImageIcon("/Users/wile/Desktop/default.jpg"));
        lblNewLabel.setBounds(22, 6, 159, 60);
        frame.getContentPane().add(lblNewLabel);

        JTextArea textArea = new JTextArea();
        textArea.setBackground(new Color(255, 255, 255));
        textArea.setBounds(318, 425, 419, 77);
        textArea.setVisible(false);
        frame.getContentPane().add(textArea);

        JButton button = new JButton("发送");
        button.setBounds(630, 514, 117, 29);
        frame.getContentPane().add(button);

        JLabel label = new JLabel("选择一个好友开始聊天吧～");
        label.setBounds(318, 62, 429, 16);
        frame.getContentPane().add(label);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(318, 78, 419, 318);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        frame.getContentPane().add(scrollPane);

        JList list_1 = new JList();
        list_1.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
        scrollPane.setViewportView(list_1);
        list_1.setBackground(new Color(255, 255, 255));

        DefaultListModel list_1model = new DefaultListModel();
        list_1model.addElement("选择一个好友这里就会显示聊天记录哦～");
        list_1.setModel(list_1model);

//        JButton btnNewButton = new JButton("上一页");
//        scrollPane.setColumnHeaderView(btnNewButton);

        JButton button_3 = new JButton("修改昵称");
        button_3.setBounds(269, 21, 117, 29);

        frame.getContentPane().add(button_3);

//        JButton btnNewButton_1 = new JButton("下一页");
//        btnNewButton_1.setBounds(607, 396, 130, 16);
//        frame.getContentPane().add(btnNewButton_1);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBounds(316, 405, 204, 20);
        frame.getContentPane().add(toolBar);

        JButton btnNewButton_2 = new JButton("字体");
        toolBar.add(btnNewButton_2);

        JButton btnNewButton_3 = new JButton("表情");
        toolBar.add(btnNewButton_3);

        txt = new JTextField();
        txt.setText(cl.Nick);
        txt.setBounds(172, 21, 84, 26);
        frame.getContentPane().add(txt);
        txt.setColumns(10);


        cl.setTalklistModel(list_1model);
        cl.setTree(tree);
        // 显示好友列表
        cl.showFriends();
        // 新建多线程接收信息的对象
        C_receive cr = new C_receive(cl.socket, cl);
        cr.start();

        // 修改昵称
        button_3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    cl.changeNick(txt.getText());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 添加好友
        button_4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    cl.AddFriend(textField.getText());
                    tree.clearSelection();
                    cl.showFriends();
                } catch (NoSuchAlgorithmException | SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 选中某位好友
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                textArea.setVisible(true);
                // 如果是取消选中则不进行操作
                if (tree.getSelectionCount() <= 0) {
                    return ;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                list_1model.removeAllElements();
                String str = node.toString();
                // 提取对方ID
                String FriID = null;
                Pattern pattern = Pattern.compile(".+( \\d )");
                Matcher matcher = pattern.matcher(str);
                if(matcher.find())
                    FriID = matcher.group(1);
                FriID = FriID.split(" ")[1];
                //保存当前选择的对方ID
                cl.setTalkID(FriID);
                try {
                    //  显示与该好友的聊天记录
                    cl.showAllTalk(FriID);
                    cl.IfAlive(cl.talkID);
                    Function.updataalready("user" + cl.ID, cl.talkID);
                    label.setText("与 " + cl.getNick(FriID) + " 的聊天～  " + (cl.talkAlive?"在线":"不在线"));
                    // cl.showFriends();
                    // 不能在这里更新好友列表，因为会导致失去焦点从而报错
                } catch (SQLException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 好友列表失去焦点时更新未读消息数
        tree.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}

            @Override
            public void focusLost(FocusEvent e) {
                tree.clearSelection();
                try {
                    label.setText("与 " + cl.getNick(cl.talkID) + " 的聊天～  " + (cl.talkAlive?"在线":"不在线"));
                    cl.showFriends();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        //发送信息给服务器
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String time = cl.getNowTime();
                if (textArea.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "这里空空如也诶QwQ","error", JOptionPane.INFORMATION_MESSAGE);
                }else {
                    // 发送消息并将新的信息写入聊天列表
                    try {
                        cl.SendMessage(new Messager(cl.ID, cl.talkID, "0", textArea.getText(), time));
                        cl.AddnewTalk(cl.ID, time, textArea.getText());
                    } catch (IOException | SQLException ex) {
                        ex.printStackTrace();
                    }

                    // 清空输入框
                    textArea.setText("");
                }
            }
        });

        // 结束程序时发送离线信息给服务器
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    cl.SendMessage(new Messager(cl.ID, "0", "1", "stop", ""));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }
}


