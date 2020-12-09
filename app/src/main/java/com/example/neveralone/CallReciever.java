package com.example.neveralone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class CallReciever extends AppCompatActivity implements View.OnClickListener {

    ImageView attendCall, endCall;
    private Ringtone r;
    private Vibrator myVib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_reciever);

        playRingtone();

        attendCall = (ImageView) findViewById(R.id.attend_call);
        endCall = (ImageView) findViewById(R.id.end_call);
        attendCall.setOnClickListener(this);
        endCall.setOnClickListener(this);
    }

    private Ringtone playRingtone() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        r = RingtoneManager.getRingtone(getApplicationContext(), notification);

        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        myVib.vibrate(40000);

        r.play();
        return r;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attend_call:
                Log.i("Recieve call", "Reached 1: pressed");
                if (r.isPlaying()) {
                    r.stop();
                    myVib.cancel();
                }
                Log.i("Recieve call", "Reached 2");
                Intent intent1 = new Intent(getApplicationContext(), VoiceCallActivity.class);
                Log.i("Recieve call", "Reached 3");
                startActivity(intent1);
                finish();
                break;

            case R.id.end_call:
                if (r.isPlaying()) {
                    r.stop();
                    myVib.cancel();
                }

                finish();
                break;
        }
    }
}
