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
	
	/* ���ļ��з��͵�Intent���WidgetUtil����
	 * WidgetUtil���պ󾭹���һ��������Intent��MediaPlayerManager��ʹ������״̬�ı�
	 */
	RemoteViews remoteViews;//���widget��������ã�widgetֻ��ͨ��RemoteView�ı���棬���ܵ�����ø����ؼ�������
	int status;
	
	//��һ��widget������ʱ���� 
	@Override
	public void onEnabled(Context context)
	{
		Log.d("widget", "onEnable");
		status=Constants.STATUS_PAUSE;
	}
	
	/* 
     * ��3������»����OnUpdate()��onUpdate()����main�߳��н��У�������������Ҫ����ʱ�����10�룬����Ӧ��service����ɡ� 
     *��1����ʱ������ʱ���ã�ʱ������widget�����android:updatePeriodMillis�����ã�  
     *��2���û���ק����ҳ��widgetʵ�����ɡ�������û������Configure activity��������Android4.4�Ĳ����У����û���קͼƬ����ҳʱ��
     *	  widgetʵ�����ɣ��ᴥ��onUpdate()��Ȼ������ʾactivity������У�����������˵�Ĳ�һ����������Ϊ���  ������Configure
     *	  acitivity�� �Ͳ�����һ��ʼ����onUpdate()����ʵ����ʾ��ʵ�����ɣ���������������ʱ�ָ����������ȵ���onUpate()���ڱ�����
     *	      ���ڴ�ʱ��preference��δ��������ݣ�����ʵ��ʱ������Ч�����������á� 
     *��3������������ʵ������ҳ����ʾ�����ٴε���onUpdate() 
     */ 
	@Override
	public void onUpdate(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds)
	{
		Log.d("widget", "onUpdate000");
		//��ȡwidget���������
		remoteViews = new RemoteViews(context.getPackageName(),R.layout.music_appwidget);
		
		Intent intent = new Intent(context,ActivityMain.class);
		//����ť��Intent�����ʹ�䷢��
		PendingIntent pendingIntent = PendingIntent.getActivity
				(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//����ť�����¼���  ,���ͼƬ����intent����Activity
		remoteViews.setOnClickPendingIntent(R.id.widget_iv_image, pendingIntent);
		Log.d("widget", "onUpdate111");
		
		Intent playIntent = new Intent(Constants.UPDATE_WIDGET);
		playIntent.putExtra("control", Constants.WIDGET_PLAY);
		playIntent.putExtra("status", status);
		//������Ű�ť�����Ͳ�������
		PendingIntent playPendingIntent = PendingIntent.getBroadcast
				(context, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.widget_iv_play, playPendingIntent);
	
		Log.d("widget", "onUpdate222");
		Intent nextIntent = new Intent(Constants.UPDATE_WIDGET);
		nextIntent.putExtra("control", Constants.WIDGET_NEXT);
		//������һ�װ�ť
		PendingIntent nextPendingIntent = PendingIntent.getBroadcast
				(context, 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.widget_iv_next, nextPendingIntent);
		
		Log.d("widget", "onUpdate333");
		Intent previousIntent = new Intent(Constants.UPDATE_WIDGET);
		previousIntent.putExtra("control", Constants.WIDGET_PREVIOUS);
		//������һ�װ�ť
		PendingIntent proviousPendingIntent = PendingIntent.getBroadcast
				(context, 2, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.widget_iv_pre, proviousPendingIntent);
		Log.d("widget", "onUpdate444");
		//��remoteView����widget��idֵ��һ�������½���
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
		//���ܹ㲥��ˢ������widget
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		 // �൱�ڻ�����б����򴴽���appwidget  
        ComponentName componentName = new ComponentName(context,UpdateWidget.class);  
        //appWidgetManager.updateAppWidget(componentName, remoteViews);
        
		int[] appIds = appWidgetManager.getAppWidgetIds(componentName);
		appWidgetManager.updateAppWidget(appIds, remoteViews);
		Log.d("widget", "onReceive666");
	}
	
	private void setStatus(Context context, Intent intent) //�ı�widget����״̬���Լ���������ͣͼ����л�
	{Log.d("widget", "setStatus000");
		status=intent.getIntExtra("status", Constants.STATUS_STOP);	//��ȡ��ǰ����״̬
		if (status == Constants.STATUS_PLAY) {//�ı�widget��������ͣ��ͼ��
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
