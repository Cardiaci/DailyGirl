package com.cardiaci.dailygirl.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cardiaci.dailygirl.R;
import com.cardiaci.dailygirl.module.commonality.MainActivity;
import com.cardiaci.dailygirl.utils.HeadsUpUtils;
import com.cardiaci.dailygirl.utils.PreferencesLoader;


public class AlarmReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {

    PreferencesLoader loader = new PreferencesLoader(context);
    if (loader.getBoolean(R.string.action_notifiable, true)) {
      HeadsUpUtils.show(context, MainActivity.class,
          context.getString(R.string.headsup_title),
          context.getString(R.string.headsup_content),
          R.mipmap.ic_launcher, R.drawable.bg, 123123);
    }
  }
}