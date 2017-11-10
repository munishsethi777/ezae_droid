package in.learntech.rights.Leaderboard;

/**
 * Created by baljeetgaheer on 07/11/17.
 */

public class LeaderboardModel {

    private String userImage;
    private String userName;
    private String score;
    private String dateDiff;

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDateDiff() {
        return dateDiff;
    }

    public void setDateDiff(String dateDiff) {
        this.dateDiff = dateDiff;
    }

}
