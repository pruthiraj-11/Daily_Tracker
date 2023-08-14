package achivementtrackerbyamit.example.achivetracker.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import achivementtrackerbyamit.example.achivetracker.R;
import achivementtrackerbyamit.example.achivetracker.SignInDone;
import achivementtrackerbyamit.example.achivetracker.databinding.ActivitySigninBinding;

public class SigninActivity extends AppCompatActivity {

    ActivitySigninBinding binding;
    private FirebaseAuth mAuth;
    EditText email,pass;
    TextView frgtpass,gotosignup;
    ProgressBar progressBar;
    Button signin;
    boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivitySigninBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        InitializeMethods();
        // Function to see password and hide password
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
        frgtpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SigninActivity.this, ForgetpassActivity.class);
                startActivity(intent1);
            }
        });

        gotosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(SigninActivity.this, SignupActivity.class);
                startActivity(intent2);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailtext = email.getText().toString().trim();
                String passtext = pass.getText().toString().trim();
                if (emailtext.isEmpty()){
                    email.setError("Field can't be empty");
                    email.requestFocus();
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(emailtext).matches()){
                    email.setError("Please enter a valid Email id");
                    email.requestFocus();
                    return;
                }
                else if (passtext.isEmpty()){
                    pass.setError("Field can't be empty");
                    pass.requestFocus();
                    return;
                }
                else if (passtext.length()<6){
                    pass.setError("Password must be at least 6 characters");
                    pass.requestFocus();
                    return;
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(emailtext,passtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                progressBar.setVisibility(View.GONE);
                                Intent intent2 = new Intent(SigninActivity.this, SignInDone.class);
                                startActivity(intent2);
                                finishAffinity();
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SigninActivity.this, "Failed to Login! Please check your credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private void InitializeMethods() {
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.loginemail);
        pass = findViewById(R.id.loginpassword);
        frgtpass = findViewById(R.id.frgtpass);
        signin = findViewById(R.id.signin);
        gotosignup = findViewById(R.id.signuptext);
        progressBar = findViewById(R.id.progressBar2);
    }
}