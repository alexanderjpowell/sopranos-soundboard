package com.soundboards.alexanderpowell.sopranossoundboard;

import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {

    public static String[] filenames;

    public static final int BUTTON_HEIGHT_PIXELS = 300;
    public static final int BUTTON_MARGIN_SMALL = 15;
    public static final int BUTTON_MARGIN_LARGE = 30;
    public static final int TABLE_ROW_WIDTH = 0;
    public static final int TABLE_ROW_HEIGHT = 0;
    public static final float TABLE_ROW_WEIGHT = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        MobileAds.initialize(getApplicationContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                //Toast.makeText(getApplicationContext(), "onAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                //Toast.makeText(getApplicationContext(), Integer.toString(errorCode), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                //Toast.makeText(getApplicationContext(), "onAdOpened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                //Toast.makeText(getApplicationContext(), "onAdClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                //Toast.makeText(getApplicationContext(), "onAdLeftApplication", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                //Toast.makeText(getApplicationContext(), "onAdClosed", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            AssetManager assetManager = getAssets();
            filenames = assetManager.list("sounds");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /*private void printPreferences() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Set set = sharedPreferences.getStringSet("favorites", new HashSet<String>());
        Toast.makeText(getApplicationContext(), set.toString(), Toast.LENGTH_LONG).show();
    }*/

    public static String formatFileString(String filename) {
        return filename.replaceAll("_", " ").replace(".mp3", "");
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        @StringRes
        private final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SoundsTab();
                case 1:
                    return new SettingsTab();
                case 2:
                    return new FavoritesTab();
                default:
                    return new Fragment();
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return getString(TAB_TITLES[position]);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}