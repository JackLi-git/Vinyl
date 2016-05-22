package com.example.vinyl.database;

import java.util.ArrayList;
import java.util.List;

import com.example.vinyl.util.Constants;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {

	public DatabaseHelper helper;
	public static SQLiteDatabase db;
	
	/* ��ΪgetWritableDatabase�ڲ�������mContext.openOrCreateDatabase(mName, 0,mFactory);
	 * ��Ҫһ��context���� ,����Ҫȷ��context�ѳ�ʼ��,���ǿ��԰�ʵ����DBManager�Ĳ������Activity��onCreate��
	 */ 
	public DBManager(Context context) {
		helper = new DatabaseHelper(context);
		Log.d("main","new  DBManager ");
		db = helper.getWritableDatabase();
		Log.d("main","getWritableDatabase ");
	}
	
	// ��ȡ���ֱ��������
	public static int getMusicCount(){
		int musicCount = 0;
		Cursor cursor = null;
		cursor = db.query("musictable", null, null, null,null, null, null);
		if(cursor.moveToFirst()){
			musicCount = cursor.getCount();
		}
		if (cursor != null) {
			cursor.close();
		}	
		return musicCount;
		
	}

	// ��ȡ���·��
	public static String getLyricPath(int id) {
		if (id == -1) {
			return null;
		}
		String lpath = null;
		Cursor cursor = null;
		cursor = db.query("musictable", null, "id = ?", new String[] { "id" },
				null, null, null);
		if (cursor.moveToFirst()) {
			lpath = cursor.getString(cursor.getColumnIndex("lpath"));
		}
		if (cursor != null) {
			cursor.close();
		}
		return lpath;
	}

	// ��ȡ����·��
	public static String getMusicPath(int id) {
		Log.d("DBManager", "getMusicPath id = "+id);
		if (id == -1) {
			Log.d("DBManager", "getMusicPath return null");
			return null;
		}
		String path = null;	
		Cursor cursor = null;
		//setLastPlay(id); 		�����������
		try {
			cursor = db.query("musictable", null, "id = ?", new String[]{""+id},null, null, null);
			if(cursor.moveToFirst()){
				path = cursor.getString(cursor.getColumnIndex("path"));
				Log.d("DBManager", "getMusicPath path = " + path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (cursor != null) {
				cursor.close();
			}	
		}
		return path;
	}
	
	
	// ��ȡ��һ�׸���
	public static int getPreviousMusic(ArrayList<Integer>musicList,int id, int playMode){
		if (id == -1) {
			return -1;
		}
		Log.d("DBManager", "prev id = "+id+" musiclist size = "+musicList.size());
		for (int i = 0; i < musicList.size(); i++) {
			//Log.d("DBManager", "prev i = "+i+"musicList.get(i) = "+musicList.get(i));
			//�ҵ���ǰid���б�ĵڼ���λ�ã�i+1��
			if (id == musicList.get(i)) {
				if (i==0) {
					//�����ǰ�ǵ�һ��
					//Log.d("DBManager", "prev i = 0 ");
					switch (playMode) {
					case Constants.PLAYMODE_REPEATALL:
						id = musicList.get(musicList.size()-1);
						break;
					case Constants.PLAYMODE_REPEATSINGLE:
						
						break;
					case Constants.PLAYMODE_SEQUENCE:
						
						break;
					case Constants.PLAYMODE_RANDOM:
						id = DBManager.getRandomMusic(musicList, id);
						break;
					}
					return id;	
				}else {
					//Log.d("DBManager", "prev else musicList.get(i) = "+musicList.get(i));
					switch (playMode) {
					case Constants.PLAYMODE_REPEATALL:
						--i;
						break;
					case Constants.PLAYMODE_REPEATSINGLE:
						
						break;
					case Constants.PLAYMODE_SEQUENCE:
						--i;
						break;
					case Constants.PLAYMODE_RANDOM:
						id = DBManager.getRandomMusic(musicList, id);
						break;
					}
					
					return musicList.get(i);
				}
			}
		}
		return -1;
	}
	//û���ǲ���ģʽ-------------�д�����
	// ��ȡ��һ�׸���(id)
	public static int getNextMusic(ArrayList<Integer> musicList, int id,int playMode) {
		if (id == -1) {
			return -1;
		}
		//Log.d("DBManager", "next id = "+id+" musiclist size = "+musicList.size());
		for (int i = 0; i < musicList.size(); i++) {
			//Log.d("DBManager", "next i = "+i+"musicList.get(i) = "+musicList.get(i));
			//�ҵ���ǰid���б�ĵڼ���λ�ã�i+1��
			if (id == musicList.get(i)) {
				if ((i+1) == musicList.size()) {
					// �����ǰ�����һ�ף��򷵻ر���
					//Log.d("DBManager", "next i = 0  musicList.get(musicList.size() - 1) = "+musicList.get(musicList.size() - 1));
					switch (playMode) {
					case Constants.PLAYMODE_REPEATALL:
						id = musicList.get(0);
						break;
					case Constants.PLAYMODE_REPEATSINGLE:
						
						break;
					case Constants.PLAYMODE_SEQUENCE:
						
						break;
					case Constants.PLAYMODE_RANDOM:
						id = DBManager.getRandomMusic(musicList, id);
						break;
					}
					return id;
				} else {
					//Log.d("DBManager", "next else musicList.get(i) = "+musicList.get(i));
					switch (playMode) {
					case Constants.PLAYMODE_REPEATALL:
						++i;
						break;
					case Constants.PLAYMODE_REPEATSINGLE:
						
						break;
					case Constants.PLAYMODE_SEQUENCE:
						++i;
						break;
					case Constants.PLAYMODE_RANDOM:
						id = DBManager.getRandomMusic(musicList, id);
						break;
					}
					return musicList.get(i);
				}
			}
		}
		return -1;
	}
	//��ȡ�������
	public static int getRandomMusic(ArrayList<Integer>list,int id) {
		int musicId = -1;
		if (id == -1) {
			return -1;
		}
		if (list.isEmpty()) {
			return -1;
		}
		if (list.size()==1) {
			return id;
		}
		do{
			int count = (int) (Math.random()*list.size());
			musicId = count;
		}while(musicId == id);
		
		return musicId;
		
	}
	
	// ��ȡ�赥�б�
	public static ArrayList<Integer> getMusicList(int playList){
		Cursor cursor = null;
		ArrayList<Integer>list = new ArrayList<Integer>();
		int musicId = -1;
		Log.d("DBManager", "getMusicList playList = "+playList);
		switch (playList) {
		case Constants.LIST_ALLMUSIC:	
			cursor = db.query("musictable", null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				musicId = cursor.getInt(cursor.getColumnIndex("id"));
				list.add(musicId);
			}
			break;
		case Constants.LIST_DOWNLOAD:	
			cursor = db.query("download", null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				musicId = cursor.getInt(cursor.getColumnIndex("id"));
				list.add(musicId);
			}
			break;
		case Constants.LIST_LASTPLAY:	
			cursor = db.query("lastplay", null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				musicId = cursor.getInt(cursor.getColumnIndex("id"));
				list.add(musicId);
			}
			break;
		case Constants.LIST_MYLOVE:	
			cursor = db.query("musictable", null, "mylove = ?", new String[]{""+1}, null, null, null);
			while (cursor.moveToNext()) {
				musicId = cursor.getInt(cursor.getColumnIndex("id"));
				list.add(musicId);
			}
			break;
		default:
			Log.d("DBManager", "getMusicList default");
			//��ѯ�Լ������ĸ赥�е����и���
			cursor = db.query("listinfo", null, "id = ?", new String[]{""+playList}, null, null, null);
			while (cursor.moveToNext()) {
				musicId = cursor.getInt(cursor.getColumnIndex("musicid"));
				Log.d("DBManager", "getMusicList music id ="+musicId);
				list.add(musicId);
			}
			break;
		}
		if (cursor!=null) {
			cursor.close();
		}
		
		return list;
		
	}
		
	// ��ȡ������ϸ��Ϣ
	public static ArrayList<String> getMusicInfo(int id) {
		if (id == -1) {
			Log.d("DBManager", "getMusicInfo id = -1");
			return null;
		}
		Cursor cursor = null;
		ArrayList<String>musicInfo = new ArrayList<String>();
		cursor = db.query("musictable", null, "id = ?", new String[]{""+id}, null, null, null);
		if (cursor.moveToFirst()) {
//			Log.d("DBManager", "getMusicInfo id = " + cursor.getInt(cursor.getColumnIndex("id")));
//			Log.d("DBManager", "getMusicInfo " + cursor.getString(cursor.getColumnIndex("music")));
//			Log.d("DBManager", "getMusicInfo " + cursor.getString(cursor.getColumnIndex("singer")));
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				musicInfo.add(i,cursor.getString(i));
//				Log.d("DBManager", "getMusicInfo " + cursor.getString(i));
			}
		}else {
			musicInfo.add("0");
			musicInfo.add("�ڽ�����");
			musicInfo.add("0");
			musicInfo.add("0");
			musicInfo.add("0");
			musicInfo.add("0");
			musicInfo.add("0");
			musicInfo.add("0");
		}
		if(cursor!=null){
			cursor.close();
		}
		return musicInfo;	
	}
	
	//�����¸赥
	public static void createPlayList(String listName){
		Cursor cursor = null;
		int listCount = 0;
		int listId = 1;
		try {
			cursor = db.query("playlist", null, null, null, null, null, null);
			Log.d("main", "befor cursor.getCount() = " + cursor.getCount());
			Log.d("main", "befor cursor = " + cursor);
			listCount = cursor.getCount();
//			if(cursor.moveToFirst())
//			{
//				//��������ӵı�IDΪ���һ�����ID+1
//				//listId=cursor.getInt(0)+1;
//				Log.d("main", "cursor move to first" );
//			}
//			if (cursor == null) {
//				Log.d("main", "cursor = null" );
//				Log.d("main", "cursor = null" );
//				Log.d("main", "cursor = null" );
//			}
			
			String sql = "select max(id) from playlist;";
			cursor = db.rawQuery(sql, null); 
			if(cursor.moveToFirst())
			{
				//��������ӵı�IDΪ���һ�����ID+1
				listId=cursor.getInt(0)+1;
			}
			// ���赥���в�������赥
			ContentValues values = new ContentValues();
			values.put("id",listId);
			values.put("name",listName);
			db.insert("playlist",null, values);
			cursor = db.query("playlist", null, null, null, null, null, null);
			Log.d("main", "after cursor.getCount() = " + cursor.getCount());
			listCount = cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor!=null){cursor.close();}
		}
		
		

		
		
		
	}
	
	//��ȡ�赥�б�
	public static ArrayList<String[]> getPlayList(){
		Cursor cursor = null;
		ArrayList<String[]> list=new ArrayList<String[]>();
		try {
			cursor = db.query("playlist", null, null, null, null, null, null);
			Log.d("main", "get play list cursor count = "+cursor.getCount());
			while (cursor.moveToNext()) 
			{
				//�Ѹ赥���е�id��name��ϳ�һ��String����Ž�������
				String []tempStr={cursor.getInt(0)+"",cursor.getString(1)};
				Log.d("main", "cursor "+cursor.getInt(0)+"  " + cursor.getString(1));
				list.add(tempStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor!=null){cursor.close();}
		}
		return list;
	}
	
	//ɾ��ѡ�еĸ赥
	public static void deletePlayList(int listId){
		Cursor cursor = null;
		Log.d("main", "deletePlayList000000");
		try {
			Log.d("main", "deletePlayList try listId = " + listId);
			String id = String.valueOf(listId);
			//String id = Integer.toString(listId);
			db.delete("playlist", "id = ?", new String[]{id});
			ArrayList<String[]>playList = DBManager.getPlayList();		//�����ݿ��л�ȡ�赥
			Log.d("main", "deletePlayList try list.size = " + playList.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor!=null){cursor.close();}
		}
	}
	
	//ɾ�����ݿ������еı�
	public static void deleteAllTable() {
		try {
			db.delete("musictable", null, null);
			db.delete("lastplay", null, null);
			db.delete("download", null, null);
			db.delete("playlist", null, null);
			db.delete("listinfo", null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//��Ӹ��������ֱ�
	public static void addToMusicTable(String [] Info) {
		Cursor cursor = null;
		ContentValues values = null;
		int id = 1;
		try {
			if (Info == null) {
				return;
			}
			if (Info.length == 6) {
				String sql = "select max(id) from musictable;";
				cursor = db.rawQuery(sql, null); 
				if(cursor.moveToFirst())
				{
					//��������ӵ�IDΪ����ID+1
					id=cursor.getInt(0)+1;
				}
				values = new ContentValues();
				values.put("id",id);
				values.put("file",Info[0]);
				values.put("music",Info[1]);
				values.put("singer",Info[2]);
				values.put("path",Info[3]);
				values.put("lyric",Info[4]);
				values.put("lpath",Info[5]);
				values.put("mylove",0);
				db.insert("musictable",null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(cursor!=null){cursor.close();}
		}
		
		
	}
	
	//��ȡ���ֱ��еĵ�һ�����ֵ�ID
	public static int getFirstId(int listNumber){
		ArrayList<Integer>list = new ArrayList<Integer>();
		Cursor cursor = null;
		Cursor cursor2 = null;
		int id = -1;
		int musicId;
		String sql = null;
		try {
			switch (listNumber) {
			case Constants.LIST_ALLMUSIC:
				sql = "select min(id) from musictable;";
				break;
			case Constants.LIST_DOWNLOAD:
				sql = "select min(id) from download;";
				break;
			case Constants.LIST_LASTPLAY:
				sql = "select min(id) from lastplay;";
				break;
			case Constants.LIST_MYLOVE:
				cursor2 = db.query("musictable", null, "mylove = ?", new String[]{""+1}, null, null, null);
				while (cursor2.moveToNext()) {
					musicId = cursor2.getInt(cursor2.getColumnIndex("id"));
					list.add(musicId);
				}
				if (list.size() > 0) {
					id = list.get(0);
				}
				break;

			default:
				cursor = db.query("listinfo", null, "id = ?", new String[]{""+listNumber}, null, null, null);
				while (cursor.moveToNext()) {
					musicId = cursor.getInt(cursor.getColumnIndex("musicid"));
					list.add(musicId);
				}
				if (list.size() > 0) {
					id = list.get(0);
				}
				break;
			}
			if (sql !=null) {
				cursor = db.rawQuery(sql, null);
				if (cursor.moveToFirst()) {
					id = cursor.getInt(0);
					Log.d("DBManager", "getFirstId min id = "+id);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(cursor!=null){
				cursor.close();
			}
			if(cursor2!=null){
				cursor2.close();
			}
		}
		return id;
		
	}
	
	//��Ӹ�����ĳ�赥��
	public static void addToPlayList(int musicId,int listId) {
		Cursor cursor = null;
		ContentValues values = null;
		try {
//			cursor = db.query("playlist", null, "id = ?",new String[]{""+listId}, null, null, null);
//			if (cursor.moveToFirst()) {
//				values = new ContentValues();
//				values.put("musicid", ""+musicId);
//			}
			String sql2="insert into listinfo values("+listId+","+musicId+");";
//			values = new ContentValues();
//			values.put("id", ""+listId);
//			values.put("musicid", ""+musicId);
//			db.insert("listinfo", null, values);
			db.execSQL(sql2);
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			if(cursor!=null){
				cursor.close();
			}
		}
	}
	
	//�����ҵ��
	public static void setMyLove(int musicId){
		Cursor cursor = null;
		ContentValues values;
		try {
			cursor = db.query("musictable", null, "id = ?", new String[]{""+musicId}, null, null, null);
			if(cursor.moveToFirst()) {
				int isLove = cursor.getInt(7);
				int id = cursor.getInt(0);
				String music = cursor.getString(2);
				String singer = cursor.getString(3);
				Log.d("DBManager","befor set my love id = "+id);
				Log.d("DBManager","befor set my love islove = "+isLove);
				Log.d("DBManager","befor set my love music = "+music);
				Log.d("DBManager","befor set my love singer = "+singer);
				if (isLove == 0) {
					values = new ContentValues();
					values.put("mylove", 1);
					db.update("musictable", values, "id = ?",  new String[]{""+musicId});
				}
			}
			
			cursor = db.query("musictable", null, "id = ?", new String[]{""+musicId}, null, null, null);
			if(cursor.moveToFirst()) {
				int isLove = cursor.getInt(7);
				Log.d("DBManager","after set my love islove = "+isLove);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(cursor!=null){
				cursor.close();
			}
		}
	}
	
	//�����б���ɾ��ѡ�е����֣����˸赥����Ϊ�赥����û�и�����
	public static void deleteMusicId(int musicId){
		Cursor cursor = null;
		try {
			cursor = db.query("musictable", null, null, null, null, null, null);
			Log.d("DBManager", "delete music id befor music count = "+cursor.getCount());
			
			db.delete("musictable", "id = ?", new String[]{""+musicId});
			db.delete("lastplay", "id = ?", new String[]{""+musicId});
			db.delete("download", "id = ?", new String[]{""+musicId});
			db.delete("listinfo", "id = ?", new String[]{""+musicId});
			
			cursor = db.query("musictable", null, null, null, null, null, null);
			Log.d("DBManager", "delete music id after music count = "+cursor.getCount());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//��ѡ�еĸ赥��ɾ��ѡ�еĸ���
	public static void deleteMusicInList(int musicId,int listNumber)
	{
		ContentValues values;
		Cursor cursor = null;
		String sql = null;
		Log.d("DBManager", "delete music in list");
		try {
			switch (listNumber) {
			case Constants.LIST_DOWNLOAD:
				db.delete("download", "id = ?", new String[]{""+musicId});
				break;
			case Constants.LIST_LASTPLAY:
				db.delete("lastplay", "id = ?", new String[]{""+musicId});
				break;
			case Constants.LIST_MYLOVE:
				cursor = db.query("musictable", null, "id = ?", new String[]{""+musicId}, null, null, null);
				if(cursor.moveToFirst()) {
					int isLove = cursor.getInt(7);
					if (isLove == 1) {
						values = new ContentValues();
						values.put("mylove", 0);
						db.update("musictable", values, "id = ?",  new String[]{""+musicId});
					}
				}
				break;
			default:
				cursor = db.query("listinfo", null, "id = ?", new String[]{""+listNumber}, null, null, null);
				sql="delete from listinfo where musicid=" + musicId + " and id=" + listNumber + ";";
				db.execSQL(sql);
				break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(cursor!=null){
				cursor.close();
			}
			Log.d("DBManager", "delete music in list11111");
		}
		
	}
	
	//��Ӹ�����������ʷ��
	
	
	//�����������
	//��ȡ�ø����Ƿ����ҵ��
	
	//��ȡ��� ���ڲ�ʵ���е㿴������
	//������������嵥��id��

}
