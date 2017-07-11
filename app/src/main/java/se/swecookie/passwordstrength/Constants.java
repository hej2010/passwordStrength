package se.swecookie.passwordstrength;

class Constants {
    private final static String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#£¤$%€&/{([)]=}?+\\^¨~*,;.:-_<> ";
    private static final String adID = "ca-app-pub-2831297200743176~9657558447";

    static String getAllowedChars() {
        return allowedChars;
    }

    static String getAdID() {
        return adID;
    }
}
