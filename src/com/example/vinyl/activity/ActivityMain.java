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

//����Ҫ�������û�����ӳٶ���(ViewPager)
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

        //�������ݿ�
        dbManager = new DBManager(this);
        
        //��ȡϵͳ�е����������ļ�
        //ScanMP3.getScanFile(this);
        
        //ɨ�������������Ƭ�б�����������
        TextView tv_lm_count = (TextView)findViewById(R.id.local_music_count);
		int musicCount = DBManager.getMusicCount();
		Log.d("main", "come from db music count = " +musicCount);
		tv_lm_count.setText(musicCount+"��");
		
		 //ע��㲥������,���ڸ������UI��״̬
        receiverForMain = new ReceiverForMain(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.UPDATE_MAIN_ACTIVITY);
        registerReceiver(receiverForMain, intentFilter);
        
        //����ó������û������ϵͳ��������������̨�����ҷ��ͳ�ʼ��MediaPlayer������   
        if(!isWorkService(this,Constants.SERVICE_NAME)){
        	Intent intent = new Intent(this,MediaPlayerService.class);
        	startService(intent);
        	Log.d("main", "service work complete ");
        }
        
        //ע��playBar�����¼�
        LinearLayout playBarLL = (LinearLayout)findViewById(R.id.play_bar_linearlayout);
        playBarLL.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//��ת�����Ż
				Intent intent = new Intent(ActivityMain.this,ActivityPlayer.class);
				//startActivity(intent);
			}
		});
        
        //ע��playPrev��ť�����¼�
        ImageView play_prev = (ImageView)findViewById(R.id.pre);
        play_prev.setOnClickListener(new OnClickListener() {
			/* 1.����ǵ�һ�Σ��Ͳ��ű������ֵ�һ�ף��������û�����֣�����ʾ�û�
			 * 2.����Ǹ��б��һ�׾����²��Ŵ˸���
			 * 3.����Ͳ��Ÿ��б���һ��
			 */
        	
			@Override
			public void onClick(View v) {
				//��ȡ��ǰ�Ĳ���ģʽ��id�������б�
				int playmode = getShared("playmode");	//û�еĻ�Ĭ��˳�򲥷�
				int musicId = getShared("id");
				ArrayList<Integer> musicList = DBManager.getMusicList(getShared("list"));//Ĭ�ϲ��ұ��������б�
				
				//��ȡ��һ�׸�����id
				
				musicId = DBManager.getPreviousMusic(musicList, musicId,playmode);
				
				setShared("id",musicId);
				if (musicId == -1) 
				{	//�б�����û�и������Բ��ţ�ͬʱ����ֹͣ���ŵ��������������������ʾ
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_STOP);
					ActivityMain.this.sendBroadcast(intent);
					Toast.makeText(ActivityMain.this, "����������",Toast.LENGTH_LONG).show();
					return;
				}
				//��ȡ��һ�׸�����·��
				String path = DBManager.getMusicPath(musicId);
				Log.d("main","prev  id = "+musicId+"path = "+ path);
				//���Ͳ���ǰһ�����ֵĹ㲥
				Intent intent = new Intent(Constants.MP_FILTER);
				intent.putExtra("cmd", Constants.COMMAND_PLAY);
				intent.putExtra("path",path );
				sendBroadcast(intent);
				
				
			}
		});
        
        //ע��play��ť�����¼�
        ImageView play = (ImageView)findViewById(R.id.play);
        play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int musicId = getShared("id");
				if (musicId == -1) {
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_STOP);
					ActivityMain.this.sendBroadcast(intent);
					Toast.makeText(ActivityMain.this, "����������",Toast.LENGTH_SHORT).show();
					return;
				}
				Log.d("main","play    id = "+musicId); 
				//�����ǰý���ڲ�������״̬����ͼƬ��ʾ��ͣͼƬ�����²��ż���������ͣý�����ͼƬ��ʾ����ͼƬ���Դ����ơ�
				if (receiverForMain.status == Constants.STATUS_PAUSE) {//��ǰΪ����״̬ʱ������ͣ����
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd",Constants.COMMAND_PLAY);
					sendBroadcast(intent);
				}else if (receiverForMain.status == Constants.STATUS_PLAY) {//Ϊ��ͣ״̬ʱ���Ͳ�������
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_PAUSE);
					sendBroadcast(intent);
				}else {										//Ϊֹͣ״̬ʱ���Ͳ�����������ͽ�Ҫ���Ÿ�����·��
					String path = DBManager.getMusicPath(musicId);
					Log.d("main","play    path = "+path); 
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_PLAY);
					intent.putExtra("path", path);
					sendBroadcast(intent);
				}							
			}
		});
        
        //ע��playNext��ť�����¼�
        ImageView play_next = (ImageView)findViewById(R.id.next);
        play_next.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {				
				//��ȡ��һ��ID
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
					Toast.makeText(ActivityMain.this, "����������",Toast.LENGTH_LONG).show();
					return;
				}
				
				//��ȡ���Ÿ���·��
				String path = DBManager.getMusicPath(musicId);
				Log.d("main","next path ="+path);
				//���Ͳ�������
				Log.d("main","next  id = "+musicId+"path = "+ path);
				Intent intent = new Intent(Constants.MP_FILTER);
				intent.putExtra("cmd", Constants.COMMAND_PLAY);
				intent.putExtra("path", path);
				sendBroadcast(intent);
				
//				FragmentLocalMusic.baseAdapter.notifyDataSetChanged();
//				FragmentModel.baseAdapter.notifyDataSetChanged();
				
			}
		});
        
        //ע��seekBar�ļ����¼�
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				seekBar_touch = true;	//�����϶���־
				int musicId = getShared("id");
				if (musicId == -1) 
				{
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_STOP);
					ActivityMain.this.sendBroadcast(intent);
					Toast.makeText(ActivityMain.this, "����������",Toast.LENGTH_LONG).show();
					return;
				}
				
				//���Ͳ�������
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
    
    //�ж�ĳ�������Ƿ���������
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
	
	// ����sharedPreferences
	public void setShared(String key,int value){
		SharedPreferences pref = getSharedPreferences("music",Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	// ��ȡsharedPreferences		
	public int getShared(String key) {
		SharedPreferences pref = getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
		int value = pref.getInt(key, -1);
		return value;
	}
}

