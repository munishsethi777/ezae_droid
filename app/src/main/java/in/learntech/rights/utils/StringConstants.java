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
    public static final String NOTIFICATION_STATE = "notificationState";
    public static final String NOTIFICATION_ENTITY_SEQ = "notificationEntitySeq";
    public static final String NOTIFICATION_ENTITY_TYPE = "notificationEntityType";
    public static final String FROM_USER_NAME = "fromUserName";
    public static final String LOGGED_IN_USER_COMPANY_SEQ = "loggedInUserCompanySeq";
    public static final String CURRENT_ACTIVITY_NAME = "currentActivityName";
    public static final String MESSAGE_CHAT_ACTIVITY = "MessageChatActivity";
    public static  final String VERSION = "versionCode";
    public static final String SHORTCUT = "SHORTCUT";
    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";
    public static final String LP_SEQ = "lpSeq";
    public static final String MODULE_SEQ = "moduleSeq";
    public static final String USER_DUMMY_IMAGE_NAME = "dummy.jpg";
    public static final String NOTE_SEQ = "noteSeq";
    public static final String EVENT_DATE = "eventDate";
    public static final String DATA_STRING = "dataString";

    //API URL STRINGS
    public static final String ACTION_API_URL = "http://www.ezae.in/Actions/Mobile/";

    //APPLICATION URL
    public static final String WEB_URL = "http://www.ezae.in/";
    public static final String IMAGE_URL = "http://www.ezae.in/images/";
    public static final String DOC_URL = WEB_URL + "docs/moduledocs/";


    //User Actions
    public static final String LOGIN_URL = ACTION_API_URL + "UserAction.php?call=login&username={0}&password={1}&gcmid={2}";
    public static final String GET_COUNTS = ACTION_API_URL+"UserAction.php?call=getDashboardCounts&userSeq={0,number,#}&companySeq={1,number,#}";
    public static final String GET_DASHBOARD_COUNTS = ACTION_API_URL+"UserAction.php?call=getDashboardStats&userSeq={0,number,#}&companySeq={1,number,#}";
    public static final String GET_MYACHIEVEMENT_COUNTS = ACTION_API_URL + "UserAction.php?call=myAchievements&userSeq={0,number,#}&companyseq={1,number,#}";
    public static final String GET_NOTIFICATIONS = ACTION_API_URL + "UserAction.php?userSeq={0,number,#}&companySeq={1,number,#}&call=getNotifications";

    public static final String CHANGE_PASSWORD = ACTION_API_URL + "UserAction.php?call=changePassword&userSeq={0,number,#}&companySeq={1,number,#}&earlierPassword={2}&newPassword={3}";
    public static final String GET_USER_DETAIL = ACTION_API_URL + "UserAction.php?call=getUserDetail&userSeq={0,number,#}&companySeq={1,number,#}";
    public static final String UPDATE_USER_PROFILE = ACTION_API_URL + "UserAction.php?call=updateUserProfile&userSeq={0,number,#}&companySeq={1,number,#}&userProfileDetail={2}";
    public static final String SYNCH_USERS = ACTION_API_URL + "UserAction.php?call=synchUsersAndAdmins&userSeq={0,number,#}&companySeq={1,number,#}";
    public static final String GET_PROFILE_AND_MODULES = ACTION_API_URL + "UserAction.php?call=getProfilesAndModules&userSeq={0,number,#}&companySeq={1,number,#}";

    public static final String GET_LEADERBOARD_BY_MODULE = ACTION_API_URL + "UserAction.php?call=getLeaderBoardDataByModule&userSeq={0,number,#}&companySeq={1,number,#}&moduleSeq={2,number,#}";
    public static final String GET_LEADERBOARD_BY_PROFILE = ACTION_API_URL + "UserAction.php?call=getLeaderBoardDataByProfile&userSeq={0,number,#}&companySeq={1,number,#}&profileSeq={2,number,#}";
    public static final String GET_LEADERBOARD_BY_LEARNINGPLAN = ACTION_API_URL + "UserAction.php?call=getLeaderBoardDataByLearningPlan&userSeq={0,number,#}&companySeq={1,number,#}&lpSeq={2,number,#}";
    public static final String NOMINATE_TRAINING = ACTION_API_URL+"UserAction.php?call=nominateCT&userSeq={0,number,#}&companySeq={1,number,#}&trainingSeq={2,number,#}&lpSeq={3,number,#}";
    public static final String GET_SCORES = ACTION_API_URL+"UserAction.php?call=getScoresByLearningPlan&userSeq={0,number,#}&companySeq={1,number,#}&lpSeq={2,number,#}";

    //LearningPlan Actions
    public static final String GET_LEARNING_PLANS = ACTION_API_URL + "LearningPlanAction.php?call=getLearningPlans&userSeq={0,number,#}&companySeq={1,number,#}";
    public static final String GET_ALL_LEARNING_PLANS = ACTION_API_URL + "LearningPlanAction.php?call=getLearningPlansByUser&userSeq={0,number,#}&companySeq={1,number,#}";

    //Badge Actions
    public static final String GET_MYACHIEVEMENT_MY_BADGES = ACTION_API_URL + "BadgeAction.php?call=myAchievementMyBadges&userSeq={0,number,#}&companyseq={1,number,#}";

    //Module Action
    public static final String GET_MODULES = ACTION_API_URL + "ModuleAction.php?&call=getDirectModules&userSeq={0,number,#}&companySeq={1,number,#}";
    public static final String GET_MODULE_DETAILS = ACTION_API_URL + "ModuleAction.php?call=getModuleDetails&userSeq={0,number,#}&moduleSeq={1,number,#}&learningPlanSeq={2,number,#}";

    //Learning Plan Action
    public static final String GET_LEARNING_PLAN_DETAIL = ACTION_API_URL + "LearningPlanAction.php?userSeq={0,number,#}&companySeq={1,number,#}&call=getLearningPlanDetails";

    //QuizProgress Action

    public static final String SUBMIT_QUIZ_PROGRESS = ACTION_API_URL + "QuizProgressAction.php?call=saveQuizProgress&userSeq={0,number,#}&companySeq={1,number,#}&answers={2}";

    //Activity Action
    public static final String SAVE_ACTIVITY = ACTION_API_URL + "ActivityAction.php?call=saveActivity&userSeq={0,number,#}&companySeq={1,number,#}&activityData={2}";

    //NOTES Action
    public static final String GET_ALL_NOTES = ACTION_API_URL + "NoteAction.php?call=getAllNotes&userSeq={0,number,#}&companySeq={1,number,#}";
    public static final String GET_NOTES_DETAILS = ACTION_API_URL + "NoteAction.php?call=getNoteDetails&userSeq={0,number,#}&companySeq={1,number,#}&noteSeq={2,number,#}";
    public static final String SAVE_NOTES_DETAILS = ACTION_API_URL + "NoteAction.php?call=saveNote&userSeq={0,number,#}&companySeq={1,number,#}&seq={2,number,#}&details={3}";
    public static final String DELETE_NOTE = ACTION_API_URL + "NoteAction.php?call=deleteNote&userSeq={0,number,#}&companySeq={1,number,#}&noteSeq={2,number,#}";

    //MESSAGES Action
    public static final String GET_MESSAGES = ACTION_API_URL + "MessageAction.php?call=getMessages&userSeq={0,number,#}&companySeq={1,number,#}";
    public static final String GET_MESSAGE_DETAILS = ACTION_API_URL + "MessageAction.php?call=getMessageDetails&userSeq={0,number,#}&companySeq={1,number,#}&chattingWithUserSeq={2,number,#}&chattingWithUserType={3}&afterMessageSeq={4,number,#}";
    public static final String SEND_MESSAGE_CHAT = ACTION_API_URL + "MessageAction.php?call=sendMessageChat&userSeq={0,number,#}&companySeq={1,number,#}&chattingWithUserSeq={2,number,#}&chattingWithUserType={3}&message={4}&chatLoadedTillSeq={5,number,#}";
    public static final String DELETE_CONVERSATION = ACTION_API_URL + "MessageAction.php?call=deleteChatConversation&userSeq={0,number,#}&companySeq={1,number,#}&chattingWithUserSeq={2,number,#}&chattingWithUserType={3}";
    public static final String  MESSAGE_MARK_AS_READ = ACTION_API_URL + "MessageAction.php?call=markReadAndUnRead&userSeq={0,number,#}&companySeq={1,number,#}&chattingWithUserSeq={2,number,#}&chattingWithUserType={3}&isRead={4}";

    //Chatroom Action
    public static final String GET_CHAT_ROOMS = ACTION_API_URL + "ChatroomAction.php?call=getChatrooms&userSeq={0,number,#}&companySeq={1,number,#}";
    public static final String GET_ALL_EVENTS = ACTION_API_URL + "ChatroomAction.php?call=getAllEvents&userSeq={0,number,#}&companySeq={1,number,#}";
    public static final String GET_CHAT_ROOMS_DETAIL = ACTION_API_URL + "ChatroomAction.php?call=getChatroomDetails&userSeq={0,number,#}&companySeq={1,number,#}&chatroomId={2,number,#}&afterMessageSeq={3,number,#}";
    public static final String SEND_CHAT_ROOM_CHAT = ACTION_API_URL + "ChatroomAction.php?call=sendMessageChat&userSeq={0,number,#}&companySeq={1,number,#}&chatroomId={2,number,#}&userType={3}&userName={4}&afterMessageSeq={5,number,#}&message={6}";

    //Notification Action
    public static final String GET_NOTIFICATIONS_NEW = ACTION_API_URL + "NotificationAction.php?call=getAllNotifications&userSeq={0,number,#}&companySeq={1,number,#}";
    public static final String MARK_AS_READ_NOTIFICATION = ACTION_API_URL + "NotificationAction.php?call=markReadOrUnRead&isRead=1&userSeq={0,number,#}&companySeq={1,number,#}";
    public static final String DELETE_NOTIFICATION = ACTION_API_URL + "NotificationAction.php?call=markAsClearNotification&userSeq={0,number,#}&companySeq={1,number,#}&seq={2,number,#}";



}
