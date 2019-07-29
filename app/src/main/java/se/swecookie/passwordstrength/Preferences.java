package se.swecookie.passwordstrength;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

class Preferences {
    private static final String PREF_NAME = "accepted2";
    private static final String PREF_ACCEPTED = "accepted";
    private static final String PREF_IN_EU = "eu";
    private static final String PREF_UNDER_18 = "under_age";

    private final SharedPreferences prefs;

    Preferences(@NonNull Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private boolean getBoolean(@NonNull String pref) {
        boolean b = prefs.getBoolean(pref, false);
        Log.e("return", "return " + b + " for " + pref);
        return b;
    }

    boolean isAcceptedPP() {
        return getBoolean(PREF_ACCEPTED);
    }

    boolean isInEUAndUnderAgeOfConsent() {
        return getBoolean(PREF_IN_EU);
    }

    boolean isUnder18() {
        return getBoolean(PREF_UNDER_18);
    }

    void setPreferences(boolean accepted, boolean inEU, boolean under18) {
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(PREF_ACCEPTED, accepted)
                .putBoolean(PREF_IN_EU, inEU)
                .putBoolean(PREF_UNDER_18, under18)
                .apply();
    }
}
