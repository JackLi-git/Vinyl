package com.example.vinyl.util;

import com.example.vinyl.receiver.WidgetUtil;


public class Constants {
	//service名
	public static final String SERVICE_NAME = "com.example.vinyl.service.MediaPlayerService";//服务的名称为包名+类名
	//播放状态
	public static final int STATUS_STOP = 0; //停止状态
	public static final int STATUS_PLAY = 1; //播放状态
	public static final int STATUS_PAUSE = 2; //暂停状态
	public static final int STATUS_RUN = 3;  //   状态
	
	public static final int COMMAND_INIT = 1; //初始化命令
	public static final int COMMAND_PLAY = 2; //播放命令
	public static final int COMMAND_PAUSE = 3; //暂停命令
	public static final int COMMAND_STOP = 4; //停止命令
	public static final int COMMAND_PROGRESS = 5; //改变进度命令
	
	//播放模式
	public static final int PLAYMODE_SEQUENCE = -1;
	public static final int PLAYMODE_REPEATALL = 10;
	public static final int PLAYMODE_REPEATSINGLE = 11;
	public static final int PLAYMODE_RANDOM = 12;
	
	public static final String PLAYMODE_SEQUENCE_TEXT = "顺序播放";
	public static final String PLAYMODE_REPEATALL_TEXT = "循环播放";
	public static final String PLAYMODE_REPEATSINGLE_TEXT = "单曲循环";
	public static final String PLAYMODE_RANDOM_TEXT = "随机播放";
	
	//歌曲列表常量
	public static final int LIST_ALLMUSIC = -1;
	public static final int LIST_MYLOVE = 10000;
	public static final int LIST_LASTPLAY = 10001;
	public static final int LIST_DOWNLOAD = 10002;
	
	//fragment_title
	public static final String FRAGMENT_MYLOVE = "我喜爱";
	public static final String FRAGMENT_DOWNLOAD = "下载管理";
	public static final String FRAGMENT_MYLIST = "我的歌单";
	public static final String FRAGMENT_RECENTPLAY = "最近播放";
	
	//AlertDialog 
	public static final String DIALOG_TITLE = "创建歌单";
	public static final String DIALOG_OK = "确定";
	public static final String DIALOG_CANCEL = "取消";
	
	//handle常量
	public static final int DATABASE_ERROR = 0;
	public static final int DATABASE_COMPLETE = 1;
	public static final int PROGRESS_UPDATE = 2;
	public static final int PATH_UPDATE = 3;
//	public static final int LOAD_COMPLETE = 4;
//	public static final int LOAD_PREPARE = 5;
//	public static final int LOAD_ERROR = 6;
//	public static final int DOWNLOAD_UPDATE = 14;
	
	//ReceiverForMain.action
	public static final String UPDATE_MAIN_ACTIVITY ="MainActivityToReceiver.action";
	//MediaPlayerManager.action
	public static final String MP_FILTER = "com.example.vinyl.start_mediaplayer";
	//WidgetUtil.action
	public static final String UPDATE_WIDGET = "android.intent.ACTION_WIDGET";
	//UpdateWidget.action
	public static final String WIDGET_STATUS = "android.appwidget.action.WIDGET_STATUS";
	public static final String WIDGET_SEEK = "android.appwidget.action.WIDGET_SEEK";
	//
	public static final String MUSIC_CONTROL = "kugoumusic.ACTION_CONTROL";
	public static final String UPDATE_STATUS = "kugoumusic.ACTION_STATUS";
	
	//widget播放控制
	public static final String WIDGET_PLAY="android.appwidget.WIDGET_PLAY";
	public static final String WIDGET_NEXT="android.appwidget.WIDGET_NEXT";
	public static final String WIDGET_PREVIOUS="android.appwidget.WIDGET_PREVIOUS";
}
