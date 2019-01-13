package se.swecookie.passwordstrength;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {
    private AlertDialog privacyBuilder;
    private InterstitialAd mInterstitialAd;
    private Snackbar snackbarBackPressed;
    private Button btnTips;
    private int nrOfResumes = 0;

    private boolean fromGeneration = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTips = findViewById(R.id.btnTips);

        loadWebView();

        loadAds();

        if (notAcceptedPP()) {
            displayPrivacyPolicyNotification();
        }
    }

    private void loadAds() {
        MobileAds.initialize(this, Constants.admobAppID);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest.Builder adRequest = new AdRequest.Builder();

        mAdView.loadAd(adRequest.build());

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
        webView.loadUrl("file:///android_asset/index.html");
    }

    public void onButtonClicked(View view) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Password tips");
        builder.setMessage(getText(R.string.tips_message));
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Generator", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startGeneratorActivity();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startGeneratorActivity() {
        fromGeneration = true;
        startActivity(new Intent(MainActivity.this, GeneratorActivity.class));
    }

    private void showInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Information");
        builder.setMessage(getString(R.string.info_message));
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("About", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showAbout();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("About");
        builder.setIcon(R.drawable.se);
        builder.setMessage(getString(R.string.about_message));
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("License", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showLicenseDialog();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showLicenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Script License");
        builder.setMessage(getText(R.string.license));
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setNeutralButton(getString(R.string.privacy_policy_title), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean notAcceptedPP() {
        SharedPreferences prefs = getSharedPreferences("accepted", MODE_PRIVATE);
        return !prefs.getBoolean("acceptedPP", false);
    }

    private void setAcceptedPP(boolean accepted) {
        SharedPreferences.Editor editor = getSharedPreferences("accepted", MODE_PRIVATE).edit();
        editor.putBoolean("acceptedPP", accepted);
        editor.apply();
    }

    private void displayPrivacyPolicyNotification() {
        privacyBuilder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.privacy_policy_title))
                .setMessage(getString(R.string.privacy_policy_message))
                .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setAcceptedPP(true);
                    }
                })
                .setNegativeButton(getString(R.string.decline), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setAcceptedPP(false);
                        finish();
                    }
                })
                .setNeutralButton(getString(R.string.read_it), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onResume() {
        if (privacyBuilder != null && notAcceptedPP() && !privacyBuilder.isShowing()) {
            displayPrivacyPolicyNotification();
        }
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
            snackbarBackPressed = Snackbar.make(btnTips, "Press the back button again to exit!", 2000);
            View snackBarView = snackbarBackPressed.getView();
            snackBarView.setBackgroundColor(Color.RED);
            TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbarBackPressed.show();
        }

    }

}
