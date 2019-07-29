package se.swecookie.passwordstrength;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {
    private CheckBox cBEU, cBUnderAge;
    private Button btnAccept;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        CheckBox cBAgree = findViewById(R.id.cBAgree);
        cBEU = findViewById(R.id.cBEU);
        cBUnderAge = findViewById(R.id.cBUnderAge);
        btnAccept = findViewById(R.id.btnAccept);
        preferences = new Preferences(this);

        cBAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                btnAccept.setEnabled(isChecked);
            }
        });
        cBEU.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cBUnderAge.setEnabled(!isChecked);
                if (isChecked) {
                    cBUnderAge.setChecked(true);
                }
            }
        });
        if (preferences.isAcceptedPP()) {
            onFinish();
        }
    }

    public void onButtonClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btnDecline:
                finish();
                break;
            case R.id.btnAccept:
                onAccept();
                break;
        }
    }

    private void onAccept() {
        preferences.setPreferences(true, cBEU.isChecked(), cBUnderAge.isChecked());
        onFinish();
    }

    private void onFinish() {
        finish();
        startActivity(new Intent(LauncherActivity.this, MainActivity.class));
        overridePendingTransition(0,0);
    }
}
