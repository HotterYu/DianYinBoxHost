package com.znt.speaker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.qihoo360.replugin.RePluginApplication;

public class MyApplication extends RePluginApplication {

	private static final String TAG = MyApplication.class.getSimpleName();
	private final int NOTIFICATIONID = 7890;
	private static MyApplication mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		
		mInstance = this;
	}
	
	/**
	 * @return the Context of this application
	 */
	public static Context getInstance() {
		if (mInstance == null)
			Log.e(TAG, "Global context not set");
		return mInstance;
	}

	/**
	 * Get the version from the manifest.
	 * 
	 * @return The version as a String.
	 */
	public static String getVersion() {
		Context context = getInstance();
		String packageName = context.getPackageName();
		try {
			PackageManager pm = context.getPackageManager();
			return pm.getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Unable to find the name " + packageName
					+ " in the package");
			return null;
		}
	}
	@SuppressLint("StringFormatInvalid")
	public void setupNotification(Context context) {
		Log.d(TAG, "Setting up the notification");
		// Get NotificationManager reference
		/*String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) context
				.getSystemService(ns);

		// get ip address
		InetAddress address = FtpServerService.getLocalInetAddress();
		if (address == null) {
			Log.w(TAG, "Unable to retreive the local ip address");
			return;
		}
		String iptext = "ftp://" + address.getHostAddress() + ":"
				+ FtpServerService.getPort() + "/";

		// Instantiate a Notification
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = String.format(
				context.getString(R.string.notif_server_starting), iptext);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		// Define Notification's message and Intent
		CharSequence contentTitle = context.getString(R.string.notif_title);
		CharSequence contentText = iptext;

		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		// Pass Notification to NotificationManager
		nm.notify(NOTIFICATIONID, notification);*/

		Log.d(TAG, "Notication setup done");
	}

	public void clearNotification(Context context) {
		Log.d(TAG, "Clearing the notifications");
		/*String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) context
				.getSystemService(ns);
		nm.cancelAll();*/
		Log.d(TAG, "Cleared notification");
	}
}
