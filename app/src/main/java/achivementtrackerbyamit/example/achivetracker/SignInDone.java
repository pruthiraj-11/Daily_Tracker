package achivementtrackerbyamit.example.achivetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SignInDone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_done);

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1200);
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                startActivity(new Intent(SignInDone.this,HomeActivity.class));
                finish();
            }
        });thread.start();
    }
}