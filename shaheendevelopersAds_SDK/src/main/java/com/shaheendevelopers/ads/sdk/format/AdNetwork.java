package com.shaheendevelopers.ads.sdk.format;

import static com.shaheendevelopers.ads.sdk.util.Constant.ADMOB;
import static com.shaheendevelopers.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.shaheendevelopers.ads.sdk.util.Constant.APPLOVIN;
import static com.shaheendevelopers.ads.sdk.util.Constant.APPLOVIN_DISCOVERY;
import static com.shaheendevelopers.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.shaheendevelopers.ads.sdk.util.Constant.FACEBOOK;
import static com.shaheendevelopers.ads.sdk.util.Constant.FAN;
import static com.shaheendevelopers.ads.sdk.util.Constant.FAN_BIDDING_ADMOB;
import static com.shaheendevelopers.ads.sdk.util.Constant.FAN_BIDDING_AD_MANAGER;
import static com.shaheendevelopers.ads.sdk.util.Constant.FAN_BIDDING_APPLOVIN_MAX;
import static com.shaheendevelopers.ads.sdk.util.Constant.FAN_BIDDING_IRONSOURCE;
import static com.shaheendevelopers.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.shaheendevelopers.ads.sdk.util.Constant.IRONSOURCE;
import static com.shaheendevelopers.ads.sdk.util.Constant.MOPUB;
import static com.shaheendevelopers.ads.sdk.util.Constant.NONE;
import static com.shaheendevelopers.ads.sdk.util.Constant.STARTAPP;
import static com.shaheendevelopers.ads.sdk.util.Constant.UNITY;
import static com.shaheendevelopers.ads.sdk.util.Constant.WORTISE;

import android.app.Activity;
import android.util.Log;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.ironsource.mediationsdk.IronSource;
import com.shaheendevelopers.ads.sdk.helper.AudienceNetworkInitializeHelper;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.unity3d.mediation.IInitializationListener;
import com.unity3d.mediation.InitializationConfiguration;
import com.unity3d.mediation.UnityMediation;
import com.unity3d.mediation.errors.SdkInitializationError;
import com.wortise.ads.WortiseSdk;

import java.util.Map;

public class AdNetwork {

    public static class Initialize {

        private static final String TAG = "AdNetwork";
        Activity activity;
        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobAppId = "";
        private String startappAppId = "0";
        private String unityGameId = "";
        private String appLovinSdkKey = "";
        private String mopubBannerId = "";
        private String ironSourceAppKey = "";
        private String wortiseAppId = "";
        private boolean debug = true;

        public Initialize(Activity activity) {
            this.activity = activity;
        }

        public Initialize build() {
            initAds();
            initBackupAds();
            return this;
        }

