package mobiric.demo.wifiwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * A very basic {@link AppWidgetProvider} implementation that delegates
 * the actual processing to the {@link WifiWidgetService}.
 */
public class WifiWidgetProvider extends AppWidgetProvider
{

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		/*
		 * AppWidgetProvider extends BroadcastReceiver, so we must not spend
		 * lots of processing time in this class. Actual processing is done in
		 * a Service so that this method can return as quickly as possible.
		 */
		context.startService(getIntentForService(context));
	}

	/**
	 * Stops the background service when the widget is removed.
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		context.stopService(getIntentForService(context));

		super.onDeleted(context, appWidgetIds);
	}

	/**
	 * Helper method to create the correct {@link Intent} to use when working
	 * with the {@link WifiWidgetService}.
	 * 
	 * @param context
	 *        Context to use for the Intent
	 * @return Intent that can be used to interact with the
	 *         {@link WifiWidgetService}
	 */
	private Intent getIntentForService(Context context)
	{
		Intent widgetService = new Intent(context.getApplicationContext(), WifiWidgetService.class);
		return widgetService;
	}

}