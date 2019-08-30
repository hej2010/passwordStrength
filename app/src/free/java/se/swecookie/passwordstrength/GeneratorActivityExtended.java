package se.swecookie.passwordstrength;

import android.os.Bundle;
import android.view.ViewGroup;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class GeneratorActivityExtended implements GeneratorFlavour {

    @Override
    public void loadAds(GeneratorActivity activity, Preferences preferences) {
        MobileAds.initialize(activity, Constants.bannerAdID);

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

        mAdView.loadAd(adRequest.build());
    }
}
