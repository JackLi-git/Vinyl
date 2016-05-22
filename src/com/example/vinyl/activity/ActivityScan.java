package com.example.vinyl.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.util.ArrayList;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinyl.R;
import com.example.vinyl.database.DBManager;
import com.example.vinyl.util.Constants;

public class ActivityScan extends Activity {
	boolean thread_flag;
	int degrees = 0;

	Handler handler;
	SQLiteDatabase musicData;
	ArrayList<String> Music_bianhao = new ArrayList<String>();
	ArrayList<String> Music_wenjian = new ArrayList<String>();
	ArrayList<String> Music_gequ = new ArrayList<String>();
	ArrayList<String> Music_geshou = new ArrayList<String>();
	ArrayList<String> Music_lujing = new ArrayList<String>();
	ArrayList<String> Music_geci = new ArrayList<String>();
	ArrayList<String> Music_gecilujing = new ArrayList<String>();
	ArrayList<String> geci = new ArrayList<String>();
	ArrayList<String> gecilujing = new ArrayList<String>();
	String scanPath = "";// 显示扫描到的歌曲;
	int progress = 0;
	int music_number = 0;
	Message msg;
	boolean updateUI_flag = false;
	int musicCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scan_before);
		thread_flag = false;

		// 添加退出监听
		ImageView iv_back = (ImageView) findViewById(R.id.scan_back);
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityScan.this.finish();
			}
		});
		Log.d("scan", "000000");
		
		// 添加全部扫描监听
		LinearLayout ll_scan = (LinearLayout) findViewById(R.id.scan_linearlayout_all);
		ll_scan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setContentView(R.layout.activity_scan_run);	//切换为扫描中布局
				thread_flag = true;	//标识是否在扫描线程中
				Log.d("scan", "11111111111");
				handler = new Handler() {
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						updateUI_flag = true;
						Log.d("scan", "msg max arg2 = " + msg.arg2 + "arg1 = " + msg.arg1);
						
						switch (msg.arg1) {
						case Constants.DATABASE_ERROR:
							Toast.makeText(ActivityScan.this, "数据库错误",Toast.LENGTH_LONG).show();
						case Constants.DATABASE_COMPLETE:
							// 切换view
							Log.d("scan", "case complete000");
							//setContentView(R.layout.activity_scan_after);
//							TextView tv_sum2 = (TextView) findViewById(R.id.scan_textview_musicsum2);
//							tv_sum2.setText(musicCount + "");
							
							Button bt_complete = (Button) findViewById(R.id.scan_tv_canal_or_ok);
							Log.d("scan", "case complete1111");
							bt_complete.setText("完成");
							bt_complete.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									ActivityScan.this.finish();
									Log.d("scan", "case complete222");
									thread_flag = false;
									Log.d("scan", "case complete333");
								}
							});
							break;
						case Constants.PROGRESS_UPDATE:
							Log.d("scan", "case update 00000");
							Log.d("scan", "progress = " + progress);
							ProgressBar pb = (ProgressBar) findViewById(R.id.scan_progressbar_scanning);
							pb.setMax(msg.arg2);
							pb.setProgress(progress);// 显示扫描进度
							
							TextView tv_sum = (TextView) findViewById(R.id.scan_count);
							tv_sum.setText("已扫描到"+ progress + "首歌曲");// 显示扫描到的音乐数量
							
							TextView tv_path = (TextView) findViewById(R.id.scan_textview_musicpath);
							tv_path.setText(scanPath);// 显示扫描路径
							
							break;
						}
						
						updateUI_flag = false;
					}
				};
				
				new Thread() {

					@Override
					public void run() {
						super.run();
						
						String[] muiscInfo = new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, 
								MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.DURATION, 
								MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, 
								MediaStore.Audio.Media.YEAR, MediaStore.Audio.Media.MIME_TYPE, 
								MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DATA};
						
						Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
										muiscInfo,null,null,null);
						DBManager.deleteAllTable();
						int lrcCount = 0;
						while (cursor.moveToNext()) {
							Log.d("scan", "after handle 11111");
							String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
							String file = name.replace(".mp3","");	//这项没有存在的必要
							String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
							String path	= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
							String lyric = null;
							String lpath = null;
							Log.d("scan", "after handle 222222");
							File lyricFile = new File(path.replace(".mp3", ".lrc"));	
							try {	
								//如果存在这个歌词文件
								if (lyricFile.exists()) {
									lrcCount++;
									FileInputStream fileInputStream = new FileInputStream(lyricFile);		
									InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"utf-8");		
									BufferedReader bufferedReader = new BufferedReader(inputStreamReader);		
									String buf = "";		
									while (null != (buf = bufferedReader.readLine())) {		
										if (!TextUtils.isEmpty(buf)) {
											//把该歌词文件路径存进本地数据库
											lyric = name.replace(".mp3",".lrc");
											lpath = path.replace(".mp3",".lrc");
										}					
									}	
								}
													
							} catch (FileNotFoundException e) {		
								e.printStackTrace();	
							} catch (UnsupportedEncodingException e) {	
								e.printStackTrace();		
							} catch (IOException e) {	
								e.printStackTrace();	
							}finally{
//								Log.d("scan", "lrc count = "+lrcCount);

							}
							
							
							//找到一条就添加一条歌曲信息到数据库
							String temp[] = {file,name,singer,path,lyric,lpath };
							
							DBManager.addToMusicTable(temp);
							
							// 设置搜索进度
							scanPath = path;
							progress++;   //更改搜索进度
							musicCount = cursor.getCount();
							msg = new Message();	//每次都必须new，必须发送新对象，不然会报错
					
							msg.arg1 = Constants.PROGRESS_UPDATE;
							msg.arg2 = musicCount;
							
							handler.sendMessage(msg);  //更新UI界面
							Log.d("scan", "after handle 00000");
							updateUI_flag = true;
							while (updateUI_flag) {
								try {
									sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							Log.d("scan", "after handle true");
							
						}
						Log.d("scan", "out of while111111111111111111111111111");
						Log.d("scan", "out of while111111111111111111111111111");
						Log.d("scan", "out of while111111111111111111111111111");
						try {
							Log.d("scan", "out of while222222222222222222222");
							SharedPreferences pref = getSharedPreferences(
									"music", Context.MODE_MULTI_PROCESS);
							SharedPreferences.Editor spEditor = pref.edit();
							spEditor.putInt("id", 1);
							spEditor.commit();
							Log.d("scan", "out of while333333333333333333333333");
							msg = new Message();
							msg.arg1 = Constants.DATABASE_COMPLETE;
							handler.sendMessage(msg);  //更新UI界面
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						
						if (cursor != null) {
							cursor.close();
						}
						
					}
						
					
				}.start();
					
				//取消搜索监听
				Button bt_cancel = (Button) findViewById(R.id.scan_tv_canal_or_ok);
				bt_cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						thread_flag = false;
						ActivityScan.this.finish();
					}
				});
				
			}
		});
	}
}

