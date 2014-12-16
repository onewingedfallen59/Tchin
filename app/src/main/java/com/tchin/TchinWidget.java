package com.tchin;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.text.DecimalFormat;
import java.util.Calendar;


/**
 * Implementation of App Widget functionality.
 */
public class TchinWidget extends AppWidgetProvider {
    public static final String ONCLICK = "ONCLICK";
    public static final String ONNEWDAY = "ONNEWDAY";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TchinWidget.class);
        intent.setAction(ONNEWDAY);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        SharedPreferences preferences = context.getSharedPreferences(TchinWidget.class.getSimpleName(), Context.MODE_PRIVATE);

        float water = preferences.getFloat("water", 0f);
        DecimalFormat df = new DecimalFormat("0.00");
        CharSequence widgetText = df.format(water);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tchin_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        Intent intent = new Intent(context, TchinWidget.class);
        intent.setAction(ONCLICK);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pIntent);

        if(water < .15f)
        {
            views.setTextViewCompoundDrawablesRelative(R.id.appwidget_text, R.drawable.empty_glass, 0, 0, 0);
        }
        else if(water < .3f)
        {
            views.setTextViewCompoundDrawablesRelative(R.id.appwidget_text, R.drawable.glass_1, 0, 0, 0);
        }
        else if (water < .6f)
        {
            views.setTextViewCompoundDrawablesRelative(R.id.appwidget_text, R.drawable.glass_2, 0, 0, 0);
        }
        else if (water < .9f)
        {
            views.setTextViewCompoundDrawablesRelative(R.id.appwidget_text, R.drawable.glass_3, 0, 0, 0);
        }
        else if(water < 1.2f)
        {
            views.setTextViewCompoundDrawablesRelative(R.id.appwidget_text, R.drawable.glass_4, 0, 0, 0);
        }
        else if (water >= 1.5f)
        {
            views.setTextViewCompoundDrawablesRelative(R.id.appwidget_text, R.drawable.full_glass, 0, 0, 0);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        SharedPreferences preferences = context.getSharedPreferences(TchinWidget.class.getSimpleName(), Context.MODE_PRIVATE);
        float water = preferences.getFloat("water", 0f);
        if(intent.getAction().equalsIgnoreCase(ONCLICK))
        {
            water += .15d;
            preferences.edit().putFloat("water", water).apply();
        }
        else if(intent.getAction().equalsIgnoreCase(ONNEWDAY))
        {
            preferences.edit().clear().apply();
            ComponentName thisWidget = new ComponentName(context.getApplicationContext(), TchinWidget.class);
            final int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(thisWidget);
            onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
        }
        ComponentName thisWidget = new ComponentName(context.getApplicationContext(), TchinWidget.class);
        final int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(thisWidget);
        onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
    }
}


