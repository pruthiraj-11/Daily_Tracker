package achivementtrackerbyamit.example.achivetracker.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import achivementtrackerbyamit.example.achivetracker.HomeActivity;
import achivementtrackerbyamit.example.achivetracker.R;
import achivementtrackerbyamit.example.achivetracker.databinding.ActivityVerifyOtpBinding;

public class VerifyOTP extends AppCompatActivity {

    ActivityVerifyOtpBinding binding;
    PinView pinView;
    AppCompatButton verify;
    String phonenumber,email,name;
    String otpid;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        pinView = findViewById(R.id.pinView);
        verify = findViewById(R.id.verifyotp);
        mAuth = FirebaseAuth.getInstance();
        phonenumber = Objects.requireNonNull(getIntent().getStringExtra("mobile")).toString();
        email = Objects.requireNonNull(getIntent().getStringExtra("email")).toString();
        name = Objects.requireNonNull(getIntent().getStringExtra("name")).toString();
        initiateOTP();
        verify.setOnClickListener(v -> {
            if (Objects.requireNonNull(pinView.getText()).toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Please Enter the OTP", Toast.LENGTH_SHORT).show();
            }else if (pinView.getText().toString().length()!=6){
                Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
            }else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpid,pinView.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }
        });

        TextView resend = findViewById(R.id.resendotp);
        resend.setOnClickListener(v -> resendotp());
    }
    private void resendotp(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,
                30,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String a, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpid=a;
                    }
                });
    }
    private void initiateOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,
                40,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpid=s;
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                         HashMap<String, Object> hashMap = new HashMap<>();
                         hashMap.put("name",name);
                         hashMap.put("email",email);
                        String currentuserid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid().toString();
                        DatabaseReference Rootref = FirebaseDatabase.getInstance().getReference().child("Users");
                        Rootref.child(currentuserid).updateChildren(hashMap);
                        startActivity(new Intent(VerifyOTP.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(VerifyOTP.this, "Failed to Login!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}