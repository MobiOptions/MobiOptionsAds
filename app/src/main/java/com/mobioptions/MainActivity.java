package com.mobioptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.RootApplication;
import com.lib.mobioptionsads.MobiInitializationListener;
import com.lib.mobioptionsads.banner.MobiBannerListener;
import com.lib.mobioptionsads.banner.MobiOptionBannerError;
import com.lib.mobioptionsads.banner.MobiOptionsBanner;
import com.lib.mobioptionsads.banner.MobiOptionsBannerSize;
import com.lib.mobioptionsads.banner.size.AdmobBannerSize;
import com.lib.mobioptionsads.banner.size.FacebookBannerSize;
import com.lib.mobioptionsads.banner.size.UnityBannerSize;
import com.lib.mobioptionsads.interstitial.MobiInterstitialError;
import com.lib.mobioptionsads.interstitial.MobiInterstitialListener;
import com.lib.mobioptionsads.interstitial.MobiOptionsInterstitial;
import com.lib.mobioptionsads.nativeAd.MobiNativeAdError;
import com.lib.mobioptionsads.nativeAd.MobiNativeAdListener;
import com.lib.mobioptionsads.nativeAd.MobiNativeAdSize;
import com.lib.mobioptionsads.nativeAd.MobiOptionsNativeAd;
import com.lib.mobioptionsads.nativeAd.size.NativeAdFacebookSize;
import com.lib.mobioptionsads.nativeAd.size.NativeAdmobSize;
import com.lib.mobioptionsads.rewarded.MobiOptionRewardedAd;
import com.lib.mobioptionsads.rewarded.MobiRewardAdError;
import com.lib.mobioptionsads.rewarded.MobiRewardAdListener;
import com.lib.mobioptionsads.rewarded.MobiRewardAdLoadListener;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private MobiOptionsBanner banner;
    private MobiOptionsInterstitial interstitial;
    private MobiOptionRewardedAd rewardedAd;

    private MobiOptionsNativeAd nativeAd;

    private LinearLayout nativeAdContainer;

    private final Handler handler = new Handler(Looper.getMainLooper());

    AppCompatButton interstitialButton;


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
                public void onRewardedAdError(String adProvider, MobiRewardAdError error) {
                    // Log the errors
                    Log.d(TAG, "onRewardedAdError: Reward Error, (provider: " + adProvider + ")." + "\n" + error.toString());
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        interstitialButton = findViewById(R.id.show_interstitial);

        RootApplication.setupMobiOptionsAds(new MobiInitializationListener() {
            @Override
            public void onInitializationSuccess() {
                Log.d(TAG, "onInitializationSuccess: Initialization done successfully");
                setUpAllAds();
//                handler.postDelayed(() -> setUpInterstitial(), 7000);
            }

            @Override
            public void onInitializationFailed(String error) {
                Log.d(TAG, "onInitializationFailed: Error in the MobiInitialisation, details => " + error);
            }
        });
    }


    private void setUpAllAds() {
        LinearLayout bannerContainer = findViewById(R.id.banner_container);
        AppCompatButton rewardedAdButton = findViewById(R.id.show_rewarded_ad);

        nativeAdContainer = findViewById(R.id.native_ad_container);

        MobiOptionsBannerSize bannerSize = new MobiOptionsBannerSize(
                new AdmobBannerSize(AdmobBannerSize.ADMOB_SMART_BANNER),
                new UnityBannerSize(320, 50),
                new FacebookBannerSize(FacebookBannerSize.FACEBOOK_BANNER_HEIGHT_50)
        );

        // setup banner
        banner = new MobiOptionsBanner(bannerContainer, bannerSize, "Banner_0");
        banner.load();


        // banner call backs, always call the load() method before setting a listener
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


        handler.postDelayed(() -> {

            setUpInterstitial();

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
        }, 8000);


        // Native ads
        setUpNativeAd();
    }


    private void setUpInterstitial() {
        interstitial = new MobiOptionsInterstitial(this, "Interstitial_2");
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

        interstitialButton.setOnClickListener((v) -> {
            if (interstitial.isLoaded()) {
                interstitial.show();
                interstitial.loadAd();
            } else {
                Toast.makeText(MainActivity.this, "Ad not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setUpRewardedAd() {
        rewardedAd = new MobiOptionRewardedAd(this, "Rewarded Video_3");
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


    private void setUpNativeAd() {
        MobiNativeAdSize size = new MobiNativeAdSize(NativeAdmobSize.GNT_SMALL_TEMPLATE,
                NativeAdFacebookSize.WIDTH_280_HEIGHT_250);
        nativeAd = new MobiOptionsNativeAd(this, "Native_4", size, nativeAdContainer);
        nativeAd.load(new MobiNativeAdListener() {
            @Override
            public void onAdLoaded(String adsProvider) {
                // here you can call the show method to show the native ad
                Log.d(TAG, "onAdLoaded: The native ad is loaded => " + adsProvider);
                nativeAd.show();
            }

            @Override
            public void onAdError(String adsProvider, MobiNativeAdError error) {
                Log.d(TAG, "onAdError: " + adsProvider + ", errors: " + error.code + ", message: " + error.message);
            }

            @Override
            public void onAdClicked(String adsProvider) {
                Log.d(TAG, "onAdClicked: " + adsProvider + " native ad was clicked.");
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (banner != null) {
            banner.destroy();
        }
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }
}