package com.example.vinyl.thread;

import com.example.vinyl.receiver.MediaPlayerManager;
import com.example.vinyl.util.Constants;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//���߳�ֻ������ѭ�����͹㲥��֪ͨ���ĸ������Ž��ȡ�
public class MusicPlayerThread extends Thread {

	private int threadNumber;
	private Context mContext;
	private MediaPlayerManager mediaPlayerManager;
	int duration;
	int curPosition;
	
	public MusicPlayerThread(MediaPlayerManager mediaPlayerManager,Context context,int threadNumber) {
		this.mediaPlayerManager = mediaPlayerManager;
		this.mContext = context;
		this.threadNumber = threadNumber;
	}

	@Override
	public void run() {
		while (mediaPlayerManager.threadNumber == this.threadNumber) {
			if (mediaPlayerManager == null) {
				break;
			}
			if (mediaPlayerManager.status == Constants.STATUS_STOP) {
				break;
			}
			if (mediaPlayerManager.status == Constants.STATUS_PLAY ||
					mediaPlayerManager.status == Constants.STATUS_PAUSE) {
				duration = mediaPlayerManager.mediaPlayer.getDuration();
				curPosition = mediaPlayerManager.mediaPlayer.getCurrentPosition();
				Log.d("xiancheng", "duration = "+duration);
				Log.d("xiancheng", "current = "+curPosition);
				Intent intent = new Intent(Constants.UPDATE_MAIN_ACTIVITY);
				intent.putExtra("status", Constants.STATUS_RUN);
				Log.d("xiancheng", "status2 = mediaPlayerManager.status = " +mediaPlayerManager.status);
				intent.putExtra("status2", mediaPlayerManager.status);
				intent.putExtra("duration", duration);
				intent.putExtra("current", curPosition);
				mContext.sendBroadcast(intent);//���͸�������Ĺ㲥�����ܹ㲥ΪMusicUpdateMain
				
				//���͸�������widget�㲥
				Intent intentwidget = new Intent(Constants.WIDGET_SEEK);
				intentwidget.putExtra("status",mediaPlayerManager.status);
				intentwidget.putExtra("duration", duration);
				intentwidget.putExtra("current", curPosition);
				mContext.sendBroadcast(intentwidget);
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				
		}
		
	}
}

	
/*
	public void run() 
	{
		while (mum.threadNumber==threadNumber) //���̱߳��û�ı�ʱ�����߳�һֱѭ������
		{
			if (mum.status == Constant.STATUS_STOP||mum.mp==null)
			{
				break;   //����whileѭ��
			}
			int duration = 0;
			int current = 0;
			try 
			{
				if (mum.mp != null && mum.threadNumber==threadNumber
						&& (mum.status == Constant.STATUS_PLAY || mum.status == Constant.STATUS_PAUSE)) {
					duration = mum.mp.getDuration();
					current = mum.mp.getCurrentPosition();
				}

				Intent intent = new Intent(Constant.UPDATE_STATUS);
				intent.putExtra("status",Constant.COMMAND_GO);  //���ڲ����߳��в���
				intent.putExtra("status2",mum.status);
				intent.putExtra("duration", duration);
				intent.putExtra("current", current);
				context.sendBroadcast(intent);			//���͸�������Ĺ㲥�����ܹ㲥ΪMusicUpdateMain
				
				Intent intentwidget = new Intent(Constant.WIDGET_SEEK);
				intentwidget.putExtra("status",mum.status);
				intentwidget.putExtra("duration", duration);
				intentwidget.putExtra("current", current);
				context.sendBroadcast(intentwidget);	//���ܹ㲥ΪMusicUpdateWidget
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			try 
			{
				Thread.sleep(100);       //����0.1��
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
*/
	

