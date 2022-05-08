package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DateBookingActivity extends AppCompatActivity {
    private static final String LOG_TAG = DateBookingActivity.class.getName();

    EditText dateTXT;
    ImageView cal;
    private int mDate, mMonth, mYear;
    Button notify_Button;
    Button logout;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_booking);


        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        dateTXT = findViewById(R.id.date);
        cal = findViewById(R.id.datepicker);
        notify_Button = findViewById(R.id.notifyButton);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My notification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        dateTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar Cal = Calendar.getInstance();
                mDate = Cal.get(Calendar.DATE);
                mMonth = Cal.get(Calendar.MONTH);
                mYear = Cal.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DateBookingActivity.this, android.R.style.Theme_DeviceDefault_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        dateTXT.setText(date+ "/"+month+"/"+year);

                    }
                }, mYear,mMonth,mDate);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });

        notify_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NotificationCompat.Builder builder = new NotificationCompat.Builder(DateBookingActivity.this,"My Notification");
                builder.setContentTitle("A Foglalt időpont");
                builder.setContentText("Az időpont"+ dateTXT);
                builder.setSmallIcon(R.drawable.calendar);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(DateBookingActivity.this);
                managerCompat.notify(1,builder.build());
            }
        });


    }

    public void post(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String , Object> date = new HashMap<>();
        date.put("user", user);
        date.put("day",mDate);
        date.put("month",mMonth);
        date.put("year",mYear);

        db.collection("date").document("idopontok").set(date).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(DateBookingActivity.this,"Values added",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void logout(View view) {
        logout = findViewById(R.id.logOut);
        mAuth.signOut();
    }
}