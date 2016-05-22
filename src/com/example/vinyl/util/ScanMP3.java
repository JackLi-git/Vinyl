package com.example.vinyl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.example.vinyl.database.DBManager;

public class ScanMP3 {


	public static void getScanFile(final Context mContext) {
		
			
				String[] muiscInfo = new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, 
						MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.DURATION, 
						MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, 
						MediaStore.Audio.Media.YEAR, MediaStore.Audio.Media.MIME_TYPE, 
						MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DATA};
				
				Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
								muiscInfo,null,null,null);
				DBManager.deleteAllTable();

				int lrcCount = 0;
				while (cursor.moveToNext()) {
					String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
					String file = name.replace(".mp3","");	//����û�д��ڵı�Ҫ
					String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
					String path	= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
					String lyric = null;
					String lpath = null;
					File lyricFile = new File(path.replace(".mp3", ".lrc"));	
					try {	
						//��������������ļ�
						if (lyricFile.exists()) {
							lrcCount++;
							FileInputStream fileInputStream = new FileInputStream(lyricFile);		
							InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"utf-8");		
							BufferedReader bufferedReader = new BufferedReader(inputStreamReader);		
							String buf = "";		
							while (null != (buf = bufferedReader.readLine())) {		
								if (!TextUtils.isEmpty(buf)) {
									//�Ѹø���ļ�·������������ݿ�
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
//						Log.d("scan", "lrc count = "+lrcCount);

					}
					
					
					//�ҵ�һ�������һ��������Ϣ�����ݿ�
					String temp[] = {file,name,singer,path,lyric,lpath };
					
					DBManager.addToMusicTable(temp);
					int musicCount = DBManager.getMusicCount();
//					Log.d("scan", "come from db music count = " +musicCount);
				
				}
				try {
					//��һ�δ򿪳����ʱ�����ɨ�赽���־���ӵ�һ�����ֵ��������ļ�
					if (cursor.getCount() > 0) {
						SharedPreferences pref = mContext.getSharedPreferences(
								"music", Context.MODE_MULTI_PROCESS);
						SharedPreferences.Editor editor = pref.edit();
						editor.putInt("id", 1);
						editor.commit();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				if (cursor != null) {
					cursor.close();
				}
				
			}
			
//		};
		
		
//	}
		
}
