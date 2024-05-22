package udc.psi.busgo.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Random;

import udc.psi.busgo.R;

public class StopsWidget extends AppWidgetProvider {

    private static final String ACTION_UPDATE_WIDGET = "ACTION_UPDATE_WIDGET";
    private static final CharSequence[] refreshStrings = {"AAAAAAAAA", "BBBBBBBBBBB", "CCCCCCCCCCC"};

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        int index = new Random().nextInt(refreshStrings.length);
        CharSequence stringText = refreshStrings[index];
        Log.d("_TAG23", "UPDATEEEEEEEEEEEEEEEE");
        Log.d("_TAG23", (String) stringText);
        Log.d("_TAG23", String.valueOf(index));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stops_widget);
        views.setTextViewText(R.id.stopsWidgetUndefinedStopsId, stringText);

        Intent intent = new Intent(context, StopsWidget.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.stopsWidgetUndefinedStopsButton, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        //Actualiza el widget correspondiente
        if (ACTION_UPDATE_WIDGET.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }
}
