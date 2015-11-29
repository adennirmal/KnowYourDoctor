package pack.knowyourdoctor.Constants;

//Hold all strings
public class Strings {
    //Common Strings
    public static final String EMPTY_STRING = "";
    public static final String STRING_WITH_SPACE = " ";
    public static final String IGNORE_SPACES = "%20";
    public static final String FORWARD_SLASH = "/";

    public static final String DR = "Dr. ";
    public static final String RATE_DR = "Rate Dr.";

    //Colors in hexa format
    public static final String WHITE_COLOR = "#ffffff";

    //Animation Strings
    public static final String ROTATION = "rotation";
    public static final String TRANSLATION_X = "translationX";
    public static final String TRANSLATION_Y = "translationY";

    //Web Service URLs
    public static final String INSERT_NEW_RATING_URL =
            "PhoneAppControllers/AppRatingController/insertAppNewRating/";
    public static final String GET_ALL_LOCATED_DOCTORS =
            "PhoneAppControllers/LocatedDoctorListController/getAllLocatedDoctors";
    public static final String INSERT_FAKE_DOC_DETAILS =
            "PhoneAppControllers/FakeDoctorReportController/insertFakeDoctorReport/";
    public static final String INSERT_DOC_RATING =
            "PhoneAppControllers/DoctorRatingController/insertNewRating/";
    public static final String GET_ALL_COMMENTS =
            "PhoneAppControllers/DoctorRatingController/getAllCommentsOfDoc/";
    public static final String UPDATE_LIKES =
            "PhoneAppControllers/DoctorRatingController/updateLikes/";
    public static final String GET_ALL_HOSPITALS =
            "PhoneAppControllers/HospitalListController/getAllHospitals";
    public static final String INSERT_DOC_LOCATION =
            "PhoneAppControllers/DoctorLocationController/insertLocation";
    public static final String GET_ALL_LOCATIONS =
            "PhoneAppControllers/LocatedDoctorListController/getAllLocationsOfDoc";

    //Toast Messages
    public static final String THANKING_TEXT = "Thanks for your support!!";
    public static final String NO_EMAIL_CLIENT_MSG = "There are no email clients installed.";

    //Dialog Messages
    public static final String ENABLE_INTERNET_CONNECTION =
            "Do you want to enable the Internet Connection?";
    public static final String INTERNET_CONNECTION_ERROR =
            "Sorry! please turn on your internet connection";
    public static final String YES = "YES";
    public static final String NO = "NO";
    public static final String OK = "OK";
    public static final String WARNING = "Warning!";

    //Validation texts
    public static final String INVALID_REGNO = "Invalid Registration Number";
    public static final String INVALID_NAME = "Name cannot have special characters and numbers";
    public static final String INVALID_NIC_NO = "Invalid NIC Number";
    public static final String INVALID_FAKE_REGNO =
            "Please enter fake doctor's registration number";
    public static final String INVALID_REPORTER_NAME = "Please enter your name";
    public static final String INVALID_FAKE_DOC_NAME = "Please enter fake doctor's name";

    //Home Controller
    public static final String SELECTED_DOC_TEXT = "SelectedDoc";
    //Menu items
    public static final String ABOUT_SLMC_TITLE = "About SLMC";
    public static final String ABOUT_US_TITLE = "About Us";

    //SLMC menu item
    public static final String HTML_STYLE = "<html><body><p align=\"justify\" " +
            "style=\"color:black\">";
    public static final String HTML_REST = "</p></body></html>";
    public static final String DOC_TYPE = "text/html";
    public static final String TEXT_TYPE = "utf-8";
    public static final String SLMC_SITE_URL = "http://www.srilankamedicalcouncil.org";
    public static final String NEWS_OF_SLMC = "NEWS of SLMC";
    public static final String NEWS_PAGE = "/news.php#news_163";
    public static final String FOREIGN_UNI_PAGE = "/foreignuniversity.php";
    public static final String FOREIGN_UNIVERSITIES = "Foreign Universities";

    //About us menu item
    public static final String JSON_RATING_VALUE = "rating";
    public static final String JSON_RATING_TEXT = "ratingLevelText";
    //rating levels
    public static final String RATING_VERY_BAD = "Very Bad App";
    public static final String RATING_BAD = "Bad App";
    public static final String RATING_AVERAGE_LEVEL = "App is in average level";
    public static final String RATING_GOOD = "Good App";
    public static final String RATING_VERY_GOOD = "Very Good App";
    public static final String RATING_EXCELLENT = "Excellent App";

    //Doctor detail search page
    public static final String URL_TO_SLMC_SEARCH =
            "http://www.srilankamedicalcouncil.org/registry.php?start=0&registry=";
    public static final String INITIALS = "&initials=";
    public static final String LASTNAME = "&last_name=";
    public static final String OTHERNAME = "&other_name=";
    public static final String REGNO = "&reg_no=";
    public static final String NIC = "&nic=";
    public static final String PART_OF_ADDRESS = "&part_of_address=";
    public static final String SEARCH = "&search=Search";

    public static final String LOADING_ALERT = "Loading list Please wait....";
    public static final String ADVANCED_SEARCH = "Advanced Search";
    public static final String HIDE_ADVANCED_SEARCH = "Hide Advance Search";

    //Facebook fragment
    public static final String JSON_PREFS_NAME = "PrefsFile";
    public static final String JSON_PATH = "path";
    public static final String JSON_IMAGE_CHANGED = "imageChanged";
    public static final String JSON_FB_User = "FbUser";
    public static final String HELLO = "Hello, ";
    public static final String KYD_IMAGES_TEXT = "KYD Images";
    public static final String DATA_TEXT = "data";
    public static final String TITLE_TEXT = "Title";
    public static final String JPEG_EXTENSION = ".jpg";

