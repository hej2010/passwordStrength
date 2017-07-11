package se.swecookie.passwordstrength;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Random;

public class GeneratorActivity extends AppCompatActivity {

    private TextView txtSeekBar, txtPassword, txtNotice;
    private int passwordLength = 16;
    private Button btnGenerate, btnCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        MobileAds.initialize(this, Constants.getAdID());

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        txtSeekBar = (TextView) findViewById(R.id.txtSeekBar);
        txtPassword = (TextView) findViewById(R.id.txtPassword);
        txtNotice = (TextView) findViewById(R.id.txtNotice);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnGenerate = (Button) findViewById(R.id.btnGenerate);
        btnCopy = (Button) findViewById(R.id.btnCopy);
        btnCopy.setEnabled(false);

        txtSeekBar.setText(seekBar.getProgress() + "/" + seekBar.getMax() + " characters");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                passwordLength = i;
                txtSeekBar.setText(passwordLength + "/" + seekBar.getMax() + " characters");
                if (passwordLength == 0) {
                    btnGenerate.setEnabled(false);
                } else {
                    btnGenerate.setEnabled(true);
                }
                if (passwordLength < 8) {
                    txtNotice.setVisibility(View.VISIBLE);
                } else {
                    txtNotice.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        onBtnGenerateClicked();
    }

    private void onBtnGenerateClicked() {
        char[] allowedCharsArray = Constants.getAllowedChars().toCharArray();
        char[] chars = new char[passwordLength];
        Random random = new Random();

        for (int i = 0; i < passwordLength; i++) {
            chars[i] = allowedCharsArray[random.nextInt(Constants.getAllowedChars().length())];
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
}
