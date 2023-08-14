package achivementtrackerbyamit.example.achivetracker.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import achivementtrackerbyamit.example.achivetracker.R;
import achivementtrackerbyamit.example.achivetracker.databinding.ActivitySignupBinding;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    private final int GALLERY_INTENT_CODE = 993;
    private final int CAMERA_INTENT_CODE = 990;
    private FirebaseAuth mAuth;
    EditText name,email,pass;
    Button signup;
    TextView gotosignin;
    String emailtext,passtext,nametext;
    ProgressBar progressBar;
    CircleImageView profilePic;
    CheckBox checkBox;
    ProgressDialog progressDialog;
    ImageButton add;
    boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivitySignupBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        InitializeMethods();
        pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right=2;
                if (event.getAction()==MotionEvent.ACTION_UP){
                    if (event.getRawX()>=pass.getRight()-pass.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = pass.getSelectionEnd();
                        if (passwordVisible){
                            pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_off,0);
                            // for hide password
                            pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else{
                            pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility,0);
                            // for show password
                            pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        pass.setSelection(selection);
                        return true;
                    }

                }
                return false;
            }
        });
        gotosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowOptionsforProfilePic();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpMethods();
            }
        });
    }

    private void SignUpMethods() {
        nametext = name.getText().toString().trim();
        emailtext = email.getText().toString().trim();
        passtext = pass.getText().toString().trim();

        if (nametext.isEmpty()){
            name.setError("Field can't be empty");
            name.requestFocus();
            return;
        }

        if (emailtext.isEmpty()){
            email.setError("Field can't be empty");
            email.requestFocus();
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailtext).matches()){
            email.setError("Please enter a valid email address");
            email.requestFocus();
            return;
        }
        else if (passtext.isEmpty()){
            pass.setError("Field can't be empty");
            pass.requestFocus();
            return;
        }
        else if (passtext.length()<6){
            pass.setError("Password must be atleast 6 characters");
            pass.requestFocus();
            return;
        }
        else if (!checkBox.isChecked()){
            Toast.makeText(SignupActivity.this, "Please accept Terms and Conditions", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emailtext,passtext)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Users users = new Users(nametext,emailtext);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(SignupActivity.this,SigninActivity.class);
                                                startActivity(intent);
                                                finishAffinity();
                                            }
                                            else{
                                                Toast.makeText(SignupActivity.this, "Registration Failed "+task.getException(), Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(SignupActivity.this, "Registration Failed "+task.getException(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void InitializeMethods() {

        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        email = findViewById(R.id.signupemail);
        pass = findViewById(R.id.signuppass);
        signup = findViewById(R.id.signupbtn);
        gotosignin = findViewById(R.id.gotosignin);
        progressBar = findViewById(R.id.progressBar);
        checkBox = findViewById(R.id.checkBox);
        add = findViewById(R.id.add_image);
        profilePic = findViewById(R.id.profile_image);
    }

    private void ShowOptionsforProfilePic() {

        new MaterialAlertDialogBuilder(SignupActivity.this).setBackground(getResources().getDrawable(R.drawable.material_dialog_box)).setTitle("Change profile photo").setItems(new String[]{"Choose from gallery", "Take a new picture"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i)
                {
                    // Choosing image from gallery
                    case 0:
                        // Defining Implicit Intent to mobile gallery
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(
                                Intent.createChooser(
                                        intent,
                                        "Select Image from here..."),
                                GALLERY_INTENT_CODE);
                        break;

                    // Clicking a new picture using camera
                    case 1:
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(cameraIntent,CAMERA_INTENT_CODE);
                        }
                        startActivityForResult(cameraIntent,CAMERA_INTENT_CODE);
                        break;
                }
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data!=null)
        {
            Uri uri = (Uri) data.getData();
            switch (requestCode)
            {
                // Image received from gallery
                case GALLERY_INTENT_CODE:
                    try {
                        // Converting the image uri to bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                        profilePic.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                // Image received from camera
                case CAMERA_INTENT_CODE:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profilePic.setImageBitmap(bitmap);
                    break;
            }
        }
    }




}