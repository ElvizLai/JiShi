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
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;
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
    final String USER_PRESENT = "android.intent.action.USER_PRESENT";
    final private Time time = new Time();//用来获取当前时间
    private Timer totalTimer = new Timer();//计时器
    private int isClockOn = 1;//用来存储屏幕的状态 1表示开启，0表示关闭

    private Context context;
    private AppWidgetManager appWidgetManager;
    private ComponentName thisWidget;
    private int[] appWidgetIds;


    final private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {
        //接收系统广播
        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.d(TAG, "Broadcast on receive");
            if (intent.getAction().equals(SCREEN_ON)) {
                Log.d(TAG, "Screen ON!");
                //用解锁来替代
                //updateWidgets();
                //isClockOn = 1;
            }
            if (intent.getAction().equals(SCREEN_OFF)) {
                Log.d(TAG, "Screen OFF!");
                isClockOn = 0;
            }

            if (intent.getAction().equals(USER_PRESENT)) {
                Log.d(TAG, "USER_PRESENT!");
                isClockOn = 1;
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

        context = getBaseContext();
        appWidgetManager = AppWidgetManager.getInstance(context);
        thisWidget = new ComponentName(context, AppWidget.class);
        appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);


        //广播注册
        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(SCREEN_ON);            //添加动态广播的Action
        dynamic_filter.addAction(SCREEN_OFF);
        dynamic_filter.addAction(USER_PRESENT);
        registerReceiver(dynamicReceiver, dynamic_filter);    // 注册自定义动态广播消息

        //1s的定时器，准备用alarm来替换
        totalTimer.schedule(new TimeUpdateTask(), 0, 1000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service onDestroy");
        //注销广播
        unregisterReceiver(dynamicReceiver);
        totalTimer.cancel();
        totalTimer = null;
    }

    /**
     * @param date   时间
     * @param number 自视图的编号 1~5 0是特殊的
     */

    private void updateSecond(Time date, int number) {
        //Log.d(TAG, "updateSecond");
        int second = date.second;

        String nextSecond = (second + 1) < 10 ? "0" + (second + 1) : (second + 1 + "");
        if (second == 59) {
            nextSecond = "00";
        }

        String padding = "flip_" + (second < 10 ? "0" + second : second) + "_" + nextSecond + "_" + number;
        if (number == 0) {
            padding = "flip_" + (second < 10 ? "0" + second : second);
        }

        // System.out.println(padding);

        //int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_layout);
            views.setImageViewResource(R.id.second, getResources().getIdentifier(padding, "drawable", PACKAGESTR));
            appWidgetManager.updateAppWidget(id, views);
        }
    }

    private void updateMinute(Time date, int number) {

        //Log.d(TAG, "updateMinute");

        int minute = date.minute;

        String nextMinute = (minute + 1) < 10 ? "0" + (minute + 1) : (minute + 1 + "");
        if (minute == 59) {
            nextMinute = "00";
        }

        String padding = "flip_" + (minute < 10 ? "0" + minute : minute);
        if (date.second == 59 && number != 0) {
            padding = "flip_" + (minute < 10 ? "0" + minute : minute) + "_" + nextMinute + "_" + number;
        }

//        Context context = getBaseContext();
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_layout);
//        views.setImageViewResource(R.id.minute, getResources().getIdentifier(padding, "drawable", PACKAGESTR));


        // System.out.println(padding);

//        Context context = getBaseContext();
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//        ComponentName thisWidget = new ComponentName(context, AppWidget.class);
//
        // int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_layout);
            views.setImageViewResource(R.id.minute, getResources().getIdentifier(padding, "drawable", PACKAGESTR));
            appWidgetManager.updateAppWidget(id, views);
        }
    }

    private void updateHour(Time date, int number) {

        //Log.d(TAG, "updateHour");

        int hour = date.hour;

        String nextHour = (hour + 1) < 10 ? "0" + (hour + 1) : (hour + 1 + "");
        if (hour == 59) {
            nextHour = "00";
        }

        String padding = "flip_" + (hour < 10 ? "0" + hour : hour);
        if (date.second == 59 && date.minute == 59 && number != 0) {
            padding = "flip_" + (hour < 10 ? "0" + hour : hour) + "_" + nextHour + "_" + number;
        }

        //System.out.println(padding);

        //int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

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
            if (!isHome() || isClockOn == 0 || totalTimer == null) {
                return;
            }


            Log.d(TAG, "Timer is running");

            //Calendar cal = Calendar.getInstance();


            time.setToNow();
//            System.out.println(time.hour + "");
//            System.out.println(time.minute + "");
//            System.out.println(time.second + "");

            totalTimer.schedule(new Timer2Flip(time, 0), 0);
            totalTimer.schedule(new Timer2Flip(time, 1), 600);
            totalTimer.schedule(new Timer2Flip(time, 2), 680);
            totalTimer.schedule(new Timer2Flip(time, 3), 760);
            totalTimer.schedule(new Timer2Flip(time, 4), 840);
            totalTimer.schedule(new Timer2Flip(time, 5), 920);
        }
    }

    private class Timer2Flip extends TimerTask {
        int number;
        Time date;

        Timer2Flip(Time date, int number) {
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
