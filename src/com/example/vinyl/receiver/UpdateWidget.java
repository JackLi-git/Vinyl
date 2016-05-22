package com.example.vinyl.receiver;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.vinyl.R;
import com.example.vinyl.activity.ActivityMain;
import com.example.vinyl.database.DBManager;
import com.example.vinyl.util.Constants;

public class UpdateWidget extends AppWidgetProvider {
	
	/* 本文件中发送的Intent命令被WidgetUtil接收
	 * WidgetUtil接收后经过进一步处理发送Intent给MediaPlayerManager，使播放器状态改变
	 */
	RemoteViews remoteViews;//获得widget界面的引用，widget只能通过RemoteView改变界面，不能单独获得各个控件的引用
	int status;
	
	//第一个widget被创建时调用 
	@Override
	public void onEnabled(Context context)
	{
		Log.d("widget", "onEnable");
		status=Constants.STATUS_PAUSE;
	}
	
	/* 
     * 在3种情况下会调用OnUpdate()。onUpdate()是在main线程中进行，因此如果处理需要花费时间多于10秒，处理应在service中完成。 
     *（1）在时间间隔到时调用，时间间隔在widget定义的android:updatePeriodMillis中设置；  
     *（2）用户拖拽到主页，widget实例生成。无论有没有设置Configure activity，我们在Android4.4的测试中，当用户拖拽图片至主页时，
     *	  widget实例生成，会触发onUpdate()，然后再显示activity（如果有）。这点和资料说的不一样，资料认为如果  设置了Configure
     *	  acitivity， 就不会在一开始调用onUpdate()，而实验显示当实例生成（包括创建和重启时恢复），都会先调用onUpate()。在本例，
     *	      由于此时在preference尚未有相关数据，创建实例时不能有效进行数据设置。 
     *（3）机器重启，实例在主页上显示，会再次调用onUpdate() 
     */ 
	@Override
	public void onUpdate(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds)
	{
		Log.d("widget", "onUpdate000");
		//获取widget界面的引用
		remoteViews = new RemoteViews(context.getPackageName(),R.layout.music_appwidget);
		
		Intent intent = new Intent(context,ActivityMain.class);
		//给按钮绑定Intent，点击使其发送
		PendingIntent pendingIntent = PendingIntent.getActivity
				(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//将按钮与点击事件绑定  ,点击图片触发intent进入Activity
		remoteViews.setOnClickPendingIntent(R.id.widget_iv_image, pendingIntent);
		Log.d("widget", "onUpdate111");
		
		Intent playIntent = new Intent(Constants.UPDATE_WIDGET);
		playIntent.putExtra("control", Constants.WIDGET_PLAY);
		playIntent.putExtra("status", status);
		//点击播放按钮。发送播放命令
		PendingIntent playPendingIntent = PendingIntent.getBroadcast
				(context, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.widget_iv_play, playPendingIntent);
	
		Log.d("widget", "onUpdate222");
		Intent nextIntent = new Intent(Constants.UPDATE_WIDGET);
		nextIntent.putExtra("control", Constants.WIDGET_NEXT);
		//设置下一首按钮
		PendingIntent nextPendingIntent = PendingIntent.getBroadcast
				(context, 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.widget_iv_next, nextPendingIntent);
		
		Log.d("widget", "onUpdate333");
		Intent previousIntent = new Intent(Constants.UPDATE_WIDGET);
		previousIntent.putExtra("control", Constants.WIDGET_PREVIOUS);
		//设置上一首按钮
		PendingIntent proviousPendingIntent = PendingIntent.getBroadcast
				(context, 2, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.widget_iv_pre, proviousPendingIntent);
		Log.d("widget", "onUpdate444");
		//将remoteView根据widget的id值，一个个更新界面
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		Log.d("widget", "onUpdate555");
	}
	
	@Override
	public void onReceive(Context context,Intent intent)
	{
		super.onReceive(context, intent);
		Log.d("widget", "onReceive000");
		if(remoteViews==null)
		{Log.d("widget", "onReceive111");
			remoteViews=new RemoteViews(context.getPackageName(),R.layout.music_appwidget);
		}
		if(intent.getAction().equals(Constants.WIDGET_STATUS))
		{Log.d("widget", "onReceive222");
			setStatus(context,intent);
		}
		else if(intent.getAction().equals(Constants.WIDGET_SEEK))
		{Log.d("widget", "onReceive333");
			if(status!=intent.getIntExtra("status", status))
			{Log.d("widget", "onReceive444");
				setStatus(context, intent);
			}
			int duration = intent.getIntExtra("duration", 0);
			int current = intent.getIntExtra("current", 0);
			remoteViews.setProgressBar(R.id.widget_progress, duration, current, false);
		}
		Log.d("widget", "onReceive555");
		//接受广播后，刷新桌面widget
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		 // 相当于获得所有本程序创建的appwidget  
        ComponentName componentName = new ComponentName(context,UpdateWidget.class);  
        //appWidgetManager.updateAppWidget(componentName, remoteViews);
        
		int[] appIds = appWidgetManager.getAppWidgetIds(componentName);
		appWidgetManager.updateAppWidget(appIds, remoteViews);
		Log.d("widget", "onReceive666");
	}
	
	private void setStatus(Context context, Intent intent) //改变widget播放状态，以及播放与暂停图标的切换
	{Log.d("widget", "setStatus000");
		status=intent.getIntExtra("status", Constants.STATUS_STOP);	//获取当前播放状态
		if (status == Constants.STATUS_PLAY) {//改变widget播放与暂停的图标
			Log.d("widget", "setStatus111");
			remoteViews.setImageViewResource(R.id.widget_iv_play, R.drawable.player_pause_w);
		}
		else
		{
			Log.d("widget", "setStatus222");
			remoteViews.setImageViewResource(R.id.widget_iv_play, R.drawable.player_play_w);
		}
		SharedPreferences pref=context.getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
		int musicId = pref.getInt("id", -1);
		if(musicId!=-1)
		{Log.d("widget", "setStatus333");
			remoteViews.setTextViewText(R.id.widget_textview_name, DBManager.getMusicInfo(musicId).get(2));
			remoteViews.setTextViewText(R.id.widget_textview_singer, DBManager.getMusicInfo(musicId).get(3));
		}
		
		Intent playIntent = new Intent(Constants.UPDATE_WIDGET);
		playIntent.putExtra("control", Constants.WIDGET_PLAY);
		playIntent.putExtra("status", status);
		PendingIntent playPending = PendingIntent.getBroadcast
		(context, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.widget_iv_play, playPending);
	}
}
