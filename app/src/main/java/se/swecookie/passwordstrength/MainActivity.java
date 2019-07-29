package se.swecookie.passwordstrength;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final String PATH_TO_FILE = "file:///android_asset/index.html";

    private InterstitialAd mInterstitialAd;
    private Snackbar snackbarBackPressed;
    private Button btnTips;

    private int nrOfResumes = 0;
    private boolean fromGeneration = false;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTips = findViewById(R.id.btnTips);

        preferences = new Preferences(this);
        if (preferences.isAcceptedPP()) {
            loadWebView();
            loadAds();
        } else {
            finish();
            startActivity(new Intent(this, LauncherActivity.class));
            overridePendingTransition(0, 0);
        }
    }

    private void loadAds() {
        MobileAds.initialize(this, Constants.admobAppID);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest.Builder adRequest = new AdRequest.Builder();

        Bundle extras = new Bundle();
        extras.putBoolean("tag_for_under_age_of_consent", preferences.isInEUAndUnderAgeOfConsent());
        extras.putString("max_ad_content_rating", preferences.isUnder18() ? "T" : "MA");

        mAdView.loadAd(adRequest.addNetworkExtrasBundle(AdMobAdapter.class, extras).build());

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Constants.interstitialAdID);
        mInterstitialAd.loadAd(adRequest.build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load new ad
                AdRequest.Builder adRequest = new AdRequest.Builder();
                mInterstitialAd.loadAd(adRequest.build());
                super.onAdClosed();
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView() {
        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(PATH_TO_FILE);
    }

    public void onButtonClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btnTips:
                showTips();
                break;
            case R.id.btnInfo:
                showInfo();
                break;
            case R.id.btnGenerator:
                startGeneratorActivity();
                break;
        }
    }

    private void showTips() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.password_tips))
                .setMessage(getText(R.string.tips_message))
                .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(getString(R.string.generator), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startGeneratorActivity();
                    }
                }).show();
    }

    private void startGeneratorActivity() {
        fromGeneration = true;
        startActivity(new Intent(MainActivity.this, GeneratorActivity.class));
    }

    private void showInfo() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.information))
                .setMessage(getString(R.string.info_message))
                .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(getString(R.string.about), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showAbout();
                    }
                }).show();
    }

    private void showAbout() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.about))
                .setIcon(R.drawable.se)
                .setMessage(getString(R.string.about_message))
                .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(getString(R.string.license), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showLicenseDialog();
                    }
                }).show();
    }

    private void showLicenseDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.script_license))
                .setMessage(getText(R.string.license_script))
                .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(getString(R.string.privacy_policy_title), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        preferences.setPreferences(false, false, false);
                        finish();
                        startActivity(new Intent(MainActivity.this, LauncherActivity.class));
                    }
                }).show();
    }

    @Override
    public void onResume() {
        if (fromGeneration) {
            if (mInterstitialAd.isLoaded() && nrOfResumes % 2 == 0) {
                mInterstitialAd.show();
            }
            nrOfResumes++;
            fromGeneration = false;
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (snackbarBackPressed != null && snackbarBackPressed.isShownOrQueued()) {
            finish();
        } else {
            snackbarBackPressed = Snackbar.make(btnTips, getString(R.string.back_button_message), 2000);
            View snackBarView = snackbarBackPressed.getView();
            snackBarView.setBackgroundColor(Color.RED);
            TextView textView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbarBackPressed = Snackbar.make(btnTips, "Press the back button again to exit!", 2000);
            snackbarBackPressed.show();
        }

    }

}
