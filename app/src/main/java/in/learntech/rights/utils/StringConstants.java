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
    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";
    public static final String LP_SEQ = "lpSeq";
    public static final String MODULE_SEQ = "moduleSeq";

    //API URL STRINGS
    public static final String ACTION_API_URL = "http://www.ezae.in/Actions/Mobile/";

    //APPLICATION URL
    public static final String WEB_URL = "http://www.ezae.in/";
    public static final String IMAGE_URL = "http://www.ezae.in/images/";


    //User Actions
    public static final String LOGIN_URL = ACTION_API_URL + "UserAction.php?call=login&username={0}&password={1}&gcmid={2}";
    public static final String GET_DASHBOARD_COUNTS = ACTION_API_URL+"UserAction.php?call=getDashboardStats&userSeq={0}&companySeq={1}";

    public static final String GET_MYACHIEVEMENT_COUNTS = ACTION_API_URL + "UserAction.php?call=myAchievements&userSeq={0}&companyseq={1}";

    public static final String GET_NOTIFICATIONS = ACTION_API_URL + "UserAction.php?userSeq={0}&companySeq={1}&call=getNotifications";


    //LearningPlan Actions
    public static final String GET_LEARNING_PLANS = ACTION_API_URL + "LearningPlanAction.php?call=getLearningPlans&userSeq={0}&companySeq={1}";

    //Badge Actions
    public static final String GET_MYACHIEVEMENT_MY_BADGES = ACTION_API_URL + "BadgeAction.php?call=myAchievementMyBadges&userSeq={0}&companyseq={1}";

    //Module Action
    public static final String GET_MODULES = ACTION_API_URL + "ModuleAction.php?&call=getDirectModules&userSeq={0}&companySeq={1}";
    public static final String GET_MODULE_DETAILS = ACTION_API_URL + "ModuleAction.php?call=getModuleDetails&userSeq={0}&moduleSeq={1}&learningPlanSeq={2}";

    //Learning Plan Action
    public static final String GET_LEARNING_PLAN_DETAIL = ACTION_API_URL + "LearningPlanAction.php?userSeq={0}&companySeq={1}&call=getLearningPlanDetails";

    //QuizProgress Action
    public static final String SUBMIT_QUIZ_PROGRESS = ACTION_API_URL + "QuizProgressAction.php?call=saveQuizProgress&answers={0}";
    //Activity Action
    public static final String SAVE_ACTIVITY = ACTION_API_URL + "ActivityAction.php?call=saveActivity&activityData={0}";


}
