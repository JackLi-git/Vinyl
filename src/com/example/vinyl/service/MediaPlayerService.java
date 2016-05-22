package com.example.vinyl.service;

import com.example.vinyl.receiver.MediaPlayerManager;
import com.example.vinyl.util.Constants;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class MediaPlayerService extends Service {
	
	MediaPlayerManager mediaPlayerManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("musicservice","oncreate");
		//动态注册MediaPlayManager
		mediaPlayerManager = new MediaPlayerManager(this);
		IntentFilter mfilter = new IntentFilter();
		mfilter.addAction(Constants.MP_FILTER);
		registerReceiver(mediaPlayerManager, mfilter);
		Log.d("musicservice","registerReceiver");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//注销MediaPlayManager
		unregisterReceiver(mediaPlayerManager);
		Log.d("musicservice","unregisterReceiver");
	}

	
	//必须实现的方法
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

}
