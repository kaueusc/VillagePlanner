package com.dr34mt34m.v1ll4g3pl4nn3r;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = SignUpActivity.class.getName();
    private FirebaseAuth auth;

    Bitmap bitmap=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }

        setContentView(R.layout.activity_sign_up);
    }

    public void navigateToSignIn(View v) {
        startActivity(new Intent(this, SignInActivity.class));
        this.finish();
    }

    public void signUp(View v) {
        String firstName = ((TextView) findViewById(R.id.signup_first_name)).getText().toString();
        String lastName = ((TextView) findViewById(R.id.signup_last_name)).getText().toString();
        String email = ((TextView) findViewById(R.id.signup_email)).getText().toString();
        String password = ((TextView) findViewById(R.id.signup_password)).getText().toString();

        if (firstName.length() == 0 || lastName.length() == 0 || email.length() == 0 || password.length() == 0) {
            Toast.makeText(SignUpActivity.this, "Please fill out all the fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");

                        if(bitmap!=null){
                            System.out.println("should do upload image now");
                            FirebaseHelper.uploadImage(bitmap,(Uri photoUri)-> {
                                // Initialize user's first name and last name
                                FirebaseHelper.updateUserInfo(firstName, lastName, email, photoUri);
                                return null;
                            });
                        } else {
                            FirebaseHelper.updateUserInfo(firstName, lastName, email, Uri.parse(""));
                        }

                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        SignUpActivity.this.finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void chooseImage(View v){
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");

        startActivityForResult(chooserIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(data==null){
                return;
            }
            Uri localuri=data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), localuri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Image Selected!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}