package achivementtrackerbyamit.example.achivetracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import achivementtrackerbyamit.example.achivetracker.active_goal.GoingCLass;
import achivementtrackerbyamit.example.achivetracker.databinding.ActivityAddgoalBinding;

public class AddGoalActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    ActivityAddgoalBinding binding;
    String[] courses = {"High","Medium","Less"};
    public Button yes,no;
    private EditText tripname;
    private String currentUserID = "";
    private DatePicker datepicker;
    private boolean isNewGoal= false;
    private DatabaseReference RootRef, ActiveRef,activityRef;
    String string_priority = "Less", Edit = "";
    EditText  goalDesc;
    Spinner spino;
    @Nullable private String TAG;
    private String dataKey;
    Set<String> set;
    ImageView spinnerImageView;
    SimpleDateFormat DateFormat = new SimpleDateFormat("dd/M/yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivityAddgoalBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        InitializationMethod();

        TAG= getIntent().getStringExtra(DashboardActivity.ADD_TRIP_TAG);
        //bundle= getIntent().getExtras();
        Edit += getIntent().getStringExtra("Edit"); //Edit = true; is called from Edit Activity else Edit = "";

        if(TAG!=null && TAG.equals(DashboardActivity.ADD_TRIP_VALUE)) {
            retrievePreviousData();
        }
        else{
            findViewById(R.id.create_goal_text_view).setVisibility(View.VISIBLE);
        }

        spinnerImageView.setOnClickListener(view -> spino.performClick());

        yes.setOnClickListener(v -> YESONCLICK());

    }


    private void InitializationMethod() {

         spinnerImageView = findViewById(R.id.spinnerImageView);

        spino = findViewById(R.id.priority_spinner);
        spino.setOnItemSelectedListener(this);
        ArrayAdapter ad = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                courses);
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        spino.setAdapter(ad);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
        RootRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID);
        ActiveRef = FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Goals").child("Active");
        activityRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Activity");

        yes = (Button) findViewById(R.id.create_trip_submit_butyyon);
        //  no = (Button) findViewById(R.id.cancel_trip_submit_butyyon);
        tripname = (EditText)findViewById(R.id.edit_text_trip_name);
        datepicker = (DatePicker) findViewById(R.id.edit_text_trip_date);

        // EditText for goal description
        goalDesc = findViewById(R.id.edit_text_trip_desc);
        goalDesc.setScroller(new Scroller(this));
        goalDesc.setMaxLines(1);
        goalDesc.setVerticalScrollBarEnabled(true);
        goalDesc.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onStart() {
        super.onStart();
        set = new HashSet<>(); //Initialize
        ActiveRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                set.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) { //get all Goal IDs
                    GoingCLass going = snapshot1.getValue(GoingCLass.class);
                    String s = Objects.requireNonNull(going).getGoalName(); //Get data of Goal Name from that ID
                    set.add(s); //add in arraylist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void YESONCLICK() {

        if(TAG!=null && TAG.equals(DashboardActivity.ADD_TRIP_VALUE)) {
            isNewGoal= false;
            UpdateGoal(dataKey);
        }
        else {
            String trip_key = RootRef.child("Goals").child("Active").push().getKey();
            isNewGoal= true;
            CreteANewGoal(trip_key);
        }

    }

    private void UpdateGoal(String dataKey) {

        String string = tripname.getText().toString();
        String description = goalDesc.getText().toString();
        if (TextUtils.isEmpty (string)) {
            Toast.makeText(AddGoalActivity.this, "Enter any Trip name ..", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> onlineStat = new HashMap<>();
            onlineStat.put("GoalName", string);
            onlineStat.put("Goal_Description", description);

            RootRef.child("Goals").child("Active").child(dataKey)
                    .updateChildren(onlineStat);
            Intent loginIntent = new Intent ( AddGoalActivity.this,HomeActivity.class );
            loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( loginIntent );
        }
    }

    private void CreteANewGoal(String string_trip) {

        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        //String todaay is Today's Date
        String todaay  = format.format(today);
        String justToday = DateFormat.format(today);
        String Seven = "00:00:00:00:00:00:00";

        int year = datepicker.getYear();
        int month = datepicker.getMonth();
        int day = datepicker.getDayOfMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatt = new SimpleDateFormat("dd/M/yyyy");
        //String strDate is Selected Date
        String strDate = formatt.format(calendar.getTime())+" 23:59:59";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatt_only = new SimpleDateFormat("yyyy-M-dd");
        String strDate_only = formatt_only.format(calendar.getTime());

        String string = tripname.getText().toString();

        //MyCode Begins Here
        //Today's Date
        SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todaysDate = todayFormat.format(today);

        //Added Date
        // SimpleDateFormat addFormat = new SimpleDateFormat("dd/MM/yyyy");
        String calendarDate = todayFormat.format(calendar.getTime());

        //Conerting Strings to Date of same format
        LocalDate current = LocalDate.parse(todaysDate);
        LocalDate selected = LocalDate.parse(calendarDate);

        //Comparing Dates and storing them in Boolean Variables
        boolean bool1 = current.isAfter(selected); //Past Date
        boolean bool2 = current.isBefore(selected); //Future Date
        boolean bool3 = current.isEqual(selected); ///Today'S Date
        //Code Ends

        String description = goalDesc.getText().toString();

        if (TextUtils.isEmpty (string)) {
            Toast.makeText(AddGoalActivity.this, "Enter any Trip name ..", Toast.LENGTH_SHORT).show();

        } else if(set.contains(string) && !Edit.equals("true")) { //also checks if the function is called from Edit Activity
            Toast.makeText(this, "Goal Name Exists", Toast.LENGTH_SHORT).show();
        } else if(bool2 || bool3) //If the selected date is Future or Today's Date
        {
            HashMap<String,Object> onlineStat = new HashMap<> (  );
            onlineStat.put ( "GoalName", string);
            onlineStat.put ( "GoalType", string_priority);
            onlineStat.put ( "EndTime", strDate);
            onlineStat.put("Goal_Description",description);
            onlineStat.put ( "TodayTime", todaay);
            onlineStat.put ( "Consistency","0");
            onlineStat.put ( "Win","");
            onlineStat.put ("Status", "Active");
            onlineStat.put ("Notes", "");
            onlineStat.put ("Data/Seven", Seven);
            onlineStat.put ("Data/Date", justToday);

            RootRef.child("Goals").child("Active").child(string_trip)
                    .updateChildren ( onlineStat );

            if(isNewGoal){
                addActivity(string,todaay);
            }

            Intent loginIntent = new Intent ( AddGoalActivity.this,HomeActivity.class );
            loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( loginIntent );


        } else if(bool1) { //If the selected date is Past date
            Toast.makeText(this, "Invalid Date Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        string_priority = courses[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void retrievePreviousData() {
        //dataKey= bundle.getString(DashboardActivity.ADD_TRIP_DATA_KEY);
        dataKey= getIntent().getStringExtra(DashboardActivity.ADD_TRIP_DATA_KEY);

        RootRef.child("Goals").child("Active").child(dataKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get the previous goal name and description
                String prevName= Objects.requireNonNull(snapshot.child("GoalName").getValue()).toString ();
                String prevDesc = Objects.requireNonNull(snapshot.child("Goal_Description").getValue()).toString();
                // Set the goal name and description
                tripname.setText(prevName);
                goalDesc.setText(prevDesc);

                //Update other UI element
               // findViewById(R.id.create_goal_text_view).setVisibility(View.GONE);
                TextView tx = findViewById(R.id.create_goal_text_view);
                tx.setText("Edit your Goal");
                findViewById(R.id.TG).setVisibility(View.GONE);
                findViewById(R.id.edit_text_trip_date).setVisibility(View.GONE);
                findViewById(R.id.priority_layout).setVisibility(View.GONE);
                yes.setText("Update");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void addActivity(String goal, String time) {
        String key= activityRef.push().getKey();
        String value= "Created the "+goal+" on "+time;
        activityRef.child(Objects.requireNonNull(key)).setValue(value, (error, ref) -> Toast.makeText(AddGoalActivity.this,"Goal "+goal+" stored successfully",Toast.LENGTH_SHORT).show());
    }
}