    //Location fragment
    public static final String JSON_LATITUDE = "latitude";
    public static final String JSON_LONGTITUDE = "longtitude";

    //Fake Doctor detail Report fragment
    public static final String JSON_FAKEDOC_ID_TEXT = "fakeDocID";
    public static final String JSON_FAKEDOC_NAME_TEXT = "fakeDocName";
    public static final String JSON_REPORTED_PERSON_TEXT = "reportedPerson";
    public static final String JSON_REPORTED_CONTACT_TEXT = "contactNo";
    public static final String JSON_COMMENT_TEXT = "comment";
    //Prepare E-mail
    public static final String MSG_TYPE = "message/rfc822";
    public static final String RECEIVER_MAIL_ADD = "knowyourdoctorslmc@gmail.com";
    public static final String EMAIL_SUBJECT = "Fake doctor Details";
    public static final String LINE_SEPERATOR = "line.separator";
    //E-mail Body
    public static final String REPORTED_USER_HEADING = "Reported User Details";
    public static final String HORIZONTAL_SEPERATOR = "------------------------------";
    public static final String FULLNAME_TEXT = "Fullname : ";
    public static final String CONTACTNO_TEXT = "Contact No : ";
    public static final String FAKE_DOCTOR_HEADING = "Fake Doctor Details";
    public static final String FAKE_REGNO_TEXT = "Registration No : ";
    public static final String COMMENT_TEXT = "Comment : ";
    public static final String SENDING_MSG = "Send mail...";

    //Web Tasks
    //Doc List Load Web Task
    public static final String JSON_LOCATED_DOC = "locatedDoctors";
    public static final String JSON_DOC_NAME = "docname";
    public static final String JSON_DOC_ID = "docid";
    public static final String JSON_DOC_ADDRESS = "address";
    public static final String JSON_DOC_REG_DATE = "regdate";
    public static final String JSON_DOC_QUALIFICATION = "qualifications";
    //GET HTML content Web Task
    public static final String TABLE_ID = "r_table";
    public static final String HEADING_TYPE_2 = "h2";
    public static final String REPLACING_REG_EX = "[a-zA-Z() ]+";
    public static final String TABLE_ROW = "tr";
    public static final String TABLE_DATA = "td";
    public static final String START_WITH_EQUALS = "start=";
    public static final String NO_OF_DOC_TEXT = "Number of doctors found : ";
    public static final String NO_DATA_FOUND = "No Data Found";
    public static final String REG_NO_TEXT = "Registration Number (";
    public static final String DOESNOT_EXISTS = ") Doesn't Exist!";
    public static final String REPORT_TO_SLMC = "Do you want to report to SLMC?";
    public static final String LOADING = "Loading : ";
    public static final String COMPLETED = "% Completed";
    //Rating List Load Web Task
    public static final String JSON_DOC_RATINGS_ARRAY = "docRatings";
    public static final String JSON_DOCID = "docid";
    public static final String JSON_DOCTOR_NAME = "docname";
    public static final String JSON_ADDRESS = "address";
    public static final String JSON_REG_DATE = "regdate";
    public static final String JSON_QUALIFICATIONS = "qualifications";
    public static final String JSON_COMMENTS_ARRAY = "comments";
    public static final String JSON_COMMENT_ID = "commentid";
    public static final String JSON_COMMENT = "comment";
    public static final String JSON_COMMENT_LIKES = "commentlikes";
    public static final String REVIEWS_DOC = "Reviews: Dr. ";
    public static final String COMMENTS = " comments";

    public static final String JSON_SEND_DOCID = "docID";
    public static final String JSON_SEND_DOCTOR_NAME = "docName";
    public static final String JSON_SEND_ADDRESS = "docAddress";
    public static final String JSON_SEND_REG_DATE = "docRegDate";
    public static final String JSON_SEND_QUALIFICATIONS = "docQualifications";
    public static final String JSON_SEND_COMMENT = "comment";

    public static final String NO_COMMENTS_OF_DOC = "No Comments about Dr. ";
    //Hospital List Load Task
    public static final String JSON_HOSPITALS_ARRAY = "hospitals";
    public static final String JSON_HOSPITAL_ID = "id";
    public static final String JSON_HOSPITAL_NAME = "name";
    public static final String JSON_HOSPITAL_ADDRESS = "address";
    public static final String JSON_HOSPITAL_DISTRICT = "district";
    public static final String JSON_HOSPITAL_LATITUDE = "latitude";
    public static final String JSON_COMMENTS_LONGTITUDE = "longtitude";

    //Search Hospitals Web Task
    public static final String SRI_LANKA_CODE = "LK";
    public static final String STRING_FORMAT = "%s,%s";
    public static final String HOSPITAL_SL = " Hospital Sri Lanka";

    //Comments view adapter
    public static final String LIKE_WITH_SPACE = " Like";
    public static final String NO_OF_PEOPLE_TEXT = " people Like this";
    public static final String LIKE = "Like";
    public static final String UNLIKE = "UnLike";
    public static final String COMMENTID = "commentID";
    public static final String IS_INCREMENT = "isIncrement";

    //Doctors view adapter
    public static final String SUBMIT_MEDICAL_CENTER = "Submit Medical Center";
    public static final String JSON_SEND_LOCATION_ID = "locationID";
    public static final String TAP_TO_RATE = "Tap to rate";

}
