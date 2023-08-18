package se.swecookie.passwordstrength;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivityExtended implements MainFlavour {
    private InterstitialAd mInterstitialAd;
    private final AppCompatActivity activity;

    MainActivityExtended(AppCompatActivity activity) {
        mInterstitialAd = null;

        this.activity = activity;
    }

    @Override
    public void loadAds(MainActivity activity, Preferences preferences) {
        MobileAds.initialize(activity, initializationStatus -> {
        });

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

        loadInterstitialAd(activity);
    }

    private void loadInterstitialAd(MainActivity activity) {
        InterstitialAd.load(activity, Constants.interstitialAdID, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                super.onAdLoaded(interstitialAd);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mInterstitialAd = null;
                        super.onAdDismissedFullScreenContent();
                        loadInterstitialAd(activity);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        mInterstitialAd = null;
                        loadInterstitialAd(activity);
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
                loadInterstitialAd(activity);
                super.onAdFailedToLoad(loadAdError);
            }
        });
    }

    @Override
    public void openLauncher(AppCompatActivity activity) {
        activity.startActivity(new Intent(activity, LauncherActivity.class));
    }

    @Override
    public void onResume(int nrOfResumes) {
        if (mInterstitialAd != null && nrOfResumes % 2 == 0) {
            mInterstitialAd.show(activity);
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
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=se.swecookie.passwordstrength.pro");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(intent);
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
