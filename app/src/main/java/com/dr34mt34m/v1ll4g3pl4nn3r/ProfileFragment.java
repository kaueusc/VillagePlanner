package com.dr34mt34m.v1ll4g3pl4nn3r;

import static com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers.FirebaseHelper.updateProfilePicture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers.FirebaseHelper;
import com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers.ImageLoadTask;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class ProfileFragment extends Fragment {
    private static final String TAG = ViewRemindersFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView profilePicIV = getView().findViewById(R.id.profile_pic);
        TextView nameTV = getView().findViewById(R.id.profile_name);
        TextView emailTV = getView().findViewById(R.id.profile_email);
//        Button editBtn = getView().findViewById(R.id.profile_edit_btn);
        Button signOutBtn = getView().findViewById(R.id.profile_sign_out_btn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        nameTV.setText(user.getDisplayName());
        emailTV.setText(user.getEmail());
        if (user.getPhotoUrl() != null) {
            Log.d(TAG, "photourl: " + user.getPhotoUrl().toString());
            new ImageLoadTask(user.getPhotoUrl().toString(), profilePicIV).execute();
        } else {
            new ImageLoadTask("https://simplyilm.com/wp-content/uploads/2017/08/temporary-profile-placeholder-1.jpg", profilePicIV).execute();
        }

        signOutBtn.setOnClickListener(this::signOut);
        profilePicIV.setOnClickListener(this::chooseImage);
    }

    public void signOut(View v) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
        getActivity().finish();
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), localuri);

                FirebaseHelper.uploadImage(bitmap,(Uri photoUri)-> {
                    // Initialize user's first name and last name
                    FirebaseHelper.updateProfilePicture(photoUri);
                    Toast.makeText(this.getActivity(), "Updated picture!",
                            Toast.LENGTH_SHORT).show();
                    updateProfilePic();
                    return null;
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            //unable to findViewById?
//            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Image Selected!", Snackbar.LENGTH_LONG);
//            snackbar.show();
        }
    }

    public void updateProfilePic(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView profilePicIV = getView().findViewById(R.id.profile_pic);
        if (user.getPhotoUrl() != null) {
            Log.d(TAG, "photourl: " + user.getPhotoUrl().toString());
            new ImageLoadTask(user.getPhotoUrl().toString(), profilePicIV).execute();
        } else {
            new ImageLoadTask("https://simplyilm.com/wp-content/uploads/2017/08/temporary-profile-placeholder-1.jpg", profilePicIV).execute();
        }
    }
}