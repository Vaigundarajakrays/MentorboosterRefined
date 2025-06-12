package com.mentorboosters.app.util;

public final class Constant {

    private Constant(){
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    //HTTP
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String OPTIONS = "OPTIONS";

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String CONTENT_TYPE = "Content-Type";

    //Login success
    public static final String LOGIN_SUCCESS= "Login Successful";

    // JWT Token
    public static final String JWT_TOKEN = "jwtToken";

    //Status code
    public static final int SUCCESS_CODE=200;
    public static final int FORBIDDEN_CODE = 403;
    public static final int ERROR_CODE = 500;
    public static final int CONFLICT_CODE = 409;
    public static final int BAD_REQUEST = 400;


    //Status boolean true or false
    public static final boolean STATUS_TRUE=true;
    public static final boolean STATUS_FALSE=false;

    //General success messages
    public static final String SUCCESSFULLY_ADDED = "Successfully Added.";
    public static final String OTP_SENT_SUCCESS = "OTP sent successfully";
    public static final String PASSWORD_CHANGE_SUCCESS = "Password changed successfully";
    public static final String PASSWORD_UPDATED = "Password updated";

    //Mentor messages
    public static final String LOADED_ALL_MENTOR_DETAILS = "Loaded all the Mentor Details.";
    public static final String NO_MENTORS_AVAILABLE = "No mentors available";
    public static final String MENTOR_UPDATED_SUCCESS = "Mentor updated successfully";
    public static final String MENTOR_DELETED_SUCCESSFULLY = "Mentor Deleted Successfully";
    public static final String LOADED_ALL_VERIFIED_MENTOR_DETAILS = "Loaded all the verified mentor Details.";
    public static final String NO_VERIFIED_MENTORS_AVAILABLE = "No verified mentors available";
    public static final String NO_TOP_RATED_MENTORS_AVAILABLE = "No top rated mentors available";
    public static final String LOADED_ALL_TOP_RATED_MENTORS_DETAILS = "Loaded all the top rated mentors Details.";
    public static final String NO_TOP_MENTORS_AVAILABLE = "No top mentors available";
    public static final String LOADED_ALL_TOP_MENTORS_DETAILS = "Loaded all the top mentors Details.";
    public static final String BOOKING_CHECK_COMPLETED = "Upcoming booking check completed";

    //Category messages
    public static final String LOADED_ALL_CATEGORIES = "Loaded all the categories Details.";
    public static final String NO_CATEGORIES_AVAILABLE = "No categories available";
    public static final String CATEGORY_UPDATED_SUCCESS = "Category updated successfully";
    public static final String CATEGORY_DELETED_SUCCESSFULLY = "Category Deleted Successfully";

    //Connect methods messages
    public static final String LOADED_ALL_CONNECT_METHODS = "Loaded all the connect methods.";
    public static final String NO_CONNECT_METHODS_AVAILABLE = "No connect methods available";

    //Time Slot messages
    public static final String LOADED_ALL_TIME_SLOTS_FOR_MENTORS = "Loaded all time slots for the mentor";
    public static final String NO_TIME_SLOTS_AVAILABLE_FOR_MENTOR = "No time slots available for the mentor";

    //Booking messages
    public static final String LOADED_ALL_BOOKED_SLOTS_FOR_USER = "Loaded all booked slots for the user";
    public static final String NO_BOOKED_SLOTS_AVAILABLE = "No booked slots available";
    public static final String LOADED_ALL_BOOKED_SLOTS_FOR_MENTOR = "Loaded all booked slots for the mentor";

    //Chat messages
    public static final String NO_CHAT_UNREAD_MESSAGES_AVAILABLE = "No chat or unread messages available";
    public static final String NO_CHAT_MESSAGES_AVAILABLE = "No chat messages available";
    public static final String LOADED_ALL_CHAT_MESSAGES = "Loaded all the chat messages.";
    public static final String NO_UNREAD_MESSAGES_AVAILABLE = "No unread messages available";
    public static final String LOADED_ALL_UNREAD_MESSAGES = "Loaded all the unread messages.";
    public static final String MESSAGES_MARKED_AS_READ = "Messages marked as read";

    //Notification messages
    public static final String NO_NOTIFICATIONS_AVAILABLE_FOR_MENTOR = "No Notifications available for mentor";
    public static final String LOADED_ALL_NOTIFICATIONS_FOR_MENTOR = "Loaded all the notifications for mentor.";
    public static final String NO_NOTIFICATIONS_AVAILABLE_FOR_USER = "No Notifications available for user";
    public static final String LOADED_ALL_NOTIFICATIONS_FOR_USER = "Loaded all the notifications for the user.";
    public static final String LOADED_NOTIFICATION_FOR_ID = "Loaded notification for the id: ";
    public static final String NO_NOTIFICATIONS_AVAILABLE = "No Notifications available";
    public static final String LOADED_ALL_NOTIFICATIONS = "Loaded all the notifications.";
    public static final String SUCCESSFULLY_UPDATED_AS_READ = "Successfully updated the notification as read";
    public static final String ALREADY_MARKED_AS_TRUE = "Already marked as true";

    //User messages
    public static final String LOADED_USER_DETAILS = "Loaded user details.";
    public static final String USER_UPDATED = "User updated successfully.";
    public static final String USER_DELETED = "User deleted successfully.";

    //Payment massages
    public static final String LOADED_SESSION_URL = "Successfully loaded session url";

    //Seminar messages
    public static final String LOADED_SEMINAR_NOTES = "Loaded seminar notes.";
    public static final String NOTES_UPDATED = "Seminar notes updated successfully.";
    public static final String NO_NOTES_AVAILABLE = "No Seminar notes available";
    public static final String NOTES_DELETED_SUCCESSFULLY = "Seminar notes deleted Successfully";

    //Community posts messages
    public static final String NO_POSTS_AVAILABLE = "No community posts available";
    public static final String LOADED_COMMUNITY_POSTS = "Community posts loaded successfully";
    public static final String POSTS_DELETED_SUCCESSFULLY = "Community posts deleted Successfully";

    //Comment messages
    public static String NO_COMMENTS = "No comments found for this post";
    public static final String LOADED_COMMENTS = "Comments loaded successfully";

    //General errors
    public static final String NO_DATA="No Data found";
    public static final String INVALID_EMAIL_PASSWORD = "Invalid email or password";
    public static final String ERROR_LOGGING_IN = "Error while logging in: ";
    public static final String ERROR_UPDATING_NEW_PASSWORD = "Error when updating new password: ";

    //Database error
    public static final String SERVER_ERROR="Something went wrong";

    //General Errors
    public static final String EMAIL_ALREADY_EXISTS = "Email Already Exists";
    public static final String USERNAME_ALREADY_EXISTS = "Username Already Exists";
    public static final String ERROR_SENDING_OTP = "Error while sending OTP: ";
    public static final String EMAIL_OR_PHONE_NUMBER_ALREADY_EXISTS = "Email or phone number Already Exists";
    public static final String PHONE_NUMBER_REQUIRED = "Phone number required";

    // Authentication Errors
    public static final String TOKEN_EXPIRED = "Token Expired";
    public static final String SESSION_EXPIRED = "Your session has expired.";
    public static final String AUTHENTICATION_ERROR = "Authentication Error";
    public static final String AUTHENTICATION_FAILED = "Authentication failed";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INCORRECT_PASSWORD = "Incorrect password";
    public static final String ERROR = "Error occured";

    // Email Errors
    public static final String EMAIL_AUTH_FAILED_SENDER = "Email authentication failed: Invalid sender address";
    public static final String EMAIL_AUTH_FAILED_RECIPIENT = "Email authentication failed: Verify recipient email";
    public static final String EMAIL_SENDING_FAILED = "Unable to send email";

    // Payment Errors
    public static final String PAYMENT_ERROR = "Something went wrong with payment. Please try again";
    public static final String PAYMENT_SESSION_CREATION_FAILED = "Unable to create payment Session";
    public static final String PAYMENT_FAILED_ERROR="Payment process failed";


    // JWT Errors
    public static final String JWT_SIGNATURE_MISMATCH = "JWT signature does not match";
    public static final String INVALID_JWT_SIGNATURE = "Invalid JWT Signature";
    public static final String JWT_FORMAT_INCORRECT = "JWT token format is incorrect.";
    public static final String MALFORMED_JWT_TOKEN = "Malformed JWT Token";
    public static final String UNSUPPORTED_JWT_TOKEN = "Unsupported JWT Token";
    public static final String JWT_NOT_SUPPORTED = "JWT token is not supported.";

    //Mentor Errors
    public static final String ERROR_FETCHING_MENTORS = "Error while fetching mentors: ";
    public static final String ERROR_ADDING_MENTORS = "Error while adding mentors: ";
    public static final String MENTOR_NOT_FOUND_WITH_ID = "Mentor not found with ID: ";
    public static final String ERROR_UPDATING_MENTORS = "Error while updating mentors: ";
    public static final String ERROR_DELETING_MENTORS = "Error while deleting mentors: ";
    public static final String ERROR_FETCHING_VERIFIED_MENTORS = "Error while fetching verified mentors: ";
    public static final String ERROR_FETCHING_TOP_RATED_MENTORS = "Error while fetching top rated mentors: ";
    public static final String ERROR_FETCHING_TOP_MENTORS = "Error while fetching top mentors: ";

    //Category Errors
    public static final String ERROR_FETCHING_CATEGORIES = "Error while fetching categories: ";
    public static final String CATEGORY_NOT_FOUND_WITH_ID = "Category not found with ID: ";
    public static final String CATEGORY_ALREADY_EXISTS = "Category Already Exists";
    public static final String ERROR_ADDING_CATEGORY = "Error while adding category: ";
    public static final String ERROR_UPDATING_CATEGORY = "Error while updating category: ";
    public static final String ERROR_DELETING_CATEGORY = "Error while deleting category: ";
    public static final String CATEGORY_ID_MISMATCH = "Category id in the request URL path and category id in the request body do not match";


    //Connect methods Errors
    public static final String ERROR_FETCHING_CONNECT_METHODS = "Error while fetching connect methods: ";
    public static final String CONNECT_METHOD_ALREADY_EXISTS = "Connect method Already Exists";
    public static final String ERROR_ADDING_CONNECT_METHOD = "Error while adding connect method: ";

    //Certificate errors
    public static final String CERTIFICATE_NOT_FOUND_WITH_ID = "Certificate not found with ID: ";

    //Experience errors
    public static final String EXPERIENCE_NOT_FOUND_WITH_ID = "Experience not found with ID: ";

    //Timeslot errors
    public static final String TIMESLOT_NOT_FOUND_WITH_ID = "Timeslot not found with ID: ";
    public static final String ERROR_FETCHING_TIME_SLOTS_FOR_MENTOR = "Error while fetching time slots for the mentor: ";

    //Booking errors
    public static final String OVERLAPS_WITH_EXISTING_BOOKED_SLOT = "This time slot overlaps with an existing booking.";
    public static final String ERROR_BOOKING_TIME_SLOT = "Error while booking with the time slot: ";
    public static final String ERROR_FETCHING_BOOKED_SLOTS = "Error while fetching the booked slots: ";
    public static final String ERROR_CHECKING_TIME_SLOT = "Error while checking the time slot: ";

    //Signup error
    public static final String ERROR_DURING_SIGN_UP = "Error while signing up: ";

    //User errors
    public static final String USER_NOT_FOUND_WITH_ID = "User not found with ID: ";
    public static final String ERROR_UPDATING_USER = "Error while updating user: ";
    public static final String USER_NOT_FOUND_WITH_EMAIL = "User not found with email: ";
    public static final String ERROR_DELETING_USER = "Error while deleting user: ";

    //Chat message errors
    public static final String ERROR_ADDING_CHAT_MESSAGE = "Error while adding chat message: ";
    public static final String ERROR_FETCHING_CHAT_MESSAGES = "Error while fetching chat messages: ";
    public static final String ERROR_FETCHING_UNREAD_MESSAGES = "Error while fetching unread messages: ";
    public static final String ERROR_MARKING_MESSAGES_AS_READ = " Error while marking messages as read: ";

    //Notification errors
    public static final String ERROR_ADDING_NOTIFICATION = "Error while adding Notification: ";
    public static final String ERROR_FETCHING_NOTIFICATIONS_FOR_MENTOR = "Error while fetching Notifications: ";
    public static final String ERROR_FETCHING_NOTIFICATIONS_FOR_USER = "Error while fetching Notifications for user: ";
    public static final String NOTIFICATION_NOT_FOUND_BY_ID = "Notification not found with id: ";
    public static final String ERROR_FETCHING_NOTIFICATIONS = "Error while fetching Notifications: ";
    public static final String ERROR_UPDATING_NOTIFICATION_AS_READ = "Error while updating notification as read: ";

    //Review error
    public static final String ERROR_ADDING_REVIEW = "Error while adding review: ";

    //Seminar notes error
    public static final String ERROR_SAVING_NOTES = "Error while saving seminar notes: ";
    public static final String TITLE_ALREADY_EXISTS = "Title Already Exists";
    public static final String NOTES_NOT_FOUND_WITH_ID = "Seminar notes not found with ID: ";
    public static final String ERROR_UPDATING_NOTES = "Error while updating seminar notes: ";
    public static final String ERROR_FETCHING_NOTES = "Error while fetching Seminar notes: ";
    public static final String ERROR_DELETING_NOTES = "Error while deleting seminar notes: ";
    public static final String NOTES_NOT_FOUND_WITH_USER_ID = "Seminar notes not found with USER ID: ";

    // Community post error
    public static final String ERROR_SAVING_POST = "Error while saving community post: ";
    public static final String ERROR_FETCHING_POST = "Error fetching community posts: ";
    public static final String POST_NOT_FOUND_WITH_ID = "Post not found with ID: ";

    //Comment error
    public static final String ERROR_SAVING_COMMENT = "Error while saving comment: ";
    public static final String ERROR_FETCHING_COMMENTS = "Error while fetching comments: ";

    //JWT Token error
    public static final String INVALID_JWT_TOKEN = "Invalid or tampered JWT token.";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";

}
