package com.example.music_player;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

  MediaPlayer mPlayer;
  Button playButton, pauseButton, stopButton;
  SeekBar volumeControl;
  AudioManager audioManager;
  TextView mtvTrack;
  TimePicker timePicker;

  public static final String KEY = "KEY";
  public static final String ID = "ID";
  boolean key;
  private int id;
  private final int[] table = {R.raw.track001, R.raw.track002, R.raw.track003, R.raw.track007, R.raw.track010, R.raw.track011 };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Intent intent = getIntent();
    key = intent.getBooleanExtra(KEY,false);
    id  = intent.getIntExtra(ID, 0);

    timePicker = (TimePicker) findViewById(R.id.timePicker);

    mPlayer = MediaPlayer.create(this, table[id]);
    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override
      public void onCompletion(MediaPlayer mp) {
        stopPlay();
      }
    });

    mtvTrack    = (TextView) findViewById(R.id.mtvTrack);
    mtvTrack.setText("Track "+Integer.toString(id+1));

    playButton  = (Button) findViewById(R.id.playButton);
    pauseButton = (Button) findViewById(R.id.pauseButton);
    stopButton  = (Button) findViewById(R.id.stopButton);

    audioManager  = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    int curValue  = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

    volumeControl = (SeekBar) findViewById(R.id.volumeControl);
    volumeControl.setMax(maxVolume);
    volumeControl.setProgress(curValue);
    volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
      }
      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }
      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    pauseButton.setEnabled(false);
    stopButton.setEnabled(false);

    if(key) play();
  }

  private void stopPlay(){
    mPlayer.stop();

    pauseButton.setEnabled(false);
    stopButton.setEnabled(false);

    try {
      mPlayer.prepare();
      mPlayer.seekTo(0);
      playButton.setEnabled(true);
    }
    catch (Throwable t) {
      Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  public void play(){
    mPlayer.start();

    playButton.setEnabled(false);
    pauseButton.setEnabled(true);
    stopButton.setEnabled(true);
  }

  public void play_music(View view){play();}

  public void pause(View view){
    mPlayer.pause();

    playButton.setEnabled(true);
    pauseButton.setEnabled(false);
    stopButton.setEnabled(true);
  }

  public void stop(View view){ stopPlay(); }

  @Override
  public void onDestroy() {
    if (mPlayer.isPlaying()) stopPlay();
    super.onDestroy();
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void sleep(View view){
    if (mPlayer.isPlaying()) stopPlay();
    AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
    intent.putExtra(ID,id);
    intent.putExtra(KEY,true);

    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    Calendar time = Calendar.getInstance();
    time.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
    time.set(Calendar.MINUTE, timePicker.getMinute());

    //time.setTimeInMillis(System.currentTimeMillis());
    //time.add(Calendar.SECOND, 30);
    alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
    finish();
  }

  public void prev(View view){
    stopPlay();
    id = (id + 5) % 6;
    mPlayer = MediaPlayer.create(this, table[id]);
    mtvTrack.setText("Track "+Integer.toString(id+1));
  }

  public void next(View view){
    stopPlay();
    id = (id + 1) % 6;
    mPlayer = MediaPlayer.create(this, table[id]);
    mtvTrack.setText("Track "+Integer.toString(id+1));
  }
}