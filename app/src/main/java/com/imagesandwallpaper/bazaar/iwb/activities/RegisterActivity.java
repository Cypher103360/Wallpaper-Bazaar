package com.imagesandwallpaper.bazaar.iwb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityRegisterBinding;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;

import java.util.Locale;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    EditText regEmail,regPass;
    Button createAccountBtn;
    String email,password;
    private FirebaseAuth auth;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(binding.getRoot());
        binding.backIcon.setOnClickListener(view -> {
            onBackPressed();
        });
        auth = FirebaseAuth.getInstance();
        loadingDialog = CommonMethods.loadingDialog(RegisterActivity.this);

        regEmail = binding.regEmail;
        regPass = binding.regPass;
        createAccountBtn = binding.createAccountBtn;
        binding.loginText.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            finish();
        });

        createAccountBtn.setOnClickListener(view -> {
            loadingDialog.show();
            validateData();
        });
    }

    private void validateData() {
        email = regEmail.getText().toString().trim();
        password = regPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            regEmail.setError("Email required");
            regEmail.requestFocus();
            loadingDialog.dismiss();

        }else if (TextUtils.isEmpty(password)){
            regPass.setError("Password required");
            regPass.requestFocus();
            loadingDialog.dismiss();

        }else {
            createUser(email,password);
        }
    }

    private void createUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this,LoginActivity.class));
                        finish();
                    }else {
                        Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                });
    }

    private void uploadData() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}