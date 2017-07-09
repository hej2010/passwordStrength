package se.swecookie.passwordstrength;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ca-app-pub-2831297200743176~9657558447
        MobileAds.initialize(this, "ca-app-pub-2831297200743176~9657558447");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        loadWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView() {
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/index.html");
    }

    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.txtAbout:
                showAbout();
                break;
        }
    }

    private void showAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("About");
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
}
