package com.example.vinyl.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	//���ݿ���
	private static String DATABASE_NAME = "musicDatabase.db"; 
	//���ݿ�汾��
	private static final int VERSION = 1;
	
	//���ֱ������
	private String createMusicTable = "create table if not exists musictable("
								+ "id integer PRIMARY KEY ,"
								+ "file varchar(100),"
								+ "music varchar(100),"
								+ "singer varchar(50),"
								+ "path varchar(100),"
								+ "lyric varchar(100),"
								+ "lpath varchar(100),"
								+ "mylove integer);";
	
	//����������ʷ��
	private String createLastPlayTable = "create table if not exists lastplay("
									+ "id integer PRIMARY KEY);";
									
	
	//����������ʷ��
	private String createDownloadTable = "create table if not exists download("
									+ "id integer PRIMARY KEY);";	
	
	//�����赥��
	private String createPlaylistTable = "create table if not exists playlist("
									+ "id integer PRIMARY KEY,"
									+ "name varchar(20));";	

	//�����赥������
	private String createListinfoTable = "create table if not exists listinfo("
									+ "id integer,"
									+ "musicid integer,"	
									+ "FOREIGN KEY(id) REFERENCES playlist(id),"
									+ "FOREIGN KEY(musicid) REFERENCES musicdata(id));";

	
	public DatabaseHelper(Context context) {
		// ���ݿ�ʵ�ʱ���������getWritableDatabase()��getReadableDatabase()��������ʱ
		super(context, DATABASE_NAME, null, VERSION);
		// CursorFactory����Ϊnull,ʹ��ϵͳĬ�ϵĹ�����
		
	
	}
	
	/* ����ʱ�䣺���ݿ��һ�δ���ʱonCreate()�����ᱻ����
	 * onCreate������һ�� SQLiteDatabase������Ϊ������������Ҫ�������������ͳ�ʼ������
     * �����������Ҫ��ɴ������ݿ������ݿ�Ĳ���
     * ��������޸��������У�ֻҪ���ݿ��Ѿ����������Ͳ����ٽ������onCreate����
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("main", "creat table 0000");
		db.execSQL(createPlaylistTable);		//�����赥��
		Log.d("main", "creat table 1111");
		db.execSQL(createMusicTable);			//�������ֱ�
		Log.d("main", "creat table 2222");
		db.execSQL(createLastPlayTable);		//����������ʷ��
		db.execSQL(createDownloadTable);		//����������ʷ��
		//db.execSQL(createPlaylistTable);		//�����赥��
		db.execSQL(createListinfoTable);		//�����赥������
		Log.d("main", "creat table success");
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		
	}

		
}
