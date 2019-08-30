package se.swecookie.passwordstrength;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final String PATH_TO_FILE = "file:///android_asset/index.html";

    private Snackbar snackbarBackPressed;
    private Button btnTips;

    private int nrOfResumes = 0;
    private boolean fromGeneration = false;
    private Preferences preferences;
    private MainFlavour mainFlavour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTips = findViewById(R.id.btnTips);

        mainFlavour = new MainActivityExtended();
        preferences = new Preferences(this);
        if (BuildConfig.FREE_VERSION) {
            if (preferences.isAcceptedPP()) {
                mainFlavour.loadAds(this, preferences);
            } else {
                finish();
                mainFlavour.openLauncher(this);
                overridePendingTransition(0, 0);
                return;
            }
        }
        loadWebView();
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
        mainFlavour.onButtonClicked(view, MainActivity.this);
    }

    private void showTips() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.password_tips))
                .setMessage(getText(R.string.tips_message))
                .setPositiveButton(getString(R.string.close), null)
                .setNeutralButton(getString(R.string.generator), (dialog, which) -> startGeneratorActivity()).show();
    }

    private void startGeneratorActivity() {
        fromGeneration = true;
        startActivity(new Intent(MainActivity.this, GeneratorActivity.class));
    }

    private void showInfo() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.information))
                .setMessage(getString(R.string.info_message))
                .setPositiveButton(getString(R.string.close), null)
                .setNeutralButton(getString(R.string.about), (dialog, which) -> showAbout()).show();
    }

    private void showAbout() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.about))
                .setIcon(R.drawable.se)
                .setMessage(getString(R.string.about_message))
                .setPositiveButton(getString(R.string.close), (dialog, which) -> dialog.dismiss())
                .setNeutralButton(getString(R.string.license), (dialog, which) -> {
                    dialog.dismiss();
                    showLicenseDialog();
                }).show();
    }

    private void showLicenseDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.script_license))
                .setMessage(getText(R.string.license_script))
                .setPositiveButton(getString(R.string.close), null);
        if (BuildConfig.FREE_VERSION) {
            b.setNeutralButton(getString(R.string.privacy_policy_title), (dialogInterface, i) -> {
                preferences.setAccepted(false, false);
                finish();
                mainFlavour.openLauncher(MainActivity.this);
            });
        }
        b.show();
    }

    @Override
    public void onResume() {
        if (fromGeneration) {
            mainFlavour.onResume(nrOfResumes);
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
