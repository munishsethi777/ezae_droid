package in.learntech.rights.BusinessObjects;

import java.util.Date;

/**
 * Created by baljeetgaheer on 09/03/18.
 */

public class PendingToUpload {
    private int id;
    private int moduleSeq;
    private int learningPlanSeq;
    private int userSeq;
    private Date dated;

    public int getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(int userSeq) {
        this.userSeq = userSeq;
    }

    public Date getDated() {
        return dated;
    }

    public void setDated(Date dated) {
        this.dated = dated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getModuleSeq() {
        return moduleSeq;
    }

    public void setModuleSeq(int moduleSeq) {
        this.moduleSeq = moduleSeq;
    }

    public int getLearningPlanSeq() {
        return learningPlanSeq;
    }

    public void setLearningPlanSeq(int learningPlanSeq) {
        this.learningPlanSeq = learningPlanSeq;
    }


}
