package in.learntech.rights.Chatroom;

import java.io.Serializable;

/**
 * Created by munishsethi on 18/09/17.
 */

public class ChatRoomModel implements Serializable{

    private int seq;
    private String title;
    private int userSeq;
    private String imageURL;
    public ChatRoomModel(){}

    public ChatRoomModel(int seq,String title,String imageURL){
        this.seq = seq;
        this.title = title;
        this.imageURL = imageURL;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }





}
