package com.mobiric.demo.wifiwidget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;
import com.mobiric.demo.wifiwidget.R;

/**
 * Service that provides background processing in order to update the info on
 * the widget.
 */
public class WifiWidgetService extends Service
{
	String ipAddress;
	boolean wifiConnected = false;
	Thread widgetUpdateThread;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		widgetUpdateThread = new WidgetUpdateThread(this, intent);
		widgetUpdateThread.start();

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	/**
	 * @deprecated Binding not allowed from a widget - use the Command Pattern
	 *             instead.
	 */
	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	/**
	 * Populates the {@link #ipAddress} and {@link #wifiConnected} fields based
	 * on the current connection.
	 */
	private void initNetworkInfo()
	{
		// get connection info if available
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();

		if ((info != null) && (info.isConnected())
			&& (ConnectivityManager.TYPE_WIFI == info.getType()))
		{
			wifiConnected = true;
			ipAddress = getIpAddr();
		}
		else
		{
			wifiConnected = false;
			ipAddress = this.getString(R.string.wifi_status_not_connected);
		}
	}

	/**
	 * Determines IP address from WIFI connection.
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private String getIpAddr()
	{
		String ipString = null;
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		if (wifiManager != null)
		{
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			if (wifiInfo != null)
			{
				int ip = wifiInfo.getIpAddress();

				ipString = String.format("%d.%d.%d.%d", (ip & 0xff),
					(ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));

			}
		}
		return ipString;
	}


	private class WidgetUpdateThread extends Thread
	{
		Context context;

		WidgetUpdateThread(Context context, Intent intent)
		{
			this.context = context;
		}

		@Override
		public void run()
		{
			try
			{
				initNetworkInfo();

				/* PREPARE THE UPDATE */

				// set remote views
				RemoteViews widgetUi = new RemoteViews(
					context.getPackageName(), R.layout.widget);
				widgetUi.setTextViewText(R.id.tvIpAddress, ipAddress);

				if (wifiConnected)
				{
					widgetUi.setImageViewResource(R.id.ivLogo,
						R.drawable.wifi_on);
				}
				else
				{
					widgetUi.setImageViewResource(R.id.ivLogo,
						R.drawable.wifi_off);
				}


				// set the click actions to open wifi settings
				Intent wifiIpSettings = new Intent(
					Settings.ACTION_WIFI_IP_SETTINGS);
				PendingIntent pendingIntentIp = PendingIntent.getActivity(
					context, 0, wifiIpSettings,
					PendingIntent.FLAG_UPDATE_CURRENT);
				widgetUi.setOnClickPendingIntent(R.id.tvIpAddress,
					pendingIntentIp);

				Intent wifiSettings = new Intent(Settings.ACTION_WIFI_SETTINGS);
				PendingIntent pendingIntentImage = PendingIntent
					.getActivity(context, 0, wifiSettings,
						PendingIntent.FLAG_UPDATE_CURRENT);
				widgetUi.setOnClickPendingIntent(R.id.ivLogo,
					pendingIntentImage);


				/* UPDATE THE WIDGET INSTANCE */

				try
				{
					ComponentName widgetComponent = new ComponentName(context,
						WifiWidgetProvider.class);
					AppWidgetManager widgetManager = AppWidgetManager
						.getInstance(context);
					widgetManager.updateAppWidget(widgetComponent, widgetUi);
				}
				catch (Exception e)
				{
					Log.e("WifiWidget", "Failed to update widget", e);
				}
			}
			catch (Exception e)
			{
				Log.e("WifiWidget", "Failed to update widget", e);
			}
			finally
			{
				// clean up
				WifiWidgetService.this.stopSelf();
			}
		}
	}


}
