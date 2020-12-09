package com.example.neveralone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    private TextView result_tv;
    private Button start_listen_btn,stop_listen_btn,mute;
    private SpeechRecognizerManager mSpeechManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    }

    public void fakeCall(View view){
        Toast.makeText(this, "Fake call!!", Toast.LENGTH_SHORT).show();
        //Problem in the intent after this one - VoiceCallActivity
        Intent intent = new Intent(getApplicationContext(), CallReciever.class);
        intent.setAction("call");
        startActivity(intent);
    }

    public void emergencyAction(View view){
        Toast.makeText(this, "Emergency!!", Toast.LENGTH_SHORT).show();

    }
    public void alertAction(View view){
        Toast.makeText(this, "Alert!", Toast.LENGTH_SHORT).show();
    }
    public void manualAlertAction(View view){
        Toast.makeText(this, "Manual Alert!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                start_listen_btn.performClick();
            }
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
}
