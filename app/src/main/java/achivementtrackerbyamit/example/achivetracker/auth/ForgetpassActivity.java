package achivementtrackerbyamit.example.achivetracker.auth;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import achivementtrackerbyamit.example.achivetracker.R;
import achivementtrackerbyamit.example.achivetracker.databinding.ActivityForgetpassBinding;

public class ForgetpassActivity extends AppCompatActivity {

    ActivityForgetpassBinding binding;
    FirebaseAuth Auth2;
    EditText email;
    Button resetpass;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivityForgetpassBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        email = findViewById(R.id.editTextTextEmailAddress);
        Auth2 = FirebaseAuth.getInstance();
        resetpass = findViewById(R.id.button2);
        resetpass.setOnClickListener(view -> ForgotPassMethod());
    }

    private void ForgotPassMethod() {
        String emailnew = email.getText().toString().trim();
        if (emailnew.isEmpty()){
            email.setError("Field can't be empty");
            email.requestFocus();
            return;
        }
        // Checking whether the email is valid or not
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailnew).matches()){
            email.setError("Please enter a valid Email id");
            email.requestFocus();
            return;
        }
        else{
            // Sending the mail to your email
            Auth2.sendPasswordResetEmail(emailnew)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            try{
                                if (task.isSuccessful()){
                                    Toast.makeText(ForgetpassActivity.this, "Password Reset email sent!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(ForgetpassActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                Log.d(TAG,"Email not sent");
                                return;
                            }
                        }
                    });
        }
    }
}