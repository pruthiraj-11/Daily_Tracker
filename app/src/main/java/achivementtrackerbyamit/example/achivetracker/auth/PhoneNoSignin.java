package achivementtrackerbyamit.example.achivetracker.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.hbb20.CountryCodePicker;

import achivementtrackerbyamit.example.achivetracker.R;
import achivementtrackerbyamit.example.achivetracker.databinding.ActivityPhoneNoSigninBinding;

public class PhoneNoSignin extends AppCompatActivity {

    ActivityPhoneNoSigninBinding binding;
    CountryCodePicker countryCodePicker;
    EditText name,phone,email;
    AppCompatButton getotp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivityPhoneNoSigninBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        countryCodePicker = findViewById(R.id.countrycodepicker);
        name = findViewById(R.id.username);
        phone = findViewById(R.id.phoneno);
        email = findViewById(R.id.email);
        getotp = findViewById(R.id.getotp);
        countryCodePicker.registerCarrierNumberEditText(phone);
        getotp.setOnClickListener(v -> {
            String nameString = name.getText().toString();
            String emailString= email.getText().toString();
            String phoneString = phone.getText().toString();
            if (TextUtils.isEmpty(nameString) && TextUtils.isEmpty(emailString) && TextUtils.isEmpty(phoneString)){
                Toast.makeText(PhoneNoSignin.this, "Please enter the details!", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(PhoneNoSignin.this, VerifyOTP.class);
                intent.putExtra("mobile",countryCodePicker.getFullNumberWithPlus().replace(" ",""));
                intent.putExtra("name",nameString);
                intent.putExtra("email",emailString);
                startActivity(intent);
            }
        });
    }
}