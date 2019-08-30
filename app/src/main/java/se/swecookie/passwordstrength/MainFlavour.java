package se.swecookie.passwordstrength;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public interface MainFlavour {
    void loadAds(MainActivity activity, Preferences preferences);
    void openLauncher(AppCompatActivity activity);
    void onResume(int nrOfResumes);
    void onButtonClicked(View view, MainActivity activity);
}
