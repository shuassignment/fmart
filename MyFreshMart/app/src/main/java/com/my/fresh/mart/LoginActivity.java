package com.my.fresh.mart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.my.fresh.mart.Config.FIREBASE_UI_SIGN_IN;

public class LoginActivity extends AppCompatActivity {
//    private static final int FIREBASE_UI_SIGN_IN = 9001;
    EditText etEmail,etPassword;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail=findViewById(R.id.etLoginEmail);
        etPassword=findViewById(R.id.etLoginPassword);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        Toast.makeText(this, ""+currentUser, Toast.LENGTH_SHORT).show();
    }

    public void login(final View view) {

        String email=etEmail.getText().toString();
        String password=etPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Snackbar.make(view,"Authentication failed...",Snackbar.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }


    public void firebaseuilogin(View view) {
        startActivityForResult(Config.FIREBASE_LOGIN(),FIREBASE_UI_SIGN_IN);
    }

    public void firebaseLogin()
    {
        startActivityForResult(Config.FIREBASE_LOGIN(),FIREBASE_UI_SIGN_IN);

    }


}


