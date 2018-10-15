package se.swecookie.passwordstrength;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    private AlertDialog privacyBuilder;
    private InterstitialAd mInterstitialAd;
    private Snackbar snackbarBackPressed;
    private Button btnTips;
    private int nrOfResumes = 0;
    private FirebaseAnalytics mFirebaseAnalytics;

    private boolean fromGeneration = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTips = findViewById(R.id.btnTips);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        loadAds();

        loadWebView();

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
        sendToFirebase("Show Tips");
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
        sendToFirebase("Open Generator");
        fromGeneration = true;
        startActivity(new Intent(MainActivity.this, GeneratorActivity.class));
    }

    private void showInfo() {
        sendToFirebase("Show Info");
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
        sendToFirebase("Show About");
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
        sendToFirebase("Show License");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Script License");
        builder.setMessage(getText(R.string.license));
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Privacy Policy");
        builder.setMessage(getString(R.string.privacy_policy_message));
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setAcceptedPP(true);
            }
        });
        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setAcceptedPP(false);
                finish();
            }
        });
        builder.setNeutralButton("Read it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String privacyPolicy = "https://www.swecookie.se/apps/privacy-policies/HSIMP-PP.pdf";
                final Uri uri = Uri.parse("http://docs.google.com/gview?embedded=true&url=" + privacyPolicy);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        builder.setCancelable(false);
        privacyBuilder = builder.create();
        privacyBuilder.show();
    }

    private void sendToFirebase(String btnPressed) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, btnPressed);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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
