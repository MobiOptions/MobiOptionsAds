package com.mobioptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.RootApplication;
import com.osama.mobioptionsads.banner.MobiBannerListener;
import com.osama.mobioptionsads.banner.MobiOptionBannerError;
import com.osama.mobioptionsads.banner.MobiOptionsBanner;
import com.osama.mobioptionsads.banner.MobiOptionsBannerSize;
import com.osama.mobioptionsads.banner.size.AdmobBannerSize;
import com.osama.mobioptionsads.banner.size.FacebookBannerSize;
import com.osama.mobioptionsads.banner.size.UnityBannerSize;
import com.osama.mobioptionsads.interstitial.MobiInterstitialError;
import com.osama.mobioptionsads.interstitial.MobiInterstitialListener;
import com.osama.mobioptionsads.interstitial.MobiOptionsInterstitial;
import com.osama.mobioptionsads.rewarded.MobiOptionRewardedAd;
import com.osama.mobioptionsads.rewarded.MobiRewardAdError;
import com.osama.mobioptionsads.rewarded.MobiRewardAdListener;
import com.osama.mobioptionsads.rewarded.MobiRewardAdLoadListener;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private MobiOptionsBanner banner;
    private MobiOptionsInterstitial interstitial;
    private MobiOptionRewardedAd rewardedAd;

    private final MobiRewardAdListener rewardAdListener = new
            MobiRewardAdListener() {
                @Override
                public void onRewardedAdOpened(String adsProvider) {
                    // you can check the ads provider to know from which provider
                    // the interstitial is opened (facebook, unity or admob)
                    Log.d(TAG, "onRewardedAdOpened: Rewarded ad opened, (provider: " + adsProvider + ").");
                }

                @Override
                public void onRewardedAdClosed(String adProvider) {
                    // Handle interstitial ad closed event
                    Log.d(TAG, "onRewardedAdClosed: Rewarded ad closed, (provider: " + adProvider + ").");
                }

                @Override
                public void onUserEarnedReward(String adProvider) {
                    // Interstitial was shown successfully, check the ad provider and reward the user.
                    Log.d(TAG, "onUserEarnedReward: User earned reward, (provider: " + adProvider + ").");
                }

                @Override
                public void onRewardedAdError(String adProvider, @NotNull MobiRewardAdError error) {
                    // Log the errors
                    Log.d(TAG, "onRewardedAdError: Reward Error, (provider: " + adProvider + ")." + "\n" + error.toString());
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RootApplication.setupMobiOptionsAds(null);

        LinearLayout bannerContainer = findViewById(R.id.banner_container);
        AppCompatButton interstitialButton = findViewById(R.id.show_interstitial);
        AppCompatButton rewardedAdButton = findViewById(R.id.show_rewarded_ad);

        MobiOptionsBannerSize bannerSize = new MobiOptionsBannerSize(
                new AdmobBannerSize(AdmobBannerSize.ADMOB_SMART_BANNER),
                new UnityBannerSize(320, 50),
                new FacebookBannerSize(FacebookBannerSize.FACEBOOK_BANNER_HEIGHT_50)
        );

        // setup banner
        banner = new MobiOptionsBanner(bannerContainer, bannerSize, "the_name_of_a_banner_from_your_settings");
        banner.load();


        // banner call backs
        banner.setMobiBannerListener(new MobiBannerListener() {
            @Override
            public void onLoaded(String adsProvider) {
                Log.d(TAG, "onLoaded: Banner loaded successfully \nads Provider: " + adsProvider);
            }

            @Override
            public void onClicked(String adsProvider) {
                Log.d(TAG, "onClicked: Banner Clicked successfully \nads Provider:" + adsProvider);
            }

            @Override
            public void onFailedToLoad(String adsProvider, MobiOptionBannerError error) {
                Log.d(TAG, "onFailedToLoad: Banner Failed to load \nads Provider: " + adsProvider + "\n" + error.toString());
            }

            @Override
            public void onLeftApplication(String adsProvider) {

            }
        });


        // The rewarded ads
        setUpRewardedAd();
        rewardedAdButton.setOnClickListener((v) -> {
            if (rewardedAd.isLoaded()) {
                rewardedAd.show(rewardAdListener);
                // load another one
            } else {
                Toast.makeText(MainActivity.this, "Ad not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });

        // interstitial ads
        setUpInterstitial();
        interstitialButton.setOnClickListener((v) -> {
            if (interstitial.isLoaded()) {
                interstitial.show();
                // load another one
            } else {
                Toast.makeText(MainActivity.this, "Ad not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setUpInterstitial() {
        // setup interstitial
        interstitial = new MobiOptionsInterstitial(this, "interstitial_name");
        interstitial.loadAd();
        interstitial.setMobiInterstitialListener(new MobiInterstitialListener() {
            @Override
            public void onDisplayed(String adsProvider) {

            }

            @Override
            public void onClosed(String adsProvider) {

            }

            @Override
            public void onError(String adsProvider, MobiInterstitialError error) {
                Log.d(TAG, "onError: Interstitial errors: ad provider: (" + adsProvider + "). Details: " + error.toString());
            }

            @Override
            public void onLoaded(String adsProvider) {
                // you can check the name of the ads Provider
                // here the interstitial is loaded successfully you can call the show() method
                Log.d(TAG, "onLoaded: The interstitial was loaded successfully, you can show it (provider: " + adsProvider + ").");
            }

            @Override
            public void onClicked(String adsProvider) {

            }
        });
    }


    private void setUpRewardedAd() {
        rewardedAd = new MobiOptionRewardedAd(this, "Rewarded_ad_name");
        rewardedAd.load(new MobiRewardAdLoadListener() {
            @Override
            public void onRewardedAdLoaded(String adsProvider) {
                // You can call the show on the rewarded ad with custom listener
                // you can always check if the rewarded ad is loaded or not
//                if (rewardedAd.isLoaded()) {
//                    rewardedAd.show(rewardAdListener);
//                }

                Log.d(TAG, "onRewardedAdLoaded: rewarded ad was loaded successfully");
            }

            @Override
            public void onRewardedAdFailedToLoad(String adsProvider, MobiRewardAdError error) {
                Log.d(TAG, "onRewardedAdFailedToLoad: " + error.toString());
            }
        });
    }


    @Override
    protected void onDestroy() {
        banner.destroy();
        super.onDestroy();
    }
}