package in.learntech.rights.BusinessObjects;

/**
 * Created by baljeetgaheer on 31/10/17.
 */

public class CompanyUser {
    private int id;
    private int seq;
    private String type;
    private String userName;
    private String imageName;
    private int companySeq;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setCompanySeq(int companySeq){
        this.companySeq = companySeq;
    }
    public int getCompanySeq(){
        return this.companySeq;
    }

}
