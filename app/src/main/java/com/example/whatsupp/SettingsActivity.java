package com.example.whatsupp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Button updateAccountsSettings;
    private EditText username,userStatus;
    private CircleImageView userProfileImage;
    private  String CurrentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private  StorageReference UserProfileImagesRef;
    private  static final int GalleryPick=1;
    private ProgressDialog loadingBar;
    private Toolbar SettingsToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth=FirebaseAuth.getInstance();
        CurrentUserId=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
       UserProfileImagesRef=FirebaseStorage.getInstance().getReference().child("Profile Images");

        InitializeFields();

        username.setVisibility(View.INVISIBLE);

        updateAccountsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        RetriveUserInfo();
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick);
            }
        });
    }



    private void InitializeFields() {

        updateAccountsSettings=(Button)findViewById(R.id.update_settings_button);
        username=(EditText)findViewById(R.id.set_user_name);
        userStatus=(EditText)findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);
        SettingsToolBar=(Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();


                StorageReference filePath = UserProfileImagesRef.child(CurrentUserId + ".jpeg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                            final String downloaedUrl = task.getResult().getStorage().toString();


                            RootRef.child("Users").child(CurrentUserId).child("image")
                                    .setValue(downloaedUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(SettingsActivity.this, "Image save in Database, Successfully...", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().toString();
                                                Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        }
    }

    private void UpdateSettings() {
    String setUserName=username.getText().toString();
        String setUserStatus=userStatus.getText().toString();
        if(TextUtils.isEmpty(setUserName)){

            Toast.makeText(this, "please write your user name......", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setUserStatus)){

            Toast.makeText(this, "please write your user status......", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,Object> profileMap=new HashMap<>();
            profileMap.put("uid",CurrentUserId);
            profileMap.put("name",setUserName);
            profileMap.put("status",setUserStatus);
            RootRef.child("Users").child(CurrentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendUserMainActivity();
                                Toast.makeText(SettingsActivity.this, "profile updated successfully....", Toast.LENGTH_SHORT).show();
                            }
                            else{

                                String meassage=task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error"+meassage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    private void RetriveUserInfo() {

        RootRef.child("Users").child(CurrentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot) {
                        if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))&&(dataSnapshot.hasChild("image")))
                        {
                            String retriveUserName=dataSnapshot.child("name").getValue().toString();
                            String retriveUserStatus=dataSnapshot.child("status").getValue().toString();
                            String retriveProfileUserImage=dataSnapshot.child("image").getValue().toString();


                            username.setText(retriveUserName);
                            userStatus.setText(retriveUserStatus);
                            Picasso.get().load(retriveProfileUserImage).into(userProfileImage);

                        }
                        else if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name")))
                        {
                            String retriveUserName=dataSnapshot.child("name").getValue().toString();
                            String retriveUserStatus=dataSnapshot.child("status").getValue().toString();


                            username.setText(retriveUserName);
                            userStatus.setText(retriveUserStatus);


                        } else {

                            username.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "please set and update your profile information.....", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled( DatabaseError databaseError) {

                    }
                });

    }
    private void sendUserMainActivity() {
        Intent MainIntent=new Intent(SettingsActivity.this,MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }
}
