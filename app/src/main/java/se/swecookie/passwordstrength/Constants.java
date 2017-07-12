package se.swecookie.passwordstrength;

class Constants {
    private final static String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#£¤$%€&/{([)]=}?+\\^¨~*,;.:-_<> ";
    private static final String bannerAdID = "ca-app-pub-2831297200743176/2134291646";
    private static final String interstitialAdID = "ca-app-pub-2831297200743176/5422175246";

    static String getAllowedChars() {
        return allowedChars;
    }

    static String getBannerAdID() {
        return bannerAdID;
    }

    static String getInterstitialAdID() {
        return interstitialAdID;
    }
}
