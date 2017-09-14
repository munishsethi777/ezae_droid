package in.learntech.rights.BusinessObjects;

import java.util.Date;
/**
 * Created by baljeetgaheer on 09/09/17.
 */

public class QuestionProgress {
    private int id;
    private int moduleSeq;
    private int questionSeq;
    private int ansSeq;
    private String ansText;
    private boolean isTimeUp;
    private Date startDate;
    private int userSeq;
    private Date endDate;
    private boolean isUploaded;
    private int learningPlanSeq;
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLearningPlanSeq() {
        return learningPlanSeq;
    }

    public void setLearningPlanSeq(int learningPlanSeq) {
        this.learningPlanSeq = learningPlanSeq;
    }

    public int getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(int userSeq) {
        this.userSeq = userSeq;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
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

    public int getQuestionSeq() {
        return questionSeq;
    }

    public void setQuestionSeq(int questionSeq) {
        this.questionSeq = questionSeq;
    }

    public int getAnsSeq() {
        return ansSeq;
    }

    public void setAnsSeq(int ansSeq) {
        this.ansSeq = ansSeq;
    }

    public String getAnsText() {
        return ansText;
    }

    public void setAnsText(String ansText) {
        this.ansText = ansText;
    }

    public boolean isTimeUp() {
        return isTimeUp;
    }

    public void setTimeUp(boolean timeUp) {
        isTimeUp = timeUp;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


}
