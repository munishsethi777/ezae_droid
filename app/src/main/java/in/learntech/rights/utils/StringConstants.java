package in.learntech.rights.utils;

/**
 * Created by baljeetgaheer on 02/09/17.
 */

public class StringConstants {
    public static  final String GET = "GET";
    public static final String POST = "POST";
    public static final String CONNECTION_ERROR = "Connection Error.Please check network connection.";
    public static final String PREFS_NAME = "LoginAuth";
    public static final String LOGGED_IN_USER_SEQ = "loggedInUserSeq";
    public static final String LOGGED_IN_USER_COMPANY_SEQ = "loggedInUserCompanySeq";
    public static  final String VERSION = "versionCode";
    public static final String SHORTCUT = "SHORTCUT";

    //API URL STRINGS
    public static final String ACTION_API_URL = "http://www.ezae.in/se/Actions/Mobile/";

    //APPLICATION URL
    public static final String WEB_URL = "http://www.ezae.in/se/";


    //User Actions
    public static final String LOGIN_URL = ACTION_API_URL + "UserAction.php?call=login&username={0}&password={1}&gcmid={2}";
    public static final String GET_DASHBOARD_COUNTS = ACTION_API_URL+"UserAction.php?call=getDashboardStats&userSeq={0}&companySeq={1}";

    public static final String GET_MYACHIEVEMENT_COUNTS = ACTION_API_URL + "UserAction.php?call=myAchievements&userSeq={0}&companyseq={1}";

    public static final String GET_NOTIFICATIONS = ACTION_API_URL + "UserAction.php?userSeq={0}&companySeq={1}&call=getNotifications";


    //LearningPlan Actions
    public static final String GET_LEARNING_PLANS = ACTION_API_URL + "LearningPlanAction.php?call=getLearningPlans&userSeq={0}&companySeq={1}";

    //Badge Actions
    public static final String GET_MYACHIEVEMENT_MY_BADGES = ACTION_API_URL + "BadgeAction.php?call=myAchievementMyBadges&userSeq={0}&companyseq={1}";

}
