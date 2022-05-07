package com.imagesandwallpaper.bazaar.iwb.activities;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.activities.ui.main.SectionsPagerAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityHomeBinding;
import com.imagesandwallpaper.bazaar.iwb.fragments.CategoryFragment;
import com.imagesandwallpaper.bazaar.iwb.fragments.HomeFragment;
import com.imagesandwallpaper.bazaar.iwb.fragments.PremiumFragment;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.MyReceiver;
import com.imagesandwallpaper.bazaar.iwb.utils.Prevalent;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String BroadCastStringForAction = "checkingInternet";
    private static final float END_SCALE = 0.7f;
    int count = 1;
    ImageView navMenu;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout categoryContainer;
    IntentFilter intentFilter;
    SectionsPagerAdapter sectionsPagerAdapter;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ActivityHomeBinding binding;
    ShowAds ads = new ShowAds();


    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadCastStringForAction)) {
                if (intent.getStringExtra("online_status").equals("true")) {

                    Set_Visibility_ON();
                    count++;
                } else {
                    Set_Visibility_OFF();
                }
            }
        }
    };

    private void Set_Visibility_ON() {
        binding.lottieHomeNoInternet.setVisibility(View.GONE);
        binding.tvNotConnected.setVisibility(View.GONE);
        binding.viewPager.setVisibility(View.VISIBLE);
        binding.viewPager.setOffscreenPageLimit(3);
        binding.tabs.setVisibility(View.VISIBLE);
        getLifecycle().addObserver(ads);
        enableNavItems();
        if (count == 2) {
            ViewPager viewPager = binding.viewPager;
            viewPager.setAdapter(sectionsPagerAdapter);
            TabLayout tabs = binding.tabs;
            tabs.setupWithViewPager(viewPager);
            navigationDrawer();
        }
    }

    private void Set_Visibility_OFF() {
        binding.lottieHomeNoInternet.setVisibility(View.VISIBLE);
        binding.tvNotConnected.setVisibility(View.VISIBLE);
        binding.viewPager.setVisibility(View.GONE);
        binding.tabs.setVisibility(View.GONE);
        disableNavItems();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        navigationView = binding.navigation;
        navMenu = binding.navMenu;
        drawerLayout = binding.drawerLayout;

        // Setting Version Code
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String version = pInfo.versionName;
            binding.versionCode.setText("Version : " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            // Background work
            if (Objects.requireNonNull(Paper.book().read(Prevalent.bannerTopNetworkName)).equals("IronSourceWithMeta")) {
                ads.showTopBanner(this, binding.adViewTop);

            } else if (Objects.requireNonNull(Paper.book().read(Prevalent.bannerBottomNetworkName)).equals("IronSourceWithMeta")) {
                ads.showTopBanner(this, binding.adViewTop);
            }
        });


        //Internet Checking Condition
        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, MyReceiver.class);
        startService(serviceIntent);
        if (isOnline(HomeActivity.this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Set_Visibility_ON();
            }
        } else {
            Set_Visibility_OFF();
        }

        binding.lottieContact.setOnClickListener(view -> {
            try {
                CommonMethods.whatsApp(HomeActivity.this);
            } catch (UnsupportedEncodingException | PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        });
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String name = account.getDisplayName();
            String email = account.getEmail();
        }

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        sectionsPagerAdapter.addFragments(new HomeFragment(), "Home");
        sectionsPagerAdapter.addFragments(new CategoryFragment(), "Category");
        sectionsPagerAdapter.addFragments(new PremiumFragment(), "Premium");

    }

    public void navigationDrawer() {
        navigationView = findViewById(R.id.navigation);
        navigationView.bringToFront();
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                HomeActivity.this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(HomeActivity.this);
        navigationView.setCheckedItem(R.id.nav_home);
        categoryContainer = findViewById(R.id.container_layout);

        navMenu.setOnClickListener(view -> {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawerLayout.setScrimColor(getColor(R.color.bg_color));
        }
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                categoryContainer.setScaleX(offsetScale);
                categoryContainer.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = categoryContainer.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                categoryContainer.setTranslationX(xTranslation);
            }
        });
    }

    public void disableNavItems() {
        Menu navMenu = navigationView.getMenu();

        MenuItem nav_home = navMenu.findItem(R.id.nav_home);
        nav_home.setEnabled(false);

        MenuItem nav_contact = navMenu.findItem(R.id.nav_contact);
        nav_contact.setEnabled(false);

        MenuItem nav_share = navMenu.findItem(R.id.nav_share);
        nav_share.setEnabled(false);

        MenuItem nav_favorite = navMenu.findItem(R.id.nav_favorite);
        nav_favorite.setEnabled(false);

        MenuItem nav_policy = navMenu.findItem(R.id.nav_privacy);
        nav_policy.setEnabled(false);

        MenuItem nav_disclaimer = navMenu.findItem(R.id.nav_disclaimer);
        nav_disclaimer.setEnabled(false);

        MenuItem nav_signOut = navMenu.findItem(R.id.nav_signOut);
        nav_signOut.setEnabled(false);
    }

    public void enableNavItems() {
        Menu navMenu = navigationView.getMenu();

        MenuItem nav_home = navMenu.findItem(R.id.nav_home);
        nav_home.setEnabled(true);

        MenuItem nav_contact = navMenu.findItem(R.id.nav_contact);
        nav_contact.setEnabled(true);

        MenuItem nav_share = navMenu.findItem(R.id.nav_share);
        nav_share.setEnabled(true);

        MenuItem nav_favorite = navMenu.findItem(R.id.nav_favorite);
        nav_favorite.setEnabled(true);

        MenuItem nav_policy = navMenu.findItem(R.id.nav_privacy);
        nav_policy.setEnabled(true);

        MenuItem nav_disclaimer = navMenu.findItem(R.id.nav_disclaimer);
        nav_disclaimer.setEnabled(true);

        MenuItem nav_signOut = navMenu.findItem(R.id.nav_disclaimer);
        nav_signOut.setEnabled(true);
    }

    public boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.nav_pro:

                break;
            case R.id.nav_contact:
                try {
                    CommonMethods.whatsApp(HomeActivity.this);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_share:
                CommonMethods.shareApp(HomeActivity.this);
                break;
            case R.id.nav_rate:
                CommonMethods.rateApp(HomeActivity.this);
                break;
            case R.id.nav_favorite:
                startActivity(new Intent(this, FavoriteActivity.class));
                break;
            case R.id.nav_privacy:
                startActivity(new Intent(HomeActivity.this, PrivacyPolicyActivity.class));
                break;
            case R.id.nav_disclaimer:
                disclaimerDialog();
                break;
            case R.id.nav_signOut:
                CommonMethods.loadingDialog(HomeActivity.this).show();

                // Sign Out for google user
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                if (account != null) {
                    googleSignOut();
                }

                break;
            default:
        }
        return true;
    }

    public void disclaimerDialog() {
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.disclaimer_layout);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);
        dialog.show();
    }

    public void googleSignOut() {
        gsc.signOut().addOnCompleteListener(task -> {
            finish();
            startActivity(new Intent(HomeActivity.this, SignupActivity.class));
            CommonMethods.loadingDialog(HomeActivity.this).dismiss();
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
            super.onBackPressed();
        }
    }
}