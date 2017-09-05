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


    //User Actions
    public static final String LOGIN_URL = ACTION_API_URL + "UserAction.php?call=login&username={0}&password={1}&gcmid={2}";
    public static final String GET_DASHBOARD_COUNTS = ACTION_API_URL+"UserAction.php?call=getDashboardStats&userSeq={0}&companySeq={1}";
}
