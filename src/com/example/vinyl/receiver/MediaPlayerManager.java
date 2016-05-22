package com.example.vinyl.receiver;

import java.util.ArrayList;

import com.example.vinyl.database.DBManager;
import com.example.vinyl.fragment.FragmentLocalMusic;
import com.example.vinyl.fragment.FragmentModel;
import com.example.vinyl.thread.MusicPlayerThread;
import com.example.vinyl.util.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

//跳转上一曲会默认为这一曲已经播放完成，所以会自动回调播放完成函数
public class MediaPlayerManager extends BroadcastReceiver {
	
	public MediaPlayer mediaPlayer;
	public static int status = Constants.STATUS_STOP;
	public int playMode;
	public int threadNumber;
	Context mContext;
	
	
	public MediaPlayerManager(Context context) {
		super();
		mContext = context;
		//mediaPlayer = new MediaPlayer();
		Log.d("cmd", "create");
		initMediaPlayer();
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		//mediaPlayer = new MediaPlayer();
		//error 不能每次接受一条广播就新建一个MediaPlayer，这样会导致多个MediaPlayer同时播放歌曲
		
		int cmd = intent.getIntExtra("cmd",Constants.COMMAND_INIT);
		Log.d("cmd", "cmd = " + cmd);		//依次为1,2,3,4,5
		switch (cmd) {
		case Constants.COMMAND_INIT:	//已经在创建的时候初始化了，可以撤销了
			NumberRandom();            //改变线程号,使旧的播放线程停止
			String path = intent.getStringExtra("path");
			int current = intent.getIntExtra("current", 0);
//			if(current==0)		//如果进度为0则不需要进行此操作。
//			{
//				return;
//			}
			if(path==null)        //如果拨放路径为空则返回。
			{
				return;
			}
			if(mediaPlayer!=null)
			{
				mediaPlayer.release();        //先释放
			}
			mediaPlayer = new MediaPlayer();  //再新建MediaPlayer
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() 
			{
				//设置播放完成后的操作
				@Override
				public void onCompletion(MediaPlayer mp) 
				{
					NumberRandom();
					onComplete(mediaPlayer);     //调用音乐切换模块，进行相应操作
					UpdateUI();         
				}
			});
			
			try {
//				boolean flag=intent.getBooleanExtra("flag", true);  //获取一个标志位（此标志位的作用？？？？）
//				mediaPlayer.setDataSource(path);                 //设置MediaPlayer数据源
//				mediaPlayer.prepare();                                //MediaPlayer的准备
//				mediaPlayer.start();                                  //开始播放
				mediaPlayer.seekTo(current);                     //跳转进度
//				status = Constants.STATUS_PLAY;               //设置播放状态为播放
				status = Constants.STATUS_PAUSE;               //设置播放状态为暂停
//				if(flag)                                     //如果标志位为真，则歌曲暂停。在进入Activity时起作用。
//				{
//					mp.pause();
//					status = Constant.STATUS_PAUSE;
//				}
//				new MusicPlayerThread(this, context, threadNumber).start();    //创建一个新进程用于播放歌曲
			} catch (Exception e) {
				e.printStackTrace();
				NumberRandom();
			}
			break;
		case Constants.COMMAND_PLAY://播放	
			Log.d("cmd", "COMMAND_PLAY");
			String musicPath = intent.getStringExtra("path");
			Log.d("cmd", "COMMAND_PLAY  path = " + musicPath);
			if (musicPath!=null) {	
				playMusic(musicPath);	//播放一首新歌，调用播放歌曲函数
				Log.d("cmd", "play  music");
			}else {			
				mediaPlayer.start();	//从暂停状态切换到播放状态，直接播放
			}
			status = Constants.STATUS_PLAY;
			break;
		case Constants.COMMAND_STOP://停止
			NumberRandom();
			status = Constants.STATUS_STOP;
			if(mediaPlayer!=null)
			{
				mediaPlayer.release();
			}
			break;
		case Constants.COMMAND_PAUSE: //暂停
			status = Constants.STATUS_PAUSE;
			mediaPlayer.pause();
			break;
		case Constants.COMMAND_PROGRESS://拖动进度
			int cur_progress = intent.getIntExtra("current", 0);
			mediaPlayer.seekTo(cur_progress);
			break;
		}
		UpdateUI();
	}


	private void playMusic(String musicPath) {
		NumberRandom();
		if (mediaPlayer!=null) {
			mediaPlayer.release();
		}
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.d("cmd", "setOnConmplete ");
				Log.d("cmd", "setOnConmplete ");
				Log.d("cmd", "setOnConmplete ");
				NumberRandom();				//切换线程
				onComplete(mediaPlayer);     //调用音乐切换模块，进行相应操作
				UpdateUI(); 				//更新界面
			}
		});
		
		              
		try {
			mediaPlayer.setDataSource(musicPath);   //设置MediaPlayer数据源
			mediaPlayer.prepare();
			mediaPlayer.start(); 
			try {
				FragmentLocalMusic.baseAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				FragmentModel.baseAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
			new MusicPlayerThread(this, mContext, threadNumber).start();
		} catch (Exception e) {
			e.printStackTrace();
		}                                
		                               
		status = Constants.STATUS_PLAY;
		
	}


	private void NumberRandom() {
		int count;
		do {
			count =(int)(Math.random()*100);
		} while (count == threadNumber);	
		threadNumber = count;
	}
	
	private void onComplete(MediaPlayer mediaPlayer) {
		for (int i = 0; i < 50; i++) {
			Log.d("cmd", "OnConmplete "+i);
		}
		SharedPreferences pref = mContext.getSharedPreferences("music",Context.MODE_MULTI_PROCESS);
		int musicId = pref.getInt("id", -1);
		int playMode = pref.getInt("palymode", Constants.PLAYMODE_SEQUENCE);
		int list = pref.getInt("list", Constants.LIST_ALLMUSIC);
		ArrayList<Integer>musicList = DBManager.getMusicList(list);
		if (musicId == -1) {
			return;
		}
		if (musicList.isEmpty()) {
			return;
		}

		String path;
		switch (playMode) {
		case Constants.PLAYMODE_RANDOM:
			musicId = DBManager.getNextMusic(musicList, musicId,Constants.PLAYMODE_RANDOM);
			path = DBManager.getMusicPath(musicId);
			playMusic(path);
		case Constants.PLAYMODE_REPEATALL:
			if (musicId == musicList.get(musicList.size()-1)) {
				musicId = musicList.get(0);
			}else {
				musicId = DBManager.getNextMusic(musicList, musicId,Constants.PLAYMODE_REPEATALL);
			}		
			path = DBManager.getMusicPath(musicId);
			playMusic(path);
		case Constants.PLAYMODE_REPEATSINGLE:
			path = DBManager.getMusicPath(musicId);
			playMusic(path);
		case Constants.PLAYMODE_SEQUENCE:	//
			Log.d("cmd", "onComplete,PLAYMODE_SEQUENCE00000");
			musicId = DBManager.getNextMusic(musicList, musicId,Constants.PLAYMODE_SEQUENCE);
			path = DBManager.getMusicPath(musicId);
			playMusic(path);
			break;

		default:
			break;
		}
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("id",musicId);
		editor.commit();
		UpdateUI();
	}

	private void UpdateUI() {
		Intent intent = new Intent(Constants.UPDATE_MAIN_ACTIVITY);    //接受广播为MusicUpdateMain
		intent.putExtra("status", status);
		mContext.sendBroadcast(intent);                        //发送更新Activity的广播
		
		Intent widgetIntent = new Intent(Constants.WIDGET_STATUS);//发送更新桌面widget的广播
		widgetIntent.putExtra("status", status);
		mContext.sendBroadcast(widgetIntent);
	}
	
	
	private void initMediaPlayer() {
		NumberRandom(); // 改变线程号,使旧的播放线程停止
		SharedPreferences pref = mContext.getSharedPreferences("music",
				Context.MODE_PRIVATE);
		int musicId = pref.getInt("id", -1);
		int current = pref.getInt("current", 0);
		Log.d("cmd", "initMediaPlayer id(1) = " + musicId);
		// 如果是没取到当前正在播放的音乐ID，则从数据库中获取第一首音乐的播放信息初始化
		if (musicId == -1) {
			// 如果表中有音乐的话获得最第一首音乐id，如果表中没数据的话，还是返回-1
			musicId = DBManager.getFirstId(Constants.LIST_ALLMUSIC);
			Log.d("cmd", "initMediaPlayer id(2) = " + musicId);
		}
		Log.d("cmd", "initMediaPlayer id = " + musicId);
		String path = DBManager.getMusicPath(musicId);
		if (path == null) // 如果拨放路径为空则返回。
		{
			return;
		}
		if (current == 0) {
			status = Constants.STATUS_STOP; // 设置播放状态为停止
		}else {
			status = Constants.STATUS_PAUSE; // 设置播放状态为停止
		}
		Log.d("cmd", "initMediaPlayer status = " + status);
		SharedPreferences.Editor spEditor = pref.edit();
		spEditor.putInt("id", musicId);
		spEditor.putString("path", path);
		spEditor.commit();
		UpdateUI();
		
	}
}
