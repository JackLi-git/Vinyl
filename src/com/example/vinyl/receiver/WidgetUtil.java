package com.example.vinyl.receiver;

import java.util.ArrayList;
import java.util.List;

import com.example.vinyl.activity.ActivityMain;
import com.example.vinyl.database.DBManager;
import com.example.vinyl.service.MediaPlayerService;
import com.example.vinyl.util.Constants;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class WidgetUtil extends BroadcastReceiver {

	//���ศ������widget����MediaPlayer��Ϊ���š���ͣ����һ�ס���һ�׵Ĳ�����
	Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		Log.d("widgetutil","util 00000000000000000");
		String control = intent.getStringExtra("control");
		Log.d("widgetutil","util 11111111111111111");
		Log.d("widgetutil","control = "+control);
		// �����Ƿ���ڱ�����Ľ��̣�û���򴴽�
		if (!ActivityMain.isWorkService(context, Constants.SERVICE_NAME))
		{Log.d("widgetutil","util 222");
			context.startService(new Intent(context, MediaPlayerService.class));
			// ��� ����������
			SharedPreferences pref = context.getSharedPreferences("music",Context.MODE_PRIVATE);
			int musicId = pref.getInt("id", -1);
			int seek = pref.getInt("current", 0);

			// ���ͳ�ʼ�����󣿣�û�б�Ҫ
//			Intent intent_start = new Intent(Constants.MP_FILTER);
//			intent_start.putExtra("cmd", Constants.COMMAND_INIT);
//			intent_start.putExtra("flag", false);
//			intent_start.putExtra("path", DBManager.getMusicPath(musicId));
//			intent_start.putExtra("current", seek);
//			context.sendBroadcast(intent_start);
			return;
		}
		if (control.equals(Constants.WIDGET_NEXT)) {
			Log.d("widgetutil","util 3333");
			//��ȡ��һ��ID
			int playMode = getShared("playmode");
			Log.d("widgetutil","next play mode ="+playMode);
			int musicId=getShared("id");
			ArrayList<Integer> musicList = DBManager.getMusicList(getShared("list"));				
			
			musicId = DBManager.getNextMusic(musicList,musicId,playMode);
							
			setShared("id",musicId);				
			if (musicId == -1) 
			{
//				Intent intent = new Intent(Constants.MP_FILTER);
//				intent.putExtra("cmd", Constants.COMMAND_STOP);
//				ActivityMain.this.sendBroadcast(intent);
				Toast.makeText(context, "����������",Toast.LENGTH_SHORT).show();
				return;
			}
			
			//��ȡ���Ÿ���·��
			String path = DBManager.getMusicPath(musicId);
			Log.d("widgetutil","next path ="+path);
			//���Ͳ�������
			Log.d("widgetutil","next  id = "+musicId+"path = "+ path);
			Intent nextIntent = new Intent(Constants.MP_FILTER);
			nextIntent.putExtra("cmd", Constants.COMMAND_PLAY);
			nextIntent.putExtra("path", path);
			context.sendBroadcast(nextIntent);
			
			
			
			
			// ��ȡ��һ�׸���ID
//			int musicId = getShared("id");
//			ArrayList<Integer> musicList = DBManager.getMusicList(getShared("list"));
//			int playMode = getShared("playmode");
//			musicId = DBManager.getNextMusic(musicList, musicId, playMode);
//
//			// ��¼����ID
//			setShared("id", musicId);
//
//			// ���Ͳ�������
//			String path = DBManager.getMusicPath(musicId);
//			Intent nextIntent = new Intent(Constants.MP_FILTER);
//			nextIntent.putExtra("cmd", Constants.COMMAND_PLAY);
//			nextIntent.putExtra("path", path);
//			context.sendBroadcast(nextIntent);
		} else if (control.equals(Constants.WIDGET_PREVIOUS)) {
			Log.d("widgetutil","util 4444");
			//��ȡ��ǰ�Ĳ���ģʽ��id�������б�
			int playmode = getShared("playmode");	//û�еĻ�Ĭ��˳�򲥷�
			int musicId = getShared("id");
			ArrayList<Integer> musicList = DBManager.getMusicList(getShared("list"));//Ĭ�ϲ��ұ��������б�
			//��ȡ��һ�׸�����id
			musicId = DBManager.getPreviousMusic(musicList, musicId,playmode);
			setShared("id",musicId);
			if (musicId == -1) 
			{	//�б�����û�и������Բ��ţ�ͬʱ����ֹͣ���ŵ��������������������ʾ
//				Intent intent = new Intent(Constants.MP_FILTER);
//				intent.putExtra("cmd", Constants.COMMAND_STOP);
//				ActivityMain.this.sendBroadcast(intent);
				Toast.makeText(context, "����������",Toast.LENGTH_SHORT).show();
				return;
			}
			//��ȡ��һ�׸�����·��
			String path = DBManager.getMusicPath(musicId);
			//���Ͳ���ǰһ�����ֵĹ㲥
			Intent prevIntent = new Intent(Constants.MP_FILTER);
			prevIntent.putExtra("cmd", Constants.COMMAND_PLAY);
			prevIntent.putExtra("path",path );
			context.sendBroadcast(prevIntent);
			
			
//			
//			
//			
//			
//			// ��ȡ��һ�׸���ID
//			int musicId = getShared("id");
//			ArrayList<Integer> musicList = DBManager.getMusicList(getShared("list"));
//			int playMode = getShared("playmode");
//			musicId = DBManager.getPreviousMusic(musicList, musicId, playMode);
//
//			// ��¼����ID
//			setShared("id", musicId);
//
//			// ���Ͳ�������
//			String path = DBManager.getMusicPath(musicId);
//			Intent prevIntent = new Intent(Constants.MP_FILTER);
//			prevIntent.putExtra("cmd", Constants.COMMAND_PLAY);
//			prevIntent.putExtra("path", path);
//			context.sendBroadcast(prevIntent);
		} else if (control.equals(Constants.WIDGET_PLAY)) {
			Log.d("widgetutil","util 5555");
			int status = intent.getIntExtra("status", Constants.STATUS_STOP);
			Log.d("widgetutil", "status = "+status);
			if (status == Constants.STATUS_PLAY) {
				Intent intentTemp = new Intent(Constants.MP_FILTER);
				intentTemp.putExtra("cmd", Constants.COMMAND_PAUSE);
				context.sendBroadcast(intentTemp);
			} else if (status == Constants.STATUS_PAUSE) {
				Intent intentTemp = new Intent(Constants.MP_FILTER);
				intentTemp.putExtra("cmd", Constants.COMMAND_PLAY);
				context.sendBroadcast(intentTemp);
			} else {
				Intent intentTemp = new Intent(Constants.MP_FILTER);
				intentTemp.putExtra("cmd", Constants.COMMAND_PLAY);
				intentTemp.putExtra("path",DBManager.getMusicPath(getShared("id")));
				context.sendBroadcast(intentTemp);
			}
		}
	}

	public void setShared(String key, int value) {
		SharedPreferences sp = context.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor spEditor = sp.edit();
		spEditor.putInt(key, value);
		spEditor.commit();
	}

	public int getShared(String key) {
		SharedPreferences sp = context.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		int value = sp.getInt(key, -1);
		return value;
	}

}
