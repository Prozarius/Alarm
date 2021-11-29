package com.example.music_player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
  Intent mIntent;
  public static final String KEY = "KEY";
  public static final String ID = "ID";
  private int n_id;

  @Override
  public void onReceive(Context context, Intent intent){
    n_id = intent.getIntExtra(ID, 0);

    //Toast.makeText(context, Integer.toString(n_id), Toast.LENGTH_SHORT).show();
    mIntent = new Intent(context, MainActivity.class);
    mIntent.putExtra(KEY, true);
    mIntent.putExtra(ID, n_id);

    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.getApplicationContext().startActivity(mIntent);
  }
}
