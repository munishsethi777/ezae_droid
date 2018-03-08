package in.learntech.rights.messages;

import java.io.Serializable;

/**
 * Created by munishsethi on 18/09/17.
 */

public class MessageModel implements Serializable{

    private int seq;
    private String messageText;
    private String dated;
    private String chattingUser;
    private String chattingUserType;
    private int chattingUserSeq;
    private String imageURL;


    private boolean isRead;




    public MessageModel(){}

    public MessageModel(String _messageText,String _dated,String _chattingUser, String _imageURL, String chtUserType, int chtUserSeq){
        messageText = _messageText;
        dated = _dated;
        chattingUser = _chattingUser;
        imageURL = _imageURL;
        chattingUserType = chtUserType;
        chattingUserSeq = chtUserSeq;
    }
    public MessageModel(String _messageText,String _dated,String _chattingUser, String _imageURL, String chtUserType, int chtUserSeq,boolean isRead){
        messageText = _messageText;
        dated = _dated;
        chattingUser = _chattingUser;
        imageURL = _imageURL;
        chattingUserType = chtUserType;
        chattingUserSeq = chtUserSeq;
        this.isRead = isRead;
    }


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getDated() {
        return dated;
    }

    public void setDated(String dated) {
        this.dated = dated;
    }

    public String getChattingUser() {
        return chattingUser;
    }

    public void setChattingUser(String chattingUser) {
        this.chattingUser = chattingUser;
    }

    public String getChattingUserType() {
        return chattingUserType;
    }

    public void setChattingUserType(String chattingUserType) {
        this.chattingUserType = chattingUserType;
    }

    public int getChattingUserSeq() {
        return chattingUserSeq;
    }

    public void setChattingUserSeq(int chattingUserSeq) {
        this.chattingUserSeq = chattingUserSeq;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

}
