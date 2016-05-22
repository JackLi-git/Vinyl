package com.example.vinyl.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinyl.R;
import com.example.vinyl.database.DBManager;
import com.example.vinyl.fragment.FragmentLocalMusic;
import com.example.vinyl.fragment.FragmentModel;
import com.example.vinyl.receiver.ReceiverForMain;
import com.example.vinyl.service.MediaPlayerService;
import com.example.vinyl.util.Constants;
import com.example.vinyl.util.ScanMP3;

//还需要改善设置活动启动延迟动画(ViewPager)
public class ActivityMain extends Activity {

	public DBManager dbManager;
	ReceiverForMain receiverForMain;
	int progress_seekBar;
	public boolean seekBar_touch = true;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //创建数据库
        dbManager = new DBManager(this);
        
        //获取系统中的所有音乐文件
        //ScanMP3.getScanFile(this);
        
        //扫描完后设置主碎片中本地音乐数量
        TextView tv_lm_count = (TextView)findViewById(R.id.local_music_count);
		int musicCount = DBManager.getMusicCount();
		Log.d("main", "come from db music count = " +musicCount);
		tv_lm_count.setText(musicCount+"首");
		
		 //注册广播接收器,用于更新主活动UI及状态
        receiverForMain = new ReceiverForMain(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.UPDATE_MAIN_ACTIVITY);
        registerReceiver(receiverForMain, intentFilter);
        
        //如果该程序服务没有正在系统中运行则启动后台服务并且发送初始化MediaPlayer的请求   
        if(!isWorkService(this,Constants.SERVICE_NAME)){
        	Intent intent = new Intent(this,MediaPlayerService.class);
        	startService(intent);
        	Log.d("main", "service work complete ");
        }
        
        //注册playBar监听事件
        LinearLayout playBarLL = (LinearLayout)findViewById(R.id.play_bar_linearlayout);
        playBarLL.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳转到播放活动
				Intent intent = new Intent(ActivityMain.this,ActivityPlayer.class);
				//startActivity(intent);
			}
		});
        
        //注册playPrev按钮监听事件
        ImageView play_prev = (ImageView)findViewById(R.id.pre);
        play_prev.setOnClickListener(new OnClickListener() {
			/* 1.如果是第一次，就播放本地音乐第一首，如果本地没有音乐，则提示用户
			 * 2.如果是该列表第一首就重新播放此歌曲
			 * 3.否则就播放该列表上一首
			 */
        	
			@Override
			public void onClick(View v) {
				//获取当前的播放模式、id、音乐列表
				int playmode = getShared("playmode");	//没有的话默认顺序播放
				int musicId = getShared("id");
				ArrayList<Integer> musicList = DBManager.getMusicList(getShared("list"));//默认查找本地音乐列表
				
				//获取上一首歌曲的id
				
				musicId = DBManager.getPreviousMusic(musicList, musicId,playmode);
				
				setShared("id",musicId);
				if (musicId == -1) 
				{	//列表里面没有歌曲可以播放，同时发送停止播放的命令并弹出歌曲不存在提示
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_STOP);
					ActivityMain.this.sendBroadcast(intent);
					Toast.makeText(ActivityMain.this, "歌曲不存在",Toast.LENGTH_LONG).show();
					return;
				}
				//获取上一首歌曲的路径
				String path = DBManager.getMusicPath(musicId);
				Log.d("main","prev  id = "+musicId+"path = "+ path);
				//发送播放前一首音乐的广播
				Intent intent = new Intent(Constants.MP_FILTER);
				intent.putExtra("cmd", Constants.COMMAND_PLAY);
				intent.putExtra("path",path );
				sendBroadcast(intent);
				
				
			}
		});
        
        //注册play按钮监听事件
        ImageView play = (ImageView)findViewById(R.id.play);
        play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int musicId = getShared("id");
				if (musicId == -1) {
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_STOP);
					ActivityMain.this.sendBroadcast(intent);
					Toast.makeText(ActivityMain.this, "歌曲不存在",Toast.LENGTH_SHORT).show();
					return;
				}
				Log.d("main","play    id = "+musicId); 
				//如果当前媒体在播放音乐状态，则图片显示暂停图片，按下播放键，则发送暂停媒体命令，图片显示播放图片。以此类推。
				if (receiverForMain.status == Constants.STATUS_PAUSE) {//当前为播放状态时发送暂停命令
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd",Constants.COMMAND_PLAY);
					sendBroadcast(intent);
				}else if (receiverForMain.status == Constants.STATUS_PLAY) {//为暂停状态时发送播放命令
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_PAUSE);
					sendBroadcast(intent);
				}else {										//为停止状态时发送播放命令，并发送将要播放歌曲的路径
					String path = DBManager.getMusicPath(musicId);
					Log.d("main","play    path = "+path); 
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_PLAY);
					intent.putExtra("path", path);
					sendBroadcast(intent);
				}							
			}
		});
        
        //注册playNext按钮监听事件
        ImageView play_next = (ImageView)findViewById(R.id.next);
        play_next.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {				
				//获取下一首ID
				int playMode = getShared("playmode");
				Log.d("main","next play mode ="+playMode);
				int musicId=getShared("id");
				ArrayList<Integer> musicList = DBManager.getMusicList(getShared("list"));				
				
				musicId = DBManager.getNextMusic(musicList,musicId,playMode);
								
				setShared("id",musicId);				
				if (musicId == -1) 
				{
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_STOP);
					ActivityMain.this.sendBroadcast(intent);
					Toast.makeText(ActivityMain.this, "歌曲不存在",Toast.LENGTH_LONG).show();
					return;
				}
				
				//获取播放歌曲路径
				String path = DBManager.getMusicPath(musicId);
				Log.d("main","next path ="+path);
				//发送播放请求
				Log.d("main","next  id = "+musicId+"path = "+ path);
				Intent intent = new Intent(Constants.MP_FILTER);
				intent.putExtra("cmd", Constants.COMMAND_PLAY);
				intent.putExtra("path", path);
				sendBroadcast(intent);
				
//				FragmentLocalMusic.baseAdapter.notifyDataSetChanged();
//				FragmentModel.baseAdapter.notifyDataSetChanged();
				
			}
		});
        
        //注册seekBar的监听事件
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				seekBar_touch = true;	//可以拖动标志
				int musicId = getShared("id");
				if (musicId == -1) 
				{
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_STOP);
					ActivityMain.this.sendBroadcast(intent);
					Toast.makeText(ActivityMain.this, "歌曲不存在",Toast.LENGTH_LONG).show();
					return;
				}
				
				//发送播放请求
				Intent intent = new Intent(Constants.MP_FILTER);
				intent.putExtra("cmd", Constants.COMMAND_PROGRESS);
				intent.putExtra("current", progress_seekBar);
				sendBroadcast(intent);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				progress_seekBar = progress;
				if (fromUser) {
					seekBar_touch = false;
				}
			}
		});
  
      
    }
    
    //判断某个服务是否正在运行
	public static boolean isWorkService(Context mContext,String serviceName) {
		boolean isWork = false;
		ActivityManager mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> myList = mActivityManager.getRunningServices(100);
		if (myList.size()<=0) {
			isWork = false;
			return isWork; 
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;	
	}
	
	// 设置sharedPreferences
	public void setShared(String key,int value){
		SharedPreferences pref = getSharedPreferences("music",Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	// 获取sharedPreferences		
	public int getShared(String key) {
		SharedPreferences pref = getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
		int value = pref.getInt(key, -1);
		return value;
	}
}

