package cc.coocol.jinxiujob.networks;

/**
 * Created by coocol on 2016/3/9.
 */
public class URL {
    public static final String ROOT_PATH = "http://115.28.22.98:7652/api/v1.0/";

    public static final String PHONE_CODE = "phone/code";
    public static final String USER = "user";
    public static final String TOEKN = "token";
    public static final String USER_PROFILE = "user/profile";

    public static final String PASSWORD_CODE_GET = "account/code";
    public static final String PASSWORD_CODE_CONFIRM = "account/code";
    public static final String PASSWORD_NEW = "account/password";

    public static final String PROVINCES_CITIES = "cities";
    
    public static final String USER_LOCATION = "user/settings/location";

    public static final String ALL_JOBS = "jobs";

    public static final String ALL_ENTERPRISES = "enterprises";

    public static final String ENTERPRISE = "enterprise/";

    public static final String JOB = "job/";
    public static final String JOB_STATUS = "job/status/"; // + user_id



    public static final String USER_HEADER = "user/";

    public static final String RESUME = "resume/";


    public static final String COMPANY_LOGO = ROOT_PATH + "static/logo/";
    public static final String USER_PHOTO = ROOT_PATH + "static/head/";

    public static final String USER_COLLECTIONS_JOBS = "collection/jobs/"; // + user_id
    public static final String USER_COLLECTIONS_ENTERS = "collection/enterprises/"; // + user_id
    public static final String USER_APPLICATIONS = "applications/"; // + user_id


    public static final String NOTIFICATIONS = "notifications/";
}
