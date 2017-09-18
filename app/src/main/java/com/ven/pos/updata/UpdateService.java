package com.ven.pos.updata;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.cnyssj.pos.R;
import com.jhj.Agreement.ZYB.SharedPreferences_util;

/***
 * 升级服务
 * 
 * @author hexiaoming
 * 
 */
public class UpdateService extends Service {

	public static final String Install_Apk = "Install_Apk";
	private static final int down_step_custom = 3;

	private static final int TIMEOUT = 10 * 1000;// 超时
	private static String down_url;
	private static final int DOWN_OK = 1;
	private static final int DOWN_ERROR = 0;

	private String app_name;

	private NotificationManager notificationManager;
	private Notification notification;
	private PendingIntent pendingIntent;
	private RemoteViews contentView;
	private boolean sign_down_version = false;
	SharedPreferences_util su = new SharedPreferences_util();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		Message message = new Message();
		message.what = DOWN_ERROR;
		handler.sendMessage(message);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		app_name = intent.getStringExtra("Key_App_Name");
		down_url = intent.getStringExtra("Key_Down_Url");

		FileUtil.createFile(app_name);

		if (FileUtil.isCreateFileSucess == true) {
			createNotification();
			createThread();
		} else {
			if(!TextUtils.isEmpty(app_name)){}
			Toast.makeText(this, "请插入SD卡", Toast.LENGTH_SHORT).show();
			stopSelf();
		}
//		return super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}

	/********* update UI ******/
	private final Handler handler = new Handler() {
		@SuppressWarnings({ "deprecation", "static-access" })
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_OK:
				sign_down_version = false;
				su.setPrefboolean(getApplicationContext(), "sign_down_version",
						sign_down_version);

				Uri uri = Uri.fromFile(FileUtil.updateFile);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(uri,
						"application/vnd.android.package-archive");
				pendingIntent = PendingIntent.getActivity(UpdateService.this,
						0, intent, 0);

				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notification.setLatestEventInfo(UpdateService.this, app_name,
						"", pendingIntent);

				notificationManager.notify(R.layout.notification_item,
						notification);

				installApk();
				stopSelf();
				break;

			case DOWN_ERROR:
				sign_down_version = false;
				su.setPrefboolean(getApplicationContext(), "sign_down_version",
						sign_down_version);
				notification.flags = Notification.FLAG_AUTO_CANCEL;

				notification.setLatestEventInfo(UpdateService.this, app_name,
						"下载失败", null);

				/*** stop service *****/

				stopSelf();
				break;

			default:

				break;
			}
		}
	};

	private void installApk() {
		/********* 下载完成，点击安装 ***********/
		Uri uri = Uri.fromFile(FileUtil.updateFile);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		/********** 加这个属性是因为使用Context的startActivity方法的话，就需要开启一个新的task **********/
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		UpdateService.this.startActivity(intent);
	}

	/**
	 * 方法描述：createThread方法, 开线程下载
	 * 719845771132
	 * @param
	 * @return
	 * @see UpdateService
	 */
	public void createThread() {
		new DownLoadThread().start();
	}

	private class DownLoadThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message message = new Message();
			try {
				long downloadSize = downloadUpdateFile(down_url,
						FileUtil.updateFile.toString());
				if (downloadSize > 0) {
					// 下载完成
					message.what = DOWN_OK;
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.what = DOWN_ERROR;
				handler.sendMessage(message);
			}
		}
	}

	/**
	 * 方法描述：createNotification方法
	 * 
	 * @see UpdateService
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void createNotification() {
		sign_down_version = true;
		su.setPrefboolean(getApplicationContext(), "sign_down_version",
				sign_down_version);

		notification = new Notification(R.drawable.ic_launcher,// 应用的图标
				app_name + "正在下载", System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		/*** 自定义 Notification 的显示 ****/
		contentView = new RemoteViews(getPackageName(),
				R.layout.notification_item);
		contentView.setTextViewText(R.id.notificationTitle, app_name + "正在下载");
		contentView.setTextViewText(R.id.notificationPercent, "0%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		notification.contentView = contentView;

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(R.layout.notification_item, notification);
	}

	/***
	 * down file
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public long downloadUpdateFile(String down_url, String file)
			throws Exception {

		int down_step = down_step_custom;// 提示step
		int totalSize;// 文件总大小
		int downloadCount = 0;// 已经下载好的大小
		int updateCount = 0;// 已经上传的文件大小

		InputStream inputStream;
		OutputStream outputStream;

		URL url = new URL(down_url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		// 获取下载文件的size
		totalSize = httpURLConnection.getContentLength();

		if (httpURLConnection.getResponseCode() == 404) {
			throw new Exception("fail!");
			// 这个地方应该加一个下载失败的处理，但是，因为我们在外面加了一个try---catch，已经处理了Exception,
			// 所以不用处理
		}

		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉

		byte buffer[] = new byte[1024];
		int readsize = 0;

		while ((readsize = inputStream.read(buffer)) != -1) {

			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;// 时时获取下载到的大小
			/*** 每次增张3% **/
			if (updateCount == 0
					|| (downloadCount * 100 / totalSize - down_step) >= updateCount) {
				updateCount += down_step;
				// 改变通知栏
				contentView.setTextViewText(R.id.notificationPercent,
						updateCount + "%");
				contentView.setProgressBar(R.id.notificationProgress, 100,
						updateCount, false);
				notification.contentView = contentView;
				notificationManager.notify(R.layout.notification_item,
						notification);
			}
		}
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();

		return downloadCount;
	}

}