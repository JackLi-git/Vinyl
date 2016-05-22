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
		
		//注册搜索输入EditText监听事件
		EditText et_search = (EditText)view.findViewById(R.id.et_search);
		et_search.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					EditText et_search = (EditText)view.findViewById(R.id.et_search);
					et_search.setText("");	//获得焦点的时候，设置文本框为空
				}
			}
		});
		
		//设置搜索执行按钮监听事件
		ImageView iv_search =(ImageView)view.findViewById(R.id.iv_search);
		iv_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText et_search = (EditText)view.findViewById(R.id.et_search);
				String inputText = et_search.getText().toString();
				if (!TextUtils.isEmpty(inputText)) {
					//跳转到搜索碎片
					FragmentSearch fragmentSearch = new FragmentSearch();
					FragmentManager manager = getFragmentManager();
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.replace(R.id.framelayout_main,fragmentSearch);
					transaction.addToBackStack(null);
					transaction.commit();
				}
			}
		});
		
		//本地音乐布局监控
		final LinearLayout ll_localMusic = (LinearLayout)view.findViewById(R.id.local_music_linearlayout);
		ll_localMusic.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				TextView tv_lm_name = (TextView)view.findViewById(R.id.local_music);
				TextView tv_lm_count = (TextView)view.findViewById(R.id.local_music_count);
				ImageView iv_fm_play = (ImageView)view.findViewById(R.id.local_music_play);
				//重新获取，原因有待深究
				//LinearLayout ll_localMusic = (LinearLayout)view.findViewById(R.id.local_music_linearlayout);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_lm_name.setTextColor(getResources().getColor(R.color.white));
					tv_lm_count.setTextColor(getResources().getColor(R.color.white));
					iv_fm_play.setBackgroundResource(R.drawable.main_play_l2);
					//此处必须手动设置背景，因为此时子控件ImageView并没有被点击所以背景不会变，
					//这样就只会出现子空间ImageView之外的地方改变布局背景
					ll_localMusic.setBackgroundColor(getResources().getColor(R.color.blue));
					break;
				case MotionEvent.ACTION_UP:
					// 跳转到本地音乐碎片
					FragmentLocalMusic fragmentLocalMusic = new FragmentLocalMusic();
					FragmentManager manager = getFragmentManager();
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.replace(R.id.framelayout_main, fragmentLocalMusic);
					transaction.addToBackStack(null);
					transaction.commit();
					//此处不能break,因为如果break就直接跳到本地音乐碎片，执行不到下一步取消的操作，不能回复背景
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
		
		//本地音乐播放按钮
		ImageView iv_lmPlay = (ImageView)view.findViewById(R.id.local_music_play);
		iv_lmPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//每次点击都是从本地音乐列表中按播放模式选择下一首重新播放			
				SharedPreferences pref = view.getContext().getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
				int musicId = pref.getInt("id",-1);
				if (musicId == -1) {
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd", Constants.COMMAND_STOP);
					getActivity().sendBroadcast(intent);
					Toast.makeText(getActivity(), "歌曲不存在",Toast.LENGTH_LONG).show();
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
					Toast.makeText(getActivity(), "歌曲不存在",Toast.LENGTH_LONG).show();
					return;
				}				
				//获取播放歌曲路径
				String path = DBManager.getMusicPath(musicId);			
				//发送播放请求
				Intent intent = new Intent(Constants.MP_FILTER);
				intent.putExtra("cmd", Constants.COMMAND_PLAY);
				intent.putExtra("path", path);
				getActivity().sendBroadcast(intent);
			}
		});
		
		// 乐库布局监控
		LinearLayout ll_musicLib = (LinearLayout) view.findViewById(R.id.music_library_linearlayout);
		ll_musicLib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 跳转到乐库碎片
