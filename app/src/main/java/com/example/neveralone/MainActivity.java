package com.example.neveralone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    private TextView result_tv;
    private Button start_listen_btn,stop_listen_btn,mute;
    private SpeechRecognizerManager mSpeechManager;
    SessionManager session;

    //For the emergency SMS
    private SmsManager sm;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    LocationManager locationManager;
    LocationListener locationListener;
    String locationStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        //For background alert system
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            return;
        }
        startService(new Intent(getApplicationContext(),AlertService.class));

         */
        session = new SessionManager(getApplicationContext());

        toolbar = (Toolbar)findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView)findViewById(R.id.navMenuId);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerId);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_profile :
                        Toast.makeText(MainActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
                        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(profileIntent);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menu_addcontacts:
                        Toast.makeText(MainActivity.this, "Add contacts", Toast.LENGTH_SHORT).show();
                        if(session.isLoggedIn()){
                            Intent psIntent = new Intent(getApplicationContext(), PeopleSecond.class);
                            startActivity(psIntent);
                        }else{
                            Intent pIntent = new Intent(getApplicationContext(), People.class);
                            startActivity(pIntent);
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menu_help :
                        Toast.makeText(MainActivity.this, "Help clicked", Toast.LENGTH_SHORT).show();
                        Intent helpIntent = new Intent(getApplicationContext(), HelpActivity.class);
                        startActivity(helpIntent);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        });

        result_tv=(TextView)findViewById(R.id.result_tv);
        start_listen_btn=(Button)findViewById(R.id.start);
        stop_listen_btn=(Button)findViewById(R.id.stop);
        mute=(Button)findViewById(R.id.mute);

        start_listen_btn.setOnClickListener(this);
        stop_listen_btn.setOnClickListener(this);
        mute.setOnClickListener(this);

        //For the emergency SMS
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationStr= location.getLatitude()+","+location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(RECORD_AUDIO);

        permissionsToRequest = findUnAskedPermissions(permissions);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }

    public void siren(View view){
        Toast.makeText(this, "Siren!!", Toast.LENGTH_SHORT).show();
        //Check this!! Remake
        Intent intent = new Intent(MainActivity.this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 232, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000), pendingIntent);
        Toast.makeText(MainActivity.this, "Alarm will start with in five seconds", Toast.LENGTH_SHORT).show();
    }

    public void sms(){
        Toast.makeText(this, "SMS!!", Toast.LENGTH_SHORT).show();
        HashMap<String, String> user = session.getUserDetails();

        String number1 = user.get(SessionManager.CONTACT1);
        // name
        String number2 = user.get(SessionManager.CONTACT2);

        // email
        String number3 = user.get(SessionManager.CONTACT3);
        sm = SmsManager.getDefault();

        if (number1 !=null) {

            sm.sendTextMessage(number1, null, "I'm in danger..My current location is http://maps.google.com/?q=" + locationStr, null, null);
            sm.sendTextMessage(number2, null, "I'm in danger..My current location is http://maps.google.com/?q=" + locationStr, null, null);
            sm.sendTextMessage(number3, null, "I'm in danger..My current location is http://maps.google.com/?q=" + locationStr, null, null);
            Toast.makeText(getApplicationContext(),"message sent",Toast.LENGTH_SHORT).show();
        } else {

            Intent i = new Intent(this, People.class);
            startActivity(i);
        }

    }

    public void fakeCall(){
        Toast.makeText(this, "Fake call!!", Toast.LENGTH_SHORT).show();
        //Problem in the intent after this one - VoiceCallActivity
        Intent intent = new Intent(getApplicationContext(), CallReciever.class);
        intent.setAction("call");
        startActivity(intent);
    }

    public void emergencyAction(View view){
        Toast.makeText(this, "Emergency!!", Toast.LENGTH_SHORT).show();
        sms();
        fakeCall();

    }
    public void alertAction(View view){
        Toast.makeText(this, "Alert!", Toast.LENGTH_SHORT).show();
    }
    public void manualAlertAction(View view){
        Toast.makeText(this, "Manual Alert!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

            switch (v.getId()) {
                case R.id.start:
                    if(mSpeechManager==null)
                    {
                        SetSpeechListener();
                    }
                    else if(!mSpeechManager.ismIsListening())
                    {
                        mSpeechManager.destroy();
                        SetSpeechListener();
                    }
                    result_tv.setText("Are you in danger?");

                    break;
                case R.id.stop:
                    if(mSpeechManager!=null) {
                        result_tv.setText("Destroyed");
                        mSpeechManager.destroy();
                        mSpeechManager = null;
                    }
                    break;
                case R.id.mute:
                    if(mSpeechManager!=null) {
                        if(mSpeechManager.isInMuteMode()) {
                            mute.setText("Mute");
                            mSpeechManager.mute(false);
                        }
                        else
                        {
                            mute.setText("Unmute");
                            mSpeechManager.mute(true);
                        }
                    }
                    break;
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, 1);
        }
    }

    private void SetSpeechListener()
    {
        mSpeechManager=new SpeechRecognizerManager(this, new SpeechRecognizerManager.onResultsReady() {
            @Override
            public void onResults(ArrayList<String> results) {



                if(results!=null && results.size()>0)
                {

                    if(results.size()==1)
                    {
                        mSpeechManager.destroy();
                        mSpeechManager = null;
                        result_tv.setText(results.get(0));
                    }
                    else {
                        StringBuilder sb = new StringBuilder();
                        if (results.size() > 5) {
                            results = (ArrayList<String>) results.subList(0, 5);
                        }
                        for (String result : results) {
                            sb.append(result).append("\n");
                        }
                        result_tv.setText(sb.toString());
                    }
                }
                else
                    result_tv.setText("No results found");
            }
        });
    }

    @Override
    protected void onPause() {
        if(mSpeechManager!=null) {
            mSpeechManager.destroy();
            mSpeechManager=null;
        }
        super.onPause();
    }

    //Location things
    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            //Toast.makeText(this, "These permissions are mandatory for the application. Please allow access.", Toast.LENGTH_SHORT).show();
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
