package diorid.off;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetActivity extends AppWidgetProvider {

    private static final long DOUBLE_CLICK_WINDOW = 600;
    private static volatile long firstClickTimeReference;
    final String TAG = getClass().getSimpleName();

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        for (int appWidgetId : appWidgetIds) {
            long currentSystemTime = System.currentTimeMillis();

            if (currentSystemTime - firstClickTimeReference <= DOUBLE_CLICK_WINDOW) {

                new Thread() {
                    public void run() {
                        screenOff(context);
                    }
                }.start();


            } else {
                firstClickTimeReference = currentSystemTime;

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.activity_widget);

                Intent intent = new Intent(context, WidgetActivity.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.btnOff, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }

        }

    }

    private void screenOff(Context context) {

        RootHelper helper = new RootHelper();
        if (!helper.isRooted()) {
            Toast.makeText(context, "Couldn't get root", Toast.LENGTH_SHORT).show();
        }
        try {
            helper.sudoAndExit("input keyevent KEYCODE_POWER", 1000);
            helper.tryExitAndClean();
        } catch (Exception localException) {
            Log.e(this.TAG, localException.getLocalizedMessage());
        }
    }
}
