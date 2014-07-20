package com.elvizlai.jishi;

import android.app.ActivityManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lyz on 14-7-20.
 */
public class TimeService extends Service {
    final String PACKAGESTR = "com.elvizlai.jishi";
    final String TAG = "ElvizLai";
    final String SCREEN_ON = "android.intent.action.SCREEN_ON";
    final String SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    final private Timer totalTimer = new Timer();
    private int isClockOn = 1;
    final private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {
        //动态广播的Receiver
        @Override
        public void onReceive(Context context, Intent intent) {
           // Log.d(TAG, "Broadcast on receive");
            if (intent.getAction().equals(SCREEN_ON)) {
                Log.d(TAG, "Screen ON!");
                //updateWidgets();
                isClockOn = 1;
            }
            if (intent.getAction().equals(SCREEN_OFF)) {
                Log.d(TAG, "Screen OFF!");
                isClockOn = 0;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service on bind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Service on create");

        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(SCREEN_ON);            //添加动态广播的Action
        dynamic_filter.addAction(SCREEN_OFF);
        registerReceiver(dynamicReceiver, dynamic_filter);    // 注册自定义动态广播消息

        totalTimer.schedule(new TimeUpdateTask(), 0, 1000);
    }

    private void updateSecond(Calendar date, int number) {
        //Log.d(TAG, "updateSecond");
        int second = date.get(Calendar.SECOND);

        String nextSecond = (second + 1) < 10 ? "0" + (second + 1) : (second + 1 + "");
        if (second == 59) {
            nextSecond = "00";
        }

        String padding = "flip_" + (second < 10 ? "0" + second : second) + "_" + nextSecond + "_" + number;
        if (number == 0) {
            padding = "flip_" + (second < 10 ? "0" + second : second);
        }

        // System.out.println(padding);

        Context context = getBaseContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, AppWidget.class);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_layout);
            views.setImageViewResource(R.id.second, getResources().getIdentifier(padding, "drawable", PACKAGESTR));
            appWidgetManager.updateAppWidget(id, views);
        }
    }

    private void updateMinute(Calendar date, int number) {

        //Log.d(TAG, "updateMinute");


        int minute = date.get(Calendar.MINUTE);

        String nextMinute = (minute + 1) < 10 ? "0" + (minute + 1) : (minute + 1 + "");
        if (minute == 59) {
            nextMinute = "00";
        }

        String padding = "flip_" + (minute < 10 ? "0" + minute : minute);
        if (date.get(Calendar.SECOND) == 59 && number != 0) {
            padding = "flip_" + (minute < 10 ? "0" + minute : minute) + "_" + nextMinute + "_" + number;
        }


        // System.out.println(padding);

        Context context = getBaseContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, AppWidget.class);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_layout);
            views.setImageViewResource(R.id.minute, getResources().getIdentifier(padding, "drawable", PACKAGESTR));
            appWidgetManager.updateAppWidget(id, views);
        }
    }

    private void updateHour(Calendar date, int number) {

        //Log.d(TAG, "updateHour");

        int hour = date.get(Calendar.HOUR);

        String nextHour = (hour + 1) < 10 ? "0" + (hour + 1) : (hour + 1 + "");
        if (hour == 59) {
            nextHour = "00";
        }

        String padding = "flip_" + (hour < 10 ? "0" + hour : hour);
        if (date.get(Calendar.MINUTE) == 59 && number != 0) {
            padding = "flip_" + (hour < 10 ? "0" + hour : hour) + "_" + nextHour + "_" + number;
        }

        //System.out.println(padding);

        Context context = getBaseContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, AppWidget.class);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_layout);
            views.setImageViewResource(R.id.hour, getResources().getIdentifier(padding, "drawable", PACKAGESTR));
            appWidgetManager.updateAppWidget(id, views);
        }
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = TimeService.this.getPackageManager();
        //属性
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
            // System.out.println(ri.activityInfo.packageName);
        }
        return names;
    }

    /**
     * 判断当前界面是否是桌面
     */
    public boolean isHome() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes().contains(rti.get(0).topActivity.getPackageName());
    }

    private class TimeUpdateTask extends TimerTask {

        @Override
        public void run() {
            if (!isHome() || isClockOn == 0) {
                return;
            }

            Log.d(TAG, "Timer is running");

            Calendar calendar = Calendar.getInstance();

            totalTimer.schedule(new Timer2Flip(calendar, 0), 0);
            totalTimer.schedule(new Timer2Flip(calendar, 1), 1000 - 400);
            totalTimer.schedule(new Timer2Flip(calendar, 2), 1000 - 350);
            totalTimer.schedule(new Timer2Flip(calendar, 3), 1000 - 250);
            totalTimer.schedule(new Timer2Flip(calendar, 4), 1000 - 200);
            totalTimer.schedule(new Timer2Flip(calendar, 5), 1000 - 100);
        }
    }

    private class Timer2Flip extends TimerTask {
        int number;
        Calendar date;

        Timer2Flip(Calendar date, int number) {
            this.date = date;
            this.number = number;
        }

        @Override
        public void run() {
            updateSecond(date, number);
            updateMinute(date, number);
            updateHour(date, number);
        }
    }
}