//				FragmentMusicLibrary fragmentMusicLibrary = new FragmentMusicLibrary();
//				FragmentManager manager = getFragmentManager();
//				FragmentTransaction transaction = manager.beginTransaction();
//				transaction.replace(R.id.framelayout_main, fragmentMusicLibrary);
//				transaction.addToBackStack(null);
//				transaction.commit();
				//提示此功能尚未开发
			}
		});

		//最近播放布局监控
		final LinearLayout ll_recentPlay = (LinearLayout) view.findViewById(R.id.recent_play_linearlayout);
		ll_recentPlay.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				TextView tv_recent = (TextView)view.findViewById(R.id.recent_play);
				//重新获取，原因有待深究
				//LinearLayout ll_recentPlay = (LinearLayout)view.findViewById(R.id.recent_play_linearlayout);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_recent.setTextColor(getResources().getColor(R.color.white));
					//ll_recentPlay.setBackgroundColor(getResources().getColor(R.color.blue));
					break;
				case MotionEvent.ACTION_UP:
					// 跳转到最近播放碎片---FragmentModel
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
					
					//此处不能break,因为如果break就直接跳到本地音乐碎片，执行不到下一步取消的操作，不能回复背景
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
		
		//下载管理布局监控
		final LinearLayout ll_download = (LinearLayout) view.findViewById(R.id.download_manager_linearlayout);
		ll_download.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				TextView tv_download = (TextView)view.findViewById(R.id.download_manager);
				//重新获取，原因有待深究
				//LinearLayout ll_download = (LinearLayout)view.findViewById(R.id.download_manager_linearlayout);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_download.setTextColor(getResources().getColor(R.color.white));
					//ll_recentPlay.setBackgroundColor(getResources().getColor(R.color.blue));
					break;
				case MotionEvent.ACTION_UP:
					// 跳转到下载管理碎片---FragmentModel
					FragmentModel fragmentDownload = new FragmentModel(Constants.FRAGMENT_DOWNLOAD);
					FragmentManager manager = getFragmentManager();
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.replace(R.id.framelayout_main, fragmentDownload);
					transaction.addToBackStack(null);
					transaction.commit();
					//此处不能break,因为如果break就直接跳到本地音乐碎片，执行不到下一步取消的操作，不能回复背景
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
		
		// 我喜爱布局监控
		final LinearLayout ll_myLove = (LinearLayout) view.findViewById(R.id.my_love_linearlayout);
		ll_myLove.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				TextView tv_myLove = (TextView)view.findViewById(R.id.my_love);
				//重新获取，原因有待深究
				//LinearLayout ll_download = (LinearLayout)view.findViewById(R.id.download_manager_linearlayout);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_myLove.setTextColor(getResources().getColor(R.color.white));
					//ll_recentPlay.setBackgroundColor(getResources().getColor(R.color.blue));
					break;
				case MotionEvent.ACTION_UP:
					// 跳转到我喜爱碎片---FragmentModel
					FragmentModel fragmentMyLove = new FragmentModel(Constants.FRAGMENT_MYLOVE);
					FragmentManager manager = getFragmentManager();
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.replace(R.id.framelayout_main, fragmentMyLove);
					transaction.addToBackStack(null);
					transaction.commit();
					//此处不能break,因为如果break就直接跳到本地音乐碎片，执行不到下一步取消的操作，不能回复背景
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
		
		

		// 我的歌单布局监控  已经修改为一个显示栏，不提供打开功能
		/*
		LinearLayout ll_myPlaylist = (LinearLayout) view.findViewById(R.id.my_playlist_linearlayout);
		ll_myPlaylist.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				TextView tv_myPlaylist = (TextView)view.findViewById(R.id.my_playlist);
				//重新获取，原因有待深究
				//LinearLayout ll_download = (LinearLayout)view.findViewById(R.id.download_manager_linearlayout);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_myPlaylist.setTextColor(getResources().getColor(R.color.white));
					//ll_recentPlay.setBackgroundColor(getResources().getColor(R.color.blue));
					break;
				case MotionEvent.ACTION_UP:
					// 跳转到我的歌单碎片---FragmentModel
					FragmentModel fragmentMylist = new FragmentModel(Constants.FRAGMENT_MYLIST);
					FragmentManager manager = getFragmentManager();
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.replace(R.id.framelayout_main, fragmentMylist);
					transaction.addToBackStack(null);
					transaction.commit();
					//此处不能break,因为如果break就直接跳到本地音乐碎片，执行不到下一步取消的操作，不能回复背景
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
		//新建歌单
		LinearLayout ll_create_playlist = (LinearLayout)view.findViewById(R.id.create_playlist_linearlayout);
		ll_create_playlist.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//创建一个AlertDialog
				final EditText edit = new EditText(getActivity());
				edit.setSingleLine();	//设置单行模式
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(Constants.DIALOG_TITLE);
				builder.setView(edit);
				builder.setPositiveButton(Constants.DIALOG_OK, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						DBManager.createPlayList(edit.getText().toString());	//在数据库中创建一个歌单
						playList = DBManager.getPlayList();		//从数据库中获取歌单
						ListViewUtil.setListViewHeightBasedOnChildren(listView);//listview动态设置空间的高度
						Log.d("main", "playlist.size() = " + playList.size());
						for (int i = 0; i < playList.size(); i++) {
							Log.d("main", playList.get(i)[0]+playList.get(i)[1]);
						}
						baseAdapter.notifyDataSetChanged();		//通知适配器
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
		playList = DBManager.getPlayList();		//从数据库中获取歌单
		//ListViewUtil.setListViewHeightBasedOnChildren(listView);//listview动态设置空间的高度
		setListView();
		ListViewUtil.setListViewHeightBasedOnChildren(listView);//listview动态设置空间的高度
		updatePlayListCount(playList.size());
	}
	
	//设置ListView
	private void setListView() {
		
		baseAdapter = new BaseAdapter() {
			
			//得到一个LayoutInflater对象用来导入布局 
			private LayoutInflater mInflater = LayoutInflater.from(getActivity());	
			
			@Override
			public long getItemId(int position) {		//该方法的返回值将作为列表项的ID
				return 0;
			}
			
			@Override
			public Object getItem(int position) {		//返回第position处的对象
				return null;
			}
			
			@Override
			public int getCount() {						//指定一共包含playList.size()个选项
				if (playList == null) {
					Log.d("FragmentMain", "new BaseAdapter list size = ");
					Log.d("FragmentMain", "new BaseAdapter list size = " +playList.size());
					return 0;
				}else {
					return playList.size();
				}
			}
			//为什么每次position都是定位在0
			public View getView(int position, View convertView, ViewGroup parent) {//该方法返回的View将作为列表框
				/* 优化ListView，如果convertView为空，则加载布局，不为空则直接对convertView进行重用
				 * 新增一个内部类，对控件的实例进行缓存，当convertView为空时，创建对象，将控件实例都存放在viewHolder
				 * 中，不为空时把viewHolder重新取出，这样把所有控件的实例都缓存在ViewHoder中，不用每次都通过
				 * findViewById来获取实例。通过这两种方法可以大大提高ListView的效率。
				 */
				LinearLayout ll_lvItem;
				ViewHolder viewHolder;
				
				if (convertView == null) {
					ll_lvItem = (LinearLayout)mInflater.inflate(R.layout.fragment_listview_item,null);
					viewHolder = new ViewHolder();
					viewHolder.tv_item = (TextView)ll_lvItem.findViewById(R.id.listview_tv);
					ll_lvItem.setTag(viewHolder);//将ViewHolder存储在LinearLayout中
				}else {
					ll_lvItem = (LinearLayout)convertView;
					viewHolder = (ViewHolder)ll_lvItem.getTag();//重新获取ViewHoder
				}
				if (playList.size() > 0) {
					//返回的是一个String[]的集合
					String name = playList.get(position)[1].toString();
					Log.d("main", "get view position = "+position+"name = "+name);
					//viewHolder.tv_item.setText(name + "  共有" + playList.size() + "个歌单");
					viewHolder.tv_item.setText(name);
					for (int i = 0; i < playList.size(); i++) {
						Log.d("main", "play list " +i+ playList.get(i)[0]+playList.get(i)[1]);
					}
				}
//				String name = playList.get(1).toString();
//				viewHolder.tv_item.setText(name + "  共有" + playList.size() + "个歌单");
				//设置当前播放音乐在列表中的文字颜色为黑色
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
				//切换到歌单碎片中去---FragmentModel
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
				//创建一个AlertDialog
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("是否要删除歌单");
				builder.setPositiveButton(Constants.DIALOG_OK, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Log.d("main", "delete play list id = " + listId);
						DBManager.deletePlayList(listId);		//从数据库中删除选中的歌单
						
						playList = DBManager.getPlayList();		//从数据库中获取歌单
						ListViewUtil.setListViewHeightBasedOnChildren(listView);//listview动态设置空间的高度
						Log.d("main", "delete list size = " + playList.size());
						baseAdapter.notifyDataSetChanged();		//通知适配器
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
		tv_myPLCount.setText(count + "个歌单");
	}
	
}


