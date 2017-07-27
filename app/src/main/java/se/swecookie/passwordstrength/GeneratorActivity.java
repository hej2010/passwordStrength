package se.swecookie.passwordstrength;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Random;

public class GeneratorActivity extends AppCompatActivity {

    private TextView txtSeekBar, txtPassword, txtNotice;
    private int passwordLength = 16;
    private Button btnGenerate, btnCopy;
    private CheckBox cBShowAdvanced, cBLowerCase, cBUpperCase, cBNumbers, cBSpecialChar;
    private LinearLayout lLAdvancedOptions;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        loadAds();

        initLayout();

        onBtnGenerateClicked();
    }

    private void initLayout() {
        txtSeekBar = (TextView) findViewById(R.id.txtSeekBar);
        txtPassword = (TextView) findViewById(R.id.txtPassword);
        txtNotice = (TextView) findViewById(R.id.txtNotice);
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnGenerate = (Button) findViewById(R.id.btnGenerate);
        btnCopy = (Button) findViewById(R.id.btnCopy);
        btnCopy.setEnabled(false);

        cBShowAdvanced = (CheckBox) findViewById(R.id.cBShowAdvanced);
        cBLowerCase = (CheckBox) findViewById(R.id.cBLowerCase);
        cBUpperCase = (CheckBox) findViewById(R.id.cBUpperCase);
        cBNumbers = (CheckBox) findViewById(R.id.cBNumbers);
        cBSpecialChar = (CheckBox) findViewById(R.id.cBSpecialChar);
        lLAdvancedOptions = (LinearLayout) findViewById(R.id.lLAdvancedOptions);
        lLAdvancedOptions.setVisibility(View.GONE);
        cBLowerCase.setChecked(true);
        cBUpperCase.setChecked(true);
        cBNumbers.setChecked(true);
        cBSpecialChar.setChecked(true);
        cBShowAdvanced.setChecked(false);

        cBShowAdvanced.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    lLAdvancedOptions.setVisibility(View.VISIBLE);
                } else {
                    lLAdvancedOptions.setVisibility(View.GONE);
                }
            }
        });

        // So that all cannot be disabled at the same time
        cBLowerCase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b && !cBUpperCase.isChecked() && !cBNumbers.isChecked() && !cBSpecialChar.isChecked()) {
                    btnGenerate.setEnabled(false);
                }
                if (b && passwordLength > 0) {
                    btnGenerate.setEnabled(true);
                }
            }
        });

        cBUpperCase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b && !cBLowerCase.isChecked() && !cBNumbers.isChecked() && !cBSpecialChar.isChecked()) {
                    btnGenerate.setEnabled(false);
                }
                if (b && passwordLength > 0) {
                    btnGenerate.setEnabled(true);
                }
            }
        });

        cBNumbers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b && !cBUpperCase.isChecked() && !cBLowerCase.isChecked() && !cBSpecialChar.isChecked()) {
                    btnGenerate.setEnabled(false);
                }
                if (b && passwordLength > 0) {
                    btnGenerate.setEnabled(true);
                }
            }
        });

        cBSpecialChar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b && !cBUpperCase.isChecked() && !cBNumbers.isChecked() && !cBLowerCase.isChecked()) {
                    btnGenerate.setEnabled(false);
                }
                if (b && passwordLength > 0) {
                    btnGenerate.setEnabled(true);
                }
            }
        });

        txtSeekBar.setText(seekBar.getProgress() + "/" + seekBar.getMax() + " characters");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                passwordLength = i;
                txtSeekBar.setText(passwordLength + "/" + seekBar.getMax() + " characters");
                if (passwordLength == 0) {
                    btnGenerate.setEnabled(false);
                } else if (cBLowerCase.isChecked() || cBUpperCase.isChecked() || cBNumbers.isChecked() || cBSpecialChar.isChecked()){
                    btnGenerate.setEnabled(true);
                }
                if (passwordLength < 8) {
                    txtNotice.setVisibility(View.VISIBLE);
                    txtNotice.setText(getText(R.string.pwd_notice_bad));
                    txtNotice.setTextColor(Color.RED);
                } else if (passwordLength >= 8 && passwordLength < 16){
                    txtNotice.setVisibility(View.INVISIBLE);
                } else {
                    txtNotice.setVisibility(View.VISIBLE);
                    txtNotice.setText(getText(R.string.pwd_notice_good));
                    txtNotice.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void loadAds() {
        MobileAds.initialize(this, Constants.getBannerAdID());

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void onBtnGenerateClicked() {
        sendToFirebase("Generate Clicked");
        String allowed = "";
        if (cBLowerCase.isChecked()) {
            allowed = allowed + Constants.getAllowedCharsLower();
        }
        if (cBUpperCase.isChecked()) {
            allowed = allowed + Constants.getAllowedCharsUpper();
        }
        if (cBNumbers.isChecked()) {
            allowed = allowed + Constants.getAllowedCharsNumbers();
        }
        if (cBSpecialChar.isChecked()) {
            allowed = allowed + Constants.getAllowedCharsSpecial();
        }

        char[] allowedCharsArray = allowed.toCharArray();
        char[] chars = new char[passwordLength];
        Random random = new Random();

        int length = allowed.length();
        for (int i = 0; i < passwordLength; i++) {
            chars[i] = allowedCharsArray[random.nextInt(length)];
        }

        txtPassword.setText(chars, 0, passwordLength);
        btnCopy.setEnabled(true);
    }

    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.btnGenerate:
                onBtnGenerateClicked();
                break;
            case R.id.btnCopy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(txtPassword.getText(), txtPassword.getText());
                clipboard.setPrimaryClip(clip);
                Snackbar.make(btnCopy, getString(R.string.pwd_copied), Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    private void sendToFirebase(String btnPressed) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, btnPressed);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
    }

}
