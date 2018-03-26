package in.learntech.rights.messages;

/**
 * Created by munishsethi on 19/09/17.
 */

public class MessageChatModel {

    private int seq;
    private String message;
    private String time;
    private boolean isSend;
    private boolean isNew;

    public MessageChatModel(){}

    public MessageChatModel(int _seq,String _message, String _time, boolean sent){
        seq = _seq;
        message = _message;
        time = _time;
        isSend = sent;
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

    public boolean isNew() {
        return isNew;
    }
    public void setNew(boolean aNew) {
        isNew = aNew;
    }



}
