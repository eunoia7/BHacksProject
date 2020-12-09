package com.example.neveralone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("MyBroadcatReceiver", "Reached");
        //Toast.makeText(context, "Reached MyBroadcastReceiver", Toast.LENGTH_SHORT).show();
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.outgoingtone);
        Log.i("MyBroadcatReceiver", "Reached. Should be play the alarm");
        //Toast.makeText(context, "Reached. Should be play the alarm", Toast.LENGTH_SHORT).show();
        mediaPlayer.start();
        Log.i("MyBroadcatReceiver", "Reached. Alarm...");
        Toast.makeText(context,"Alarm..",Toast.LENGTH_SHORT).show();
    }
}
