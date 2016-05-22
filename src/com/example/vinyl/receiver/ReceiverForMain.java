package com.example.vinyl.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.vinyl.R;
import com.example.vinyl.activity.ActivityMain;
import com.example.vinyl.database.DBManager;
import com.example.vinyl.fragment.FragmentLocalMusic;
import com.example.vinyl.fragment.FragmentModel;
import com.example.vinyl.util.Constants;
import com.example.vinyl.receiver.MediaPlayerManager;


public class ReceiverForMain extends BroadcastReceiver {

	public static int status = Constants.STATUS_STOP;
	public ActivityMain activityMain;
	int duration;
	int current;
	int flag = 0;
	
	public ReceiverForMain(ActivityMain activityMain){
		this.activityMain = activityMain;	//获取主活动引用
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//改变主界面下端的歌手、歌曲名字
		int musicId = activityMain.getShared("id");
		TextView tv_musicName = (TextView)activityMain.findViewById(R.id.music_name);
		TextView tv_singerName = (TextView)activityMain.findViewById(R.id.singer_name);
		String musicName;
		String singerName;
		//只有获取到音乐ID才   更改歌名歌手名
		if (musicId >= 0) {
			musicName = DBManager.getMusicInfo(musicId).get(2);
			singerName = DBManager.getMusicInfo(musicId).get(3);
			tv_musicName.setText(musicName);
			tv_singerName.setText(singerName);	
		}
		
		status = intent.getIntExtra("status",Constants.STATUS_STOP);
		if (status != 3) {
			Log.d("receiver", "status = " + status);
		}
		ImageView iv_play = (ImageView)activityMain.findViewById(R.id.play);
		switch (status) {
		case Constants.STATUS_STOP:
			MediaPlayerManager.status = status;
			iv_play.setImageResource(R.drawable.player_play);
			break;
		case Constants.STATUS_PAUSE:
			MediaPlayerManager.status = status;
			iv_play.setImageResource(R.drawable.player_play);
			break;
		case Constants.STATUS_PLAY:
			Log.d("receiver", "set image play Constants.STATUS_PLAY" );
			MediaPlayerManager.status = status;
			iv_play.setImageResource(R.drawable.player_pause);
			//iv_play.setImageDrawable(activityMain.getResources().getDrawable(R.drawable.player_pause));
			break;
		case Constants.STATUS_RUN:   //歌曲正在线程中播放状态  ??
			if(status!=intent.getIntExtra("status2", Constants.STATUS_STOP))
			{	//Log.d("receiver", "run set image000");
				status = intent.getIntExtra("status2", Constants.STATUS_STOP);
				MediaPlayerManager.status = status;
//				Log.d("receiver", "run status2 = " +status);
//				if(status==Constants.STATUS_PLAY)
//				{Log.d("receiver", "run set image");
//					iv_play.setImageResource(R.drawable.player_pause);//如果是播放状态就设置主界面为暂停图标
//				}
			}
			if (activityMain.seekBar_touch) 
			{
				SeekBar seekBar = (SeekBar) activityMain.findViewById(R.id.seekBar);
				duration = intent.getIntExtra("duration", 0);
				current = intent.getIntExtra("current", 0);
				Log.d("receiver", "duration = "+duration);
				Log.d("receiver", "current = "+current);
				seekBar.setMax(duration);
				seekBar.setProgress(current);    //设置播放进度条
				
			}
		default:
			break;
		}
		
	}

	/*
	public static int status = Constant.STATUS_STOP;   //根据此类中的此播放状态来对音乐播放状态图片进行切换
	MusicActivityMain ma;
	ArrayList<String> musicinfo;
	int updateTime = 0;
	int duration = 0;
	int current = 0;

	public MusicUpdateMain(MusicActivityMain ma) 
	{
		this.ma = ma;   //获取主活动引用
	}

	@Override
	public void onReceive(Context context, Intent intent) 
	{

		int statustemp = intent.getIntExtra("status", -1);
		ImageView iv_play = (ImageView) ma.findViewById(R.id.imageview_play);

		try 
		{	//改变主界面下端的歌手、歌曲名字
			SharedPreferences sp = ma.getSharedPreferences("music",Context.MODE_MULTI_PROCESS);
			int musicid = sp.getInt(Constant.SHARED_ID, -1);
			TextView tv_gequ = (TextView) ma.findViewById(R.id.main_textview_gequ);
			TextView tv_geshou = (TextView) ma.findViewById(R.id.main_textview_geshou);
			tv_gequ.setText(DBUtil.getMusicInfo(musicid).get(1));
			tv_geshou.setText(DBUtil.getMusicInfo(musicid).get(2));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		switch (statustemp)
		{
		case Constant.STATUS_PLAY:
			status = statustemp;
			MusicUpdatePlay.status = statustemp;
			iv_play.setImageResource(R.drawable.player_pause_w);	//如果为播放状态则改变图片资源为“暂停”
			break;
		case Constant.STATUS_STOP:
			try 
			{
				TextView tv_gequ = (TextView) ma.findViewById(R.id.main_textview_gequ);
				TextView tv_geshou = (TextView) ma.findViewById(R.id.main_textview_geshou);
				tv_gequ.setText("百纳音乐");					//如果为停止状态，则显示默认的歌手、歌曲名
				tv_geshou.setText("传播好声音");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		case Constant.STATUS_PAUSE:
			status = statustemp;
			MusicUpdatePlay.status = statustemp;
			iv_play.setImageResource(R.drawable.player_play_w);		//如果为暂停状态则改变图片资源为“播放”
			break;
		case Constant.COMMAND_GO:   //歌曲正在线程中播放状态
			if(status!=intent.getIntExtra("status2", Constant.STATUS_STOP))
			{
				status = intent.getIntExtra("status2", Constant.STATUS_STOP);
				MusicUpdatePlay.status = status;
				if(status==Constant.STATUS_PLAY)
				{
					iv_play.setImageResource(R.drawable.player_pause_w);//如果是播放状态就设置主界面为暂停图标
				}
			}
			if (ma.Seekbar_touch) 
			{
				SeekBar sb = (SeekBar) ma.findViewById(R.id.seekBar1);
				duration = intent.getIntExtra("duration", 0);
				current = intent.getIntExtra("current", 0);
				sb.setMax(duration);
				sb.setProgress(current);    //设置播放进度条
				updateTime++;
				if(updateTime>10)   //????拖动进度条超过10次
				{
					updateTime=0;
					try {
						MusicFragmentLocalmusic.ba.notifyDataSetChanged();  //更新本地音乐适配器
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						MusicFragmentFour.ba.notifyDataSetChanged();//更新其他碎片适配器
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}
	}
	//把传递进来的时间格式化为分秒的形式返回
	public String fromMsToMinuteStr(int ms) 
	{
		ms = ms / 1000;
		int minute = ms / 60;
		int second = ms % 60;
		return minute + ":" + ((second > 9) ? second : "0" + second);
	}*/
}
