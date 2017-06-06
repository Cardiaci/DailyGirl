package com.cardiaci.dailygirl.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.cardiaci.dailygirl.service.AlarmReceiver;

import java.util.Calendar;

public class AlarmManagerUtils {

  public static void register(Context context) {

    Calendar today = Calendar.getInstance();
    Calendar now = Calendar.getInstance();

    today.set(Calendar.HOUR_OF_DAY, 12);
    today.set(Calendar.MINUTE, 30);
    today.set(Calendar.SECOND, 10);

    if (now.after(today)) {
      return;
    }

    Intent intent = new Intent("com.hotbitmapgg.moequest.alarm");
    intent.setClass(context, AlarmReceiver.class);

    PendingIntent broadcast = PendingIntent.getBroadcast(context, 520, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    manager.set(AlarmManager.RTC_WAKEUP, today.getTimeInMillis(), broadcast);
  }
}
