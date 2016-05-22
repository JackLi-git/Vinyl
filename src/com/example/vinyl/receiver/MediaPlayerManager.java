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

//��ת��һ����Ĭ��Ϊ��һ���Ѿ�������ɣ����Ի��Զ��ص�������ɺ���
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
		//error ����ÿ�ν���һ���㲥���½�һ��MediaPlayer�������ᵼ�¶��MediaPlayerͬʱ���Ÿ���
		
		int cmd = intent.getIntExtra("cmd",Constants.COMMAND_INIT);
		Log.d("cmd", "cmd = " + cmd);		//����Ϊ1,2,3,4,5
		switch (cmd) {
		case Constants.COMMAND_INIT:	//�Ѿ��ڴ�����ʱ���ʼ���ˣ����Գ�����
			NumberRandom();            //�ı��̺߳�,ʹ�ɵĲ����߳�ֹͣ
			String path = intent.getStringExtra("path");
			int current = intent.getIntExtra("current", 0);
//			if(current==0)		//�������Ϊ0����Ҫ���д˲�����
//			{
//				return;
//			}
			if(path==null)        //�������·��Ϊ���򷵻ء�
			{
				return;
			}
			if(mediaPlayer!=null)
			{
				mediaPlayer.release();        //���ͷ�
			}
			mediaPlayer = new MediaPlayer();  //���½�MediaPlayer
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() 
			{
				//���ò�����ɺ�Ĳ���
				@Override
				public void onCompletion(MediaPlayer mp) 
				{
					NumberRandom();
					onComplete(mediaPlayer);     //���������л�ģ�飬������Ӧ����
					UpdateUI();         
				}
			});
			
			try {
//				boolean flag=intent.getBooleanExtra("flag", true);  //��ȡһ����־λ���˱�־λ�����ã���������
//				mediaPlayer.setDataSource(path);                 //����MediaPlayer����Դ
//				mediaPlayer.prepare();                                //MediaPlayer��׼��
//				mediaPlayer.start();                                  //��ʼ����
				mediaPlayer.seekTo(current);                     //��ת����
//				status = Constants.STATUS_PLAY;               //���ò���״̬Ϊ����
				status = Constants.STATUS_PAUSE;               //���ò���״̬Ϊ��ͣ
//				if(flag)                                     //�����־λΪ�棬�������ͣ���ڽ���Activityʱ�����á�
//				{
//					mp.pause();
//					status = Constant.STATUS_PAUSE;
//				}
//				new MusicPlayerThread(this, context, threadNumber).start();    //����һ���½������ڲ��Ÿ���
			} catch (Exception e) {
				e.printStackTrace();
				NumberRandom();
			}
			break;
		case Constants.COMMAND_PLAY://����	
			Log.d("cmd", "COMMAND_PLAY");
			String musicPath = intent.getStringExtra("path");
			Log.d("cmd", "COMMAND_PLAY  path = " + musicPath);
			if (musicPath!=null) {	
				playMusic(musicPath);	//����һ���¸裬���ò��Ÿ�������
				Log.d("cmd", "play  music");
			}else {			
				mediaPlayer.start();	//����ͣ״̬�л�������״̬��ֱ�Ӳ���
			}
			status = Constants.STATUS_PLAY;
			break;
		case Constants.COMMAND_STOP://ֹͣ
			NumberRandom();
			status = Constants.STATUS_STOP;
			if(mediaPlayer!=null)
			{
				mediaPlayer.release();
			}
			break;
		case Constants.COMMAND_PAUSE: //��ͣ
			status = Constants.STATUS_PAUSE;
			mediaPlayer.pause();
			break;
		case Constants.COMMAND_PROGRESS://�϶�����
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
				NumberRandom();				//�л��߳�
				onComplete(mediaPlayer);     //���������л�ģ�飬������Ӧ����
				UpdateUI(); 				//���½���
			}
		});
		
		              
		try {
			mediaPlayer.setDataSource(musicPath);   //����MediaPlayer����Դ
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
		Intent intent = new Intent(Constants.UPDATE_MAIN_ACTIVITY);    //���ܹ㲥ΪMusicUpdateMain
		intent.putExtra("status", status);
		mContext.sendBroadcast(intent);                        //���͸���Activity�Ĺ㲥
		
		Intent widgetIntent = new Intent(Constants.WIDGET_STATUS);//���͸�������widget�Ĺ㲥
		widgetIntent.putExtra("status", status);
		mContext.sendBroadcast(widgetIntent);
	}
	
	
	private void initMediaPlayer() {
		NumberRandom(); // �ı��̺߳�,ʹ�ɵĲ����߳�ֹͣ
		SharedPreferences pref = mContext.getSharedPreferences("music",
				Context.MODE_PRIVATE);
		int musicId = pref.getInt("id", -1);
		int current = pref.getInt("current", 0);
		Log.d("cmd", "initMediaPlayer id(1) = " + musicId);
		// �����ûȡ����ǰ���ڲ��ŵ�����ID��������ݿ��л�ȡ��һ�����ֵĲ�����Ϣ��ʼ��
		if (musicId == -1) {
			// ������������ֵĻ�������һ������id���������û���ݵĻ������Ƿ���-1
			musicId = DBManager.getFirstId(Constants.LIST_ALLMUSIC);
			Log.d("cmd", "initMediaPlayer id(2) = " + musicId);
		}
		Log.d("cmd", "initMediaPlayer id = " + musicId);
		String path = DBManager.getMusicPath(musicId);
		if (path == null) // �������·��Ϊ���򷵻ء�
		{
			return;
		}
		if (current == 0) {
			status = Constants.STATUS_STOP; // ���ò���״̬Ϊֹͣ
		}else {
			status = Constants.STATUS_PAUSE; // ���ò���״̬Ϊֹͣ
		}
		Log.d("cmd", "initMediaPlayer status = " + status);
		SharedPreferences.Editor spEditor = pref.edit();
		spEditor.putInt("id", musicId);
		spEditor.putString("path", path);
		spEditor.commit();
		UpdateUI();
		
	}
}
