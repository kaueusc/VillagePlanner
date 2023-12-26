package com.dr34mt34m.v1ll4g3pl4nn3r;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = SignInActivity.class.getName();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }

        setContentView(R.layout.activity_sign_in);
    }

    public void navigateToSignUp(View v) {
        startActivity(new Intent(this, SignUpActivity.class));
        this.finish();
    }

    public void signIn(View v) {
        String email = ((TextView) findViewById(R.id.signin_email)).getText().toString();
        String password = ((TextView) findViewById(R.id.signin_password)).getText().toString();
        if (email.length() == 0 || password.length() == 0) {
            Toast.makeText(SignInActivity.this, "Please fill out all fields!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // TODO maybe add a check that this user's Firebase Auth ID matches the document ID stored in cloud firestore
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");

                        // Go to maps page
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        SignInActivity.this.finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}