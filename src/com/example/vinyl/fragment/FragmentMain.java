package com.example.vinyl.fragment;

import java.util.ArrayList;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinyl.R;
import com.example.vinyl.database.DBManager;
import com.example.vinyl.util.Constants;
import com.example.vinyl.util.ListViewUtil;


public class FragmentMain extends Fragment {
	View view;
	ListView listView;
	BaseAdapter baseAdapter;
	ArrayList<String[]> playList;
	TextView tv_myPLCount;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_main, container,false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//ע����������EditText�����¼�
		EditText et_search = (EditText)view.findViewById(R.id.et_search);
		et_search.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					EditText et_search = (EditText)view.findViewById(R.id.et_search);
					et_search.setText("");	//��ý����ʱ�������ı���Ϊ��
				}
			}
		});
		
		//��������ִ�а�ť�����¼�
		ImageView iv_search =(ImageView)view.findViewById(R.id.iv_search);
		iv_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText et_search = (EditText)view.findViewById(R.id.et_search);
				String inputText = et_search.getText().toString();
				if (!TextUtils.isEmpty(inputText)) {
					//��ת��������Ƭ
					FragmentSearch fragmentSearch = new FragmentSearch();
					FragmentManager manager = getFragmentManager();
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.replace(R.id.framelayout_main,fragmentSearch);
					transaction.addToBackStack(null);
					transaction.commit();
				}
			}
		});
		
		//�������ֲ��ּ��
		final LinearLayout ll_localMusic = (LinearLayout)view.findViewById(R.id.local_music_linearlayout);
		ll_localMusic.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				TextView tv_lm_name = (TextView)view.findViewById(R.id.local_music);
				TextView tv_lm_count = (TextView)view.findViewById(R.id.local_music_count);
				ImageView iv_fm_play = (ImageView)view.findViewById(R.id.local_music_play);
				//���»�ȡ��ԭ���д��
				//LinearLayout ll_localMusic = (LinearLayout)view.findViewById(R.id.local_music_linearlayout);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_lm_name.setTextColor(getResources().getColor(R.color.white));
					tv_lm_count.setTextColor(getResources().getColor(R.color.white));
					iv_fm_play.setBackgroundResource(R.drawable.main_play_l2);
					//�˴������ֶ����ñ�������Ϊ��ʱ�ӿؼ�ImageView��û�б�������Ա�������䣬
					//������ֻ������ӿռ�ImageView֮��ĵط��ı䲼�ֱ���
					ll_localMusic.setBackgroundColor(getResources().getColor(R.color.blue));
					break;
				case MotionEvent.ACTION_UP:
					// ��ת������������Ƭ
					FragmentLocalMusic fragmentLocalMusic = new FragmentLocalMusic();
					FragmentManager manager = getFragmentManager();
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.replace(R.id.framelayout_main, fragmentLocalMusic);
					transaction.addToBackStack(null);
					transaction.commit();
					//�˴�����break,��Ϊ���break��ֱ����������������Ƭ��ִ�в�����һ��ȡ���Ĳ��������ܻظ�����
				case MotionEvent.ACTION_CANCEL:
					tv_lm_name.setTextColor(getResources().getColor(R.color.black));
					tv_lm_count.setTextColor(getResources().getColor(R.color.black));
					ll_localMusic.setBackgroundColor(getResources().getColor(R.color.white));
					break;
				default:
					break;
				}
				return false;
			}
		});
		
		//�������ֲ��Ű�ť
		ImageView iv_lmPlay = (ImageView)view.findViewById(R.id.local_music_play);
		iv_lmPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//ÿ�ε�����Ǵӱ��������б��а�����ģʽѡ����һ�����²���			
				SharedPreferences pref = view.getContext().getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
				int musicId = pref.getInt("id",-1);
				if (musicId == -1) {
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_STOP);
					getActivity().sendBroadcast(intent);
					Toast.makeText(getActivity(), "����������",Toast.LENGTH_LONG).show();
					return;
				}
				int playMode = pref.getInt("playmode", Constants.PLAYMODE_RANDOM);
				ArrayList<Integer> playList = DBManager.getMusicList(Constants.LIST_ALLMUSIC);				
				if (playMode == Constants.PLAYMODE_RANDOM) {
					musicId = DBManager.getRandomMusic(playList,musicId);
				}
				else {
					musicId = DBManager.getNextMusic(playList,musicId,playMode);
				}	
				SharedPreferences.Editor editor = view.getContext().getSharedPreferences
						("music", Context.MODE_MULTI_PROCESS).edit();
				editor.putInt("id", musicId);
				editor.commit();
				if (musicId == -1) 
				{
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_STOP);
					getActivity().sendBroadcast(intent);
					Toast.makeText(getActivity(), "����������",Toast.LENGTH_LONG).show();
					return;
				}				
				//��ȡ���Ÿ���·��
				String path = DBManager.getMusicPath(musicId);			
				//���Ͳ�������
				Intent intent = new Intent(Constants.MP_FILTER);
				intent.putExtra("cmd", Constants.COMMAND_PLAY);
				intent.putExtra("path", path);
				getActivity().sendBroadcast(intent);
			}
		});
		
		// �ֿⲼ�ּ��
		LinearLayout ll_musicLib = (LinearLayout) view.findViewById(R.id.music_library_linearlayout);
		ll_musicLib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// ��ת���ֿ���Ƭ
