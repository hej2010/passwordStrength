package se.swecookie.passwordsstrength;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import se.swecookie.passwordstrength.Constants;
import se.swecookie.passwordstrength.LauncherActivity;
import se.swecookie.passwordstrength.MainActivity;
import se.swecookie.passwordstrength.MainFlavour;
import se.swecookie.passwordstrength.Preferences;
import se.swecookie.passwordstrength.R;

public class MainActivityExtended implements MainFlavour {
    private InterstitialAd mInterstitialAd;

    public MainActivityExtended() {
        mInterstitialAd = null;
    }

    @Override
    public void loadAds(MainActivity activity, Preferences preferences) {
        MobileAds.initialize(activity, Constants.admobAppID);

        AdView mAdView = activity.findViewById(R.id.adView);
        AdRequest.Builder adRequest = new AdRequest.Builder();

        Bundle extras = new Bundle();
        if (preferences.noPersonalisedAds()) {
            extras.putString("npa", "1");
        }

        ViewGroup.LayoutParams params = mAdView.getLayoutParams();
        params.height = AdSize.SMART_BANNER.getHeightInPixels(activity);
        mAdView.setLayoutParams(params);

        mAdView.loadAd(adRequest.addNetworkExtrasBundle(AdMobAdapter.class, extras).build());

        mInterstitialAd = new InterstitialAd(activity);
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

    @Override
    public void openLauncher(AppCompatActivity activity) {
        activity.startActivity(new Intent(activity, LauncherActivity.class));
    }

    @Override
    public void onResume(int nrOfResumes) {
        if (mInterstitialAd.isLoaded() && nrOfResumes % 2 == 0) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onButtonClicked(View view, MainActivity activity) {
        if (view.getId() == R.id.txtAdFree) {
            showAdFree(activity);
        }
    }

    private void showAdFree(MainActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.main_remove_ads_title));
        builder.setMessage(activity.getString(R.string.main_remove_ads_message));
        builder.setPositiveButton(activity.getString(R.string.main_remove_ads_open), (dialog, which) -> {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=se.swecookie.passwordsstrength.pro");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(intent);
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
