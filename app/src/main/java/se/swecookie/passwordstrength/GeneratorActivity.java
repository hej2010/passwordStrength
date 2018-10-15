package se.swecookie.passwordstrength;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
    private CheckBox cBLowerCase;
    private CheckBox cBUpperCase;
    private CheckBox cBNumbers;
    private CheckBox cBSpecialChar;
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

        checkSliderLength();
    }

    @SuppressLint("StringFormatInvalid")
    private void initLayout() {
        txtSeekBar = findViewById(R.id.txtSeekBar);
        txtPassword = findViewById(R.id.txtPassword);
        txtNotice = findViewById(R.id.txtNotice);
        final SeekBar seekBar = findViewById(R.id.seekBar);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnCopy = findViewById(R.id.btnCopy);
        btnCopy.setEnabled(false);

        CheckBox cBShowAdvanced = findViewById(R.id.cBShowAdvanced);
        cBLowerCase = findViewById(R.id.cBLowerCase);
        cBUpperCase = findViewById(R.id.cBUpperCase);
        cBNumbers = findViewById(R.id.cBNumbers);
        cBSpecialChar = findViewById(R.id.cBSpecialChar);
        lLAdvancedOptions = findViewById(R.id.lLAdvancedOptions);
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

        txtSeekBar.setText(getString(R.string.gener_seekbar_text, seekBar.getProgress(), seekBar.getMax()));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                passwordLength = i;
                txtSeekBar.setText(getString(R.string.gener_seekbar_text, passwordLength, seekBar.getMax()));
                if (passwordLength == 0) {
                    btnGenerate.setEnabled(false);
                } else if (cBLowerCase.isChecked() || cBUpperCase.isChecked() || cBNumbers.isChecked() || cBSpecialChar.isChecked()) {
                    btnGenerate.setEnabled(true);
                    onBtnGenerateClicked();
                }
                checkSliderLength();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void checkSliderLength() {
        if (passwordLength < 8) {
            txtNotice.setVisibility(View.VISIBLE);
            txtNotice.setText(getText(R.string.pwd_notice_bad));
            txtNotice.setTextColor(getResources().getColor(R.color.colorRed));
        } else if (passwordLength < 16) {
            txtNotice.setVisibility(View.INVISIBLE);
        } else {
            txtNotice.setVisibility(View.VISIBLE);
            txtNotice.setText(getText(R.string.pwd_notice_good));
            txtNotice.setTextColor(Color.GREEN);
        }
    }

    private void loadAds() {
        MobileAds.initialize(this, Constants.bannerAdID);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void onBtnGenerateClicked() {
        sendToFirebase();
        String allowed = "";
        if (cBLowerCase.isChecked()) {
            allowed = allowed + Constants.allowedCharsLower;
        }
        if (cBUpperCase.isChecked()) {
            allowed = allowed + Constants.allowedCharsUpper;
        }
        if (cBNumbers.isChecked()) {
            allowed = allowed + Constants.allowedCharsNumbers;
        }
        if (cBSpecialChar.isChecked()) {
            allowed = allowed + Constants.allowedCharsSpecial;
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
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Snackbar.make(btnCopy, getString(R.string.pwd_copied), Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    private void sendToFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Generate Clicked");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
    }

}
