package se.swecookie.passwordstrength;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

class PrefManager {
    private final SharedPreferences pref;

    // Shared preferences file name
    private static final String PREF_NAME = "welcome-screen";

    private static final String IS_FIRST_TIME_LAUNCH = "isFirstTimeLaunch";

    PrefManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    void setFirstTimeLaunch() {
        pref.edit().putBoolean(IS_FIRST_TIME_LAUNCH, false).apply();
    }

    boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}
