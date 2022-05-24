package com.imagesandwallpaper.bazaar.iwb.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    int RC_SIGN_IN = 1000;
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(binding.getRoot());

//        // Google SignIn
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        gsc = GoogleSignIn.getClient(this, gso);
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account!= null){
//            navigateToNextActivity();
//        }

//        // test link span
//        TextView tv =  findViewById(R.id.terms_text);
//        Spannable span = Spannable.Factory.getInstance().newSpannable(
//                "By continuing, you agree to Wallpaper Bazaar's Terms of Service and acknowledge that you've read our Privacy Policy");
//        span.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SignupActivity.this,PrivacyPolicyActivity.class);
//                intent.putExtra("key","terms");
//                startActivity(intent);
//            }
//        }, 47, 63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        // All the rest will have the same spannable.
//        ClickableSpan cs = new ClickableSpan() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SignupActivity.this,PrivacyPolicyActivity.class);
//                intent.putExtra("key","policy");
//                startActivity(intent);
//            }
//        };
//
//        // set the "test " spannable.
//        span.setSpan(cs, 101, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        tv.setText(span);
//
//        tv.setMovementMethod(LinkMovementMethod.getInstance());

//        binding.withGoogle.setOnClickListener(view -> {
//            signIn();
//
//
//        });
    }


//    private void signIn() {
//        Intent signInIntent = gsc.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//
//            try {
//                task.getResult(ApiException.class);
//                navigateToNextActivity();
//            } catch (ApiException e) {
//                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
//            }
//
//        }
//    }

//    private void navigateToNextActivity() {
//        finish();
//        Intent intent = new Intent(SignupActivity.this, RefreshingActivity.class);
//        startActivity(intent);
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Google Sign in");
//        mFirebaseAnalytics.logEvent("Clicked_On_Google_SignIn", bundle);
//    }

}