        public Initialize setAdStatus(String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        public Initialize setAdNetwork(String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        public Initialize setBackupAdNetwork(String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            return this;
        }

        public Initialize setAdMobAppId(String adMobAppId) {
            this.adMobAppId = adMobAppId;
            return this;
        }

        public Initialize setStartappAppId(String startappAppId) {
            this.startappAppId = startappAppId;
            return this;
        }

        public Initialize setUnityGameId(String unityGameId) {
            this.unityGameId = unityGameId;
            return this;
        }

        public Initialize setAppLovinSdkKey(String appLovinSdkKey) {
            this.appLovinSdkKey = appLovinSdkKey;
            return this;
        }

        public Initialize setMopubBannerId(String mopubBannerId) {
            this.mopubBannerId = mopubBannerId;
            return this;
        }

        public Initialize setIronSourceAppKey(String ironSourceAppKey) {
            this.ironSourceAppKey = ironSourceAppKey;
            return this;
        }

        public Initialize setWortiseAppId(String wortiseAppId) {
            this.wortiseAppId = wortiseAppId;
            return this;
        }

        public Initialize setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public void initAds() {
            if (adStatus.equals(AD_STATUS_ON)) {
                switch (adNetwork) {
                    case ADMOB:
                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_ADMOB:
                    case FAN_BIDDING_AD_MANAGER:
                        MobileAds.initialize(activity, initializationStatus -> {
                            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                            for (String adapterClass : statusMap.keySet()) {
                                AdapterStatus adapterStatus = statusMap.get(adapterClass);
                                assert adapterStatus != null;
                                Log.d(TAG, String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, adapterStatus.getDescription(), adapterStatus.getLatency()));
                            }
                        });
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;
                    case FAN:
                    case FACEBOOK:
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;
                    case STARTAPP:
                        StartAppSDK.init(activity, startappAppId, false);
                        StartAppSDK.setTestAdsEnabled(debug);
                        StartAppAd.disableSplash();
                        StartAppSDK.setUserConsent(activity, "pas", System.currentTimeMillis(), true);
                        break;
                    case UNITY:
                        InitializationConfiguration configuration = InitializationConfiguration.builder()
                                .setGameId(unityGameId)
                                .setInitializationListener(new IInitializationListener() {
                                    @Override
                                    public void onInitializationComplete() {
                                        Log.d(TAG, "Unity Mediation is successfully initialized. with ID : " + unityGameId);
                                    }

                                    @Override
                                    public void onInitializationFailed(SdkInitializationError errorCode, String msg) {
                                        Log.d(TAG, "Unity Mediation Failed to Initialize : " + msg);
                                    }
                                }).build();
                        UnityMediation.initialize(configuration);
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        AppLovinSdk.getInstance(activity).setMediationProvider(AppLovinMediationProvider.MAX);
                        AppLovinSdk.getInstance(activity).initializeSdk(config -> {
                        });
                        AudienceNetworkInitializeHelper.initialize(activity);
                        break;

                    case APPLOVIN_DISCOVERY:
                        AppLovinSdk.initializeSdk(activity);
                        break;

                    case MOPUB:
                        //Mopub has been acquired by AppLovin
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        String advertisingId = IronSource.getAdvertiserId(activity);
                        IronSource.setUserId(advertisingId);
                        IronSource.init(activity, ironSourceAppKey, () -> {
                            Log.d(TAG, "[" + adNetwork + "] initialize complete");
                        });
//                        IronSource.init(activity, ironSourceAppKey, IronSource.AD_UNIT.REWARDED_VIDEO);
//                        IronSource.init(activity, ironSourceAppKey, IronSource.AD_UNIT.INTERSTITIAL);
//                        IronSource.init(activity, ironSourceAppKey, IronSource.AD_UNIT.BANNER);
                        break;

                    case WORTISE:
                        WortiseSdk.initialize(activity, wortiseAppId);
                        break;
                }
                Log.d(TAG, "[" + adNetwork + "] is selected as Primary Ads");
            }
        }

        public void initBackupAds() {
            if (adStatus.equals(AD_STATUS_ON)) {
                switch (backupAdNetwork) {
                    case ADMOB:
                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_ADMOB:
                    case FAN_BIDDING_AD_MANAGER:
                        MobileAds.initialize(activity, initializationStatus -> {
                            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                            for (String adapterClass : statusMap.keySet()) {
                                AdapterStatus adapterStatus = statusMap.get(adapterClass);
                                assert adapterStatus != null;
                                Log.d(TAG, String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, adapterStatus.getDescription(), adapterStatus.getLatency()));
                            }
                        });
                        AudienceNetworkInitializeHelper.initialize(activity);
                        break;
                    case FAN:
                    case FACEBOOK:
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;
                    case STARTAPP:
                        StartAppSDK.init(activity, startappAppId, false);
                        StartAppSDK.setTestAdsEnabled(debug);
                        StartAppAd.disableSplash();
                        StartAppSDK.setUserConsent(activity, "pas", System.currentTimeMillis(), true);
                        break;
                    case UNITY:
                        InitializationConfiguration configuration = InitializationConfiguration.builder()
                                .setGameId(unityGameId)
                                .setInitializationListener(new IInitializationListener() {
                                    @Override
                                    public void onInitializationComplete() {
                                        Log.d(TAG, "Unity Mediation is successfully initialized. with ID : " + unityGameId);
                                    }

                                    @Override
                                    public void onInitializationFailed(SdkInitializationError errorCode, String msg) {
                                        Log.d(TAG, "Unity Mediation Failed to Initialize : " + msg);
                                    }
                                }).build();
                        UnityMediation.initialize(configuration);
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        AppLovinSdk.getInstance(activity).setMediationProvider(AppLovinMediationProvider.MAX);
                        AppLovinSdk.getInstance(activity).initializeSdk(config -> {
                        });
                        AudienceNetworkInitializeHelper.initialize(activity);
                        break;

                    case APPLOVIN_DISCOVERY:
                        AppLovinSdk.initializeSdk(activity);
                        break;

                    case MOPUB:
                        //Mopub has been acquired by AppLovin
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        String advertisingId = IronSource.getAdvertiserId(activity);
                        IronSource.setUserId(advertisingId);
                        IronSource.init(activity, ironSourceAppKey, () -> {
                            Log.d(TAG, "[" + adNetwork + "] initialize complete");
                        });
//                        IronSource.init(activity, ironSourceAppKey, IronSource.AD_UNIT.REWARDED_VIDEO);
//                        IronSource.init(activity, ironSourceAppKey, IronSource.AD_UNIT.INTERSTITIAL);
//                        IronSource.init(activity, ironSourceAppKey, IronSource.AD_UNIT.BANNER);
                        break;

                    case WORTISE:
                        WortiseSdk.initialize(activity, wortiseAppId);
                        break;

                    case NONE:
                        //do nothing
                        break;
                }
                Log.d(TAG, "[" + backupAdNetwork + "] is selected as Backup Ads");
            }
        }

    }

}
