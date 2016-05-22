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

	//此类辅助桌面widget更新MediaPlayer。为播放、暂停、上一首、下一首的操作。
	Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		Log.d("widgetutil","util 00000000000000000");
		String control = intent.getStringExtra("control");
		Log.d("widgetutil","util 11111111111111111");
		Log.d("widgetutil","control = "+control);
		// 检索是否存在本程序的进程，没有则创建
		if (!ActivityMain.isWorkService(context, Constants.SERVICE_NAME))
		{Log.d("widgetutil","util 222");
			context.startService(new Intent(context, MediaPlayerService.class));
			// 获得 歌曲，歌手
			SharedPreferences pref = context.getSharedPreferences("music",Context.MODE_PRIVATE);
			int musicId = pref.getInt("id", -1);
			int seek = pref.getInt("current", 0);

			// 发送初始化请求？？没有必要
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
			//获取下一首ID
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
				Toast.makeText(context, "歌曲不存在",Toast.LENGTH_SHORT).show();
				return;
			}
			
			//获取播放歌曲路径
			String path = DBManager.getMusicPath(musicId);
			Log.d("widgetutil","next path ="+path);
			//发送播放请求
			Log.d("widgetutil","next  id = "+musicId+"path = "+ path);
			Intent nextIntent = new Intent(Constants.MP_FILTER);
			nextIntent.putExtra("cmd", Constants.COMMAND_PLAY);
			nextIntent.putExtra("path", path);
			context.sendBroadcast(nextIntent);
			
			
			
			
			// 获取下一首歌曲ID
//			int musicId = getShared("id");
//			ArrayList<Integer> musicList = DBManager.getMusicList(getShared("list"));
//			int playMode = getShared("playmode");
//			musicId = DBManager.getNextMusic(musicList, musicId, playMode);
//
//			// 记录歌曲ID
//			setShared("id", musicId);
//
//			// 发送播放请求
//			String path = DBManager.getMusicPath(musicId);
//			Intent nextIntent = new Intent(Constants.MP_FILTER);
//			nextIntent.putExtra("cmd", Constants.COMMAND_PLAY);
//			nextIntent.putExtra("path", path);
//			context.sendBroadcast(nextIntent);
		} else if (control.equals(Constants.WIDGET_PREVIOUS)) {
			Log.d("widgetutil","util 4444");
			//获取当前的播放模式、id、音乐列表
			int playmode = getShared("playmode");	//没有的话默认顺序播放
			int musicId = getShared("id");
			ArrayList<Integer> musicList = DBManager.getMusicList(getShared("list"));//默认查找本地音乐列表
			//获取上一首歌曲的id
			musicId = DBManager.getPreviousMusic(musicList, musicId,playmode);
			setShared("id",musicId);
			if (musicId == -1) 
			{	//列表里面没有歌曲可以播放，同时发送停止播放的命令并弹出歌曲不存在提示
//				Intent intent = new Intent(Constants.MP_FILTER);
//				intent.putExtra("cmd", Constants.COMMAND_STOP);
//				ActivityMain.this.sendBroadcast(intent);
				Toast.makeText(context, "歌曲不存在",Toast.LENGTH_SHORT).show();
				return;
			}
			//获取上一首歌曲的路径
			String path = DBManager.getMusicPath(musicId);
			//发送播放前一首音乐的广播
			Intent prevIntent = new Intent(Constants.MP_FILTER);
			prevIntent.putExtra("cmd", Constants.COMMAND_PLAY);
			prevIntent.putExtra("path",path );
			context.sendBroadcast(prevIntent);
			
			
//			
//			
//			
//			
//			// 获取上一首歌曲ID
//			int musicId = getShared("id");
//			ArrayList<Integer> musicList = DBManager.getMusicList(getShared("list"));
//			int playMode = getShared("playmode");
//			musicId = DBManager.getPreviousMusic(musicList, musicId, playMode);
//
//			// 记录歌曲ID
//			setShared("id", musicId);
//
//			// 发送播放请求
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
