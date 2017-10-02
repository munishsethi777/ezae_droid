package in.learntech.rights.Chatroom;

/**
 * Created by munishsethi on 19/09/17.
 */

public class ChatRoomChatModel {

    private int seq;
    private String message;
    private String time;
    private boolean isSend;
    private String userName;

    public ChatRoomChatModel(){}

    public ChatRoomChatModel(int _seq, String _message, String _time, String userName,boolean sent){
        seq = _seq;
        message = _message;
        time = _time;
        isSend = sent;
        this.userName = userName;
    }


    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

}
