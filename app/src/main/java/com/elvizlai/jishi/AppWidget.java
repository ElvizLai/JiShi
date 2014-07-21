package com.elvizlai.jishi;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by lyz on 14-7-20.
 */
public class AppWidget extends AppWidgetProvider {
    final String TAG = "ElvizLai";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "widget on enable");
        context.startService(new Intent(context, TimeService.class));

    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(TAG, "widget on disable");
        context.stopService(new Intent(context, TimeService.class));

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(TAG, "widget on delete");
        //context.stopService(new Intent(context, TimeService.class));
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "widget on receive");

        //System.out.println(intent.getAction()+"");

        if (intent.getAction().equals("android.intent.action.TIME_SET")) {
            //时间更改后同步下表
            context.stopService(new Intent(context, TimeService.class));
            context.startService(new Intent(context, TimeService.class));
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "widget on update");

        context.stopService(new Intent(context, TimeService.class));
        context.startService(new Intent(context, TimeService.class));

    }


}