//				FragmentMusicLibrary fragmentMusicLibrary = new FragmentMusicLibrary();
//				FragmentManager manager = getFragmentManager();
//				FragmentTransaction transaction = manager.beginTransaction();
//				transaction.replace(R.id.framelayout_main, fragmentMusicLibrary);
//				transaction.addToBackStack(null);
//				transaction.commit();
				//��ʾ�˹�����δ����
			}
		});

		//������Ų��ּ��
		final LinearLayout ll_recentPlay = (LinearLayout) view.findViewById(R.id.recent_play_linearlayout);
		ll_recentPlay.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				TextView tv_recent = (TextView)view.findViewById(R.id.recent_play);
				//���»�ȡ��ԭ���д��
				//LinearLayout ll_recentPlay = (LinearLayout)view.findViewById(R.id.recent_play_linearlayout);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_recent.setTextColor(getResources().getColor(R.color.white));
					//ll_recentPlay.setBackgroundColor(getResources().getColor(R.color.blue));
					break;
				case MotionEvent.ACTION_UP:
					// ��ת�����������Ƭ---FragmentModel
					Log.d("mian", "mode up");
					FragmentModel fragmentRecentPlay = new FragmentModel(Constants.FRAGMENT_RECENTPLAY);
					FragmentManager manager = getFragmentManager();
					Log.d("mian", "mode up11111");
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.replace(R.id.framelayout_main, fragmentRecentPlay);
					Log.d("mian", "mode up2222");
					transaction.addToBackStack(null);
					transaction.commit();
					Log.d("mian", "mode up3333");
					
					//�˴�����break,��Ϊ���break��ֱ����������������Ƭ��ִ�в�����һ��ȡ���Ĳ��������ܻظ�����
				case MotionEvent.ACTION_CANCEL:
					tv_recent.setTextColor(getResources().getColor(R.color.black));
					//ll_recentPlay.setBackgroundColor(getResources().getColor(R.color.white));
					break;
				default:
					break;
				}
				return false;
			}
		});
		
		//���ع����ּ��
		final LinearLayout ll_download = (LinearLayout) view.findViewById(R.id.download_manager_linearlayout);
		ll_download.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				TextView tv_download = (TextView)view.findViewById(R.id.download_manager);
				//���»�ȡ��ԭ���д��
				//LinearLayout ll_download = (LinearLayout)view.findViewById(R.id.download_manager_linearlayout);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_download.setTextColor(getResources().getColor(R.color.white));
					//ll_recentPlay.setBackgroundColor(getResources().getColor(R.color.blue));
					break;
				case MotionEvent.ACTION_UP:
					// ��ת�����ع�����Ƭ---FragmentModel
					FragmentModel fragmentDownload = new FragmentModel(Constants.FRAGMENT_DOWNLOAD);
					FragmentManager manager = getFragmentManager();
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.replace(R.id.framelayout_main, fragmentDownload);
					transaction.addToBackStack(null);
					transaction.commit();
					//�˴�����break,��Ϊ���break��ֱ����������������Ƭ��ִ�в�����һ��ȡ���Ĳ��������ܻظ�����
				case MotionEvent.ACTION_CANCEL:
					tv_download.setTextColor(getResources().getColor(R.color.black));
					//ll_recentPlay.setBackgroundColor(getResources().getColor(R.color.white));
					break;
				default:
					break;
				}
				return false;
			}
		});
		
		// ��ϲ�����ּ��
		final LinearLayout ll_myLove = (LinearLayout) view.findViewById(R.id.my_love_linearlayout);
		ll_myLove.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				TextView tv_myLove = (TextView)view.findViewById(R.id.my_love);
				//���»�ȡ��ԭ���д��
				//LinearLayout ll_download = (LinearLayout)view.findViewById(R.id.download_manager_linearlayout);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_myLove.setTextColor(getResources().getColor(R.color.white));
					//ll_recentPlay.setBackgroundColor(getResources().getColor(R.color.blue));
					break;
				case MotionEvent.ACTION_UP:
					// ��ת����ϲ����Ƭ---FragmentModel
					FragmentModel fragmentMyLove = new FragmentModel(Constants.FRAGMENT_MYLOVE);
					FragmentManager manager = getFragmentManager();
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.replace(R.id.framelayout_main, fragmentMyLove);
					transaction.addToBackStack(null);
					transaction.commit();
					//�˴�����break,��Ϊ���break��ֱ����������������Ƭ��ִ�в�����һ��ȡ���Ĳ��������ܻظ�����
				case MotionEvent.ACTION_CANCEL:
					tv_myLove.setTextColor(getResources().getColor(R.color.black));
					//ll_recentPlay.setBackgroundColor(getResources().getColor(R.color.white));
					break;
				default:
					break;
				}
				return false;
			}
		});
		
		

		// �ҵĸ赥���ּ��  �Ѿ��޸�Ϊһ����ʾ�������ṩ�򿪹���
		/*
		LinearLayout ll_myPlaylist = (LinearLayout) view.findViewById(R.id.my_playlist_linearlayout);
		ll_myPlaylist.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				TextView tv_myPlaylist = (TextView)view.findViewById(R.id.my_playlist);
				//���»�ȡ��ԭ���д��
				//LinearLayout ll_download = (LinearLayout)view.findViewById(R.id.download_manager_linearlayout);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_myPlaylist.setTextColor(getResources().getColor(R.color.white));
					//ll_recentPlay.setBackgroundColor(getResources().getColor(R.color.blue));
					break;
				case MotionEvent.ACTION_UP:
					// ��ת���ҵĸ赥��Ƭ---FragmentModel
					FragmentModel fragmentMylist = new FragmentModel(Constants.FRAGMENT_MYLIST);
					FragmentManager manager = getFragmentManager();
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.replace(R.id.framelayout_main, fragmentMylist);
					transaction.addToBackStack(null);
					transaction.commit();
					//�˴�����break,��Ϊ���break��ֱ����������������Ƭ��ִ�в�����һ��ȡ���Ĳ��������ܻظ�����
				case MotionEvent.ACTION_CANCEL:
					tv_myPlaylist.setTextColor(getResources().getColor(R.color.black));
					//ll_recentPlay.setBackgroundColor(getResources().getColor(R.color.white));
					break;
				default:
					break;
				}
				return false;
			}
		});
		*/
		//�½��赥
		LinearLayout ll_create_playlist = (LinearLayout)view.findViewById(R.id.create_playlist_linearlayout);
		ll_create_playlist.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//����һ��AlertDialog
				final EditText edit = new EditText(getActivity());
				edit.setSingleLine();	//���õ���ģʽ
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(Constants.DIALOG_TITLE);
				builder.setView(edit);
				builder.setPositiveButton(Constants.DIALOG_OK, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						DBManager.createPlayList(edit.getText().toString());	//�����ݿ��д���һ���赥
						playList = DBManager.getPlayList();		//�����ݿ��л�ȡ�赥
						ListViewUtil.setListViewHeightBasedOnChildren(listView);//listview��̬���ÿռ�ĸ߶�
						Log.d("main", "playlist.size() = " + playList.size());
						for (int i = 0; i < playList.size(); i++) {
							Log.d("main", playList.get(i)[0]+playList.get(i)[1]);
						}
						baseAdapter.notifyDataSetChanged();		//֪ͨ������
						updatePlayListCount(playList.size());
						Log.d("main", "333333");
					}

				});
				builder.setNegativeButton(Constants.DIALOG_CANCEL,new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
				builder.create().show();
			}
		});
		
		tv_myPLCount = (TextView)view.findViewById(R.id.my_playlist_count);
		
	}
	
	

	@Override
	public void onResume() 
	{
		super.onStart();
		playList = DBManager.getPlayList();		//�����ݿ��л�ȡ�赥
		//ListViewUtil.setListViewHeightBasedOnChildren(listView);//listview��̬���ÿռ�ĸ߶�
		setListView();
		ListViewUtil.setListViewHeightBasedOnChildren(listView);//listview��̬���ÿռ�ĸ߶�
		updatePlayListCount(playList.size());
	}
	
	//����ListView
	private void setListView() {
		
		baseAdapter = new BaseAdapter() {
			
			//�õ�һ��LayoutInflater�����������벼�� 
			private LayoutInflater mInflater = LayoutInflater.from(getActivity());	
			
			@Override
			public long getItemId(int position) {		//�÷����ķ���ֵ����Ϊ�б����ID
				return 0;
			}
			
			@Override
			public Object getItem(int position) {		//���ص�position���Ķ���
				return null;
			}
			
			@Override
			public int getCount() {						//ָ��һ������playList.size()��ѡ��
				if (playList == null) {
					Log.d("FragmentMain", "new BaseAdapter list size = ");
					Log.d("FragmentMain", "new BaseAdapter list size = " +playList.size());
					return 0;
				}else {
					return playList.size();
				}
			}
			//Ϊʲôÿ��position���Ƕ�λ��0
			public View getView(int position, View convertView, ViewGroup parent) {//�÷������ص�View����Ϊ�б��
				/* �Ż�ListView�����convertViewΪ�գ�����ز��֣���Ϊ����ֱ�Ӷ�convertView��������
				 * ����һ���ڲ��࣬�Կؼ���ʵ�����л��棬��convertViewΪ��ʱ���������󣬽��ؼ�ʵ���������viewHolder
				 * �У���Ϊ��ʱ��viewHolder����ȡ�������������пؼ���ʵ����������ViewHoder�У�����ÿ�ζ�ͨ��
				 * findViewById����ȡʵ����ͨ�������ַ������Դ�����ListView��Ч�ʡ�
				 */
				LinearLayout ll_lvItem;
				ViewHolder viewHolder;
				
				if (convertView == null) {
					ll_lvItem = (LinearLayout)mInflater.inflate(R.layout.fragment_listview_item,null);
					viewHolder = new ViewHolder();
					viewHolder.tv_item = (TextView)ll_lvItem.findViewById(R.id.listview_tv);
					ll_lvItem.setTag(viewHolder);//��ViewHolder�洢��LinearLayout��
				}else {
					ll_lvItem = (LinearLayout)convertView;
					viewHolder = (ViewHolder)ll_lvItem.getTag();//���»�ȡViewHoder
				}
				if (playList.size() > 0) {
					//���ص���һ��String[]�ļ���
					String name = playList.get(position)[1].toString();
					Log.d("main", "get view position = "+position+"name = "+name);
					//viewHolder.tv_item.setText(name + "  ����" + playList.size() + "���赥");
					viewHolder.tv_item.setText(name);
					for (int i = 0; i < playList.size(); i++) {
						Log.d("main", "play list " +i+ playList.get(i)[0]+playList.get(i)[1]);
					}
				}
//				String name = playList.get(1).toString();
//				viewHolder.tv_item.setText(name + "  ����" + playList.size() + "���赥");
				//���õ�ǰ�����������б��е�������ɫΪ��ɫ
				viewHolder.tv_item.setTextColor(getResources().getColor(R.color.black));
				return ll_lvItem;
			}
			
			class ViewHolder{
				TextView tv_item;
			}
			
		};
		
		listView = (ListView)view.findViewById(R.id.create_playlist_listview);
		listView.setAdapter(baseAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//�л����赥��Ƭ��ȥ---FragmentModel
				FragmentModel fragmentMyList = new FragmentModel(playList.get(position)[1].toString(),Integer.valueOf(playList.get(position)[0].toString()));
				FragmentManager manager = getFragmentManager();
				FragmentTransaction transaction = manager.beginTransaction();
				transaction.replace(R.id.framelayout_main, fragmentMyList);
				transaction.addToBackStack(null);
				transaction.commit();
			}
			
		});
		
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//final int listId = position;
				final int listId=Integer.parseInt(playList.get(position)[0]);
				//����һ��AlertDialog
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("�Ƿ�Ҫɾ���赥");
				builder.setPositiveButton(Constants.DIALOG_OK, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Log.d("main", "delete play list id = " + listId);
						DBManager.deletePlayList(listId);		//�����ݿ���ɾ��ѡ�еĸ赥
						
						playList = DBManager.getPlayList();		//�����ݿ��л�ȡ�赥
						ListViewUtil.setListViewHeightBasedOnChildren(listView);//listview��̬���ÿռ�ĸ߶�
						Log.d("main", "delete list size = " + playList.size());
						baseAdapter.notifyDataSetChanged();		//֪ͨ������
						updatePlayListCount(playList.size());
						Log.d("main", "33333");
					}

				});
				builder.setNegativeButton(Constants.DIALOG_CANCEL,new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
				builder.create().show();
				return false;
			}
		});				
	}
	
	public void updatePlayListCount(int count ){
		tv_myPLCount.setText(count + "���赥");
	}
	
}


