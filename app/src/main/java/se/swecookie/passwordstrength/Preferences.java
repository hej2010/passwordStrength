package se.swecookie.passwordstrength;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

class Preferences {
    private static final String PREF_NAME = "accepted3";
    private static final String PREF_ACCEPTED = "accepted";
    private static final String PREF_PERSONALISED_ADS = "pa";

    private final SharedPreferences prefs;

    Preferences(@NonNull Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private boolean getBoolean(@NonNull String pref) {
        return prefs.getBoolean(pref, false);
    }

    boolean isAcceptedPP() {
        return getBoolean(PREF_ACCEPTED);
    }

    boolean noPersonalisedAds() {
        return getBoolean(PREF_PERSONALISED_ADS);
    }

    void setAccepted(boolean accepted, boolean personalisedAds) {
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(PREF_ACCEPTED, accepted)
                .putBoolean(PREF_PERSONALISED_ADS, personalisedAds)
                .apply();
    }
}
