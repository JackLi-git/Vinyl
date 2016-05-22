package com.example.vinyl.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	//数据库名
	private static String DATABASE_NAME = "musicDatabase.db"; 
	//数据库版本号
	private static final int VERSION = 1;
	
	//音乐表建表语句
	private String createMusicTable = "create table if not exists musictable("
								+ "id integer PRIMARY KEY ,"
								+ "file varchar(100),"
								+ "music varchar(100),"
								+ "singer varchar(50),"
								+ "path varchar(100),"
								+ "lyric varchar(100),"
								+ "lpath varchar(100),"
								+ "mylove integer);";
	
	//创建播放历史表
	private String createLastPlayTable = "create table if not exists lastplay("
									+ "id integer PRIMARY KEY);";
									
	
	//创建下载历史表
	private String createDownloadTable = "create table if not exists download("
									+ "id integer PRIMARY KEY);";	
	
	//创建歌单表
	private String createPlaylistTable = "create table if not exists playlist("
									+ "id integer PRIMARY KEY,"
									+ "name varchar(20));";	

	//创建歌单歌曲表
	private String createListinfoTable = "create table if not exists listinfo("
									+ "id integer,"
									+ "musicid integer,"	
									+ "FOREIGN KEY(id) REFERENCES playlist(id),"
									+ "FOREIGN KEY(musicid) REFERENCES musicdata(id));";

	
	public DatabaseHelper(Context context) {
		// 数据库实际被创建是在getWritableDatabase()或getReadableDatabase()方法调用时
		super(context, DATABASE_NAME, null, VERSION);
		// CursorFactory设置为null,使用系统默认的工厂类
		
	
	}
	
	/* 调用时间：数据库第一次创建时onCreate()方法会被调用
	 * onCreate方法有一个 SQLiteDatabase对象作为参数，根据需要对这个对象填充表和初始化数据
     * 这个方法中主要完成创建数据库后对数据库的操作
     * 即便程序修改重新运行，只要数据库已经创建过，就不会再进入这个onCreate方法
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("main", "creat table 0000");
		db.execSQL(createPlaylistTable);		//创建歌单表
		Log.d("main", "creat table 1111");
		db.execSQL(createMusicTable);			//创建音乐表
		Log.d("main", "creat table 2222");
		db.execSQL(createLastPlayTable);		//创建播放历史表
		db.execSQL(createDownloadTable);		//创建下载历史表
		//db.execSQL(createPlaylistTable);		//创建歌单表
		db.execSQL(createListinfoTable);		//创建歌单歌曲表
		Log.d("main", "creat table success");
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		
	}

		
}
