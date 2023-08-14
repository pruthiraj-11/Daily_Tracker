package achivementtrackerbyamit.example.achivetracker.splash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import achivementtrackerbyamit.example.achivetracker.HomeActivity;
import achivementtrackerbyamit.example.achivetracker.R;
import achivementtrackerbyamit.example.achivetracker.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    private boolean connected = false;
    private FirebaseAuth mAuth;
    private String currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivitySplashBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance ();
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
            FirebaseUser currentUser = mAuth.getCurrentUser ();
            if (currentUser == null) {
                SendUserToLoginActivity();
            } else {
                currentUserID = mAuth.getCurrentUser ().getUid ();
                new Handler().postDelayed(() -> {
                    Intent loginIntent = new Intent (SplashActivity.this, HomeActivity.class  );
                    loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( loginIntent );
                    finish ();
                }, 2900);
            }
        } else{
            connected=false;
            new AlertDialog.Builder(this)
                    .setTitle("You are Offline!")
                    .setMessage("Make sure that you are in online Mode.")
                    .setNegativeButton("OK",null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    private void SendUserToLoginActivity() {
        new Handler().postDelayed(() -> {
         Intent loginIntent = new Intent (SplashActivity.this, Onboarding.class  );
        loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( loginIntent );
        finish ();
        }, 2900);
    }
}