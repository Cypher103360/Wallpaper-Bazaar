package com.imagesandwallpaper.bazaar.iwb.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityLoginBinding;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth auth;
    EditText loginEmail, loginPass;
    Button loginBtn;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
       // auth.signOut();
        loadingDialog = CommonMethods.loadingDialog(LoginActivity.this);

        loginEmail = binding.loginEmail;
        loginPass = binding.loginPass;
        loginBtn = binding.loginBtn;

        binding.signUpText.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
        binding.backIcon.setOnClickListener(view -> {
            onBackPressed();
        });

        loginBtn.setOnClickListener(view -> {
            loadingDialog.show();
            loginUser();
        });
    }

    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            loginEmail.setError("Email required");
            loginEmail.requestFocus();
            loadingDialog.dismiss();

        } else if (TextUtils.isEmpty(password)) {
            loginPass.setError("Password required");
            loginPass.requestFocus();
            loadingDialog.dismiss();

        } else {
            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));

                            }else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}