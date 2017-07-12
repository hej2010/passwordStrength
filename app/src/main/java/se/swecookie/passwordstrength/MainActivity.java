package se.swecookie.passwordstrength;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private AlertDialog privacyBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadAds();

        loadWebView();

        if (!checkIfAcceptedPP()) {
            displayPrivacyPolicyNotification();
        }
    }

    private void loadAds() {
        // ca-app-pub-2831297200743176~9657558447
        MobileAds.initialize(this, Constants.getAdID());

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView() {
        WebView webView = (WebView) findViewById(R.id.webview);
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
        builder.setMessage(getString(R.string.license));
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean checkIfAcceptedPP() {
        SharedPreferences prefs = getSharedPreferences("accepted", MODE_PRIVATE);
        return prefs.getBoolean("acceptedPP", false);
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

    @Override
    public void onBackPressed() {
        displayExitDialog();
    }

    private void displayExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit?");
        builder.setMessage(getString(R.string.exit));
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (privacyBuilder != null && !checkIfAcceptedPP() && !privacyBuilder.isShowing()) {
            displayPrivacyPolicyNotification();
        }
    }

}
