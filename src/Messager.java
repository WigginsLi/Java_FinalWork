// 消息对象
class Messager implements java.io.Serializable{
    String fromID,toID, operation, cont, time;
    // operation 0:普通消息  ； 1：其他消息（好友申请,是否在线）

    Messager(String f, String t, String op, String cont, String time) {
        this.fromID = f;
        this.toID = t;
        this.operation = op;
        this.cont = cont;
        this.time = time;
    }
}