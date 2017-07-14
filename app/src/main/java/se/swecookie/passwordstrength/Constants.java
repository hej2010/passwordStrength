package se.swecookie.passwordstrength;

class Constants {
    private final static String allowedCharsLower = "abcdefghijklmnopqrstuvwxyz";
    private final static String allowedCharsUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String allowedCharsNumbers = "0123456789";
    private final static String allowedCharsSpecial = "!@#£¤$%€&/{([)]=}?+\\^¨~*,;.:-_<> ";
    private static final String bannerAdID = "ca-app-pub-2831297200743176/2134291646";
    private static final String interstitialAdID = "ca-app-pub-2831297200743176/5422175246";

    static String getAllowedCharsLower() {
        return allowedCharsLower;
    }

    static String getAllowedCharsUpper() {
        return allowedCharsUpper;
    }

    static String getAllowedCharsNumbers() {
        return allowedCharsNumbers;
    }

    static String getAllowedCharsSpecial() {
        return allowedCharsSpecial;
    }

    static String getBannerAdID() {
        return bannerAdID;
    }

    static String getInterstitialAdID() {
        return interstitialAdID;
    }
}
