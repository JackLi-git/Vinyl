package com.example.vinyl.fragment;

import java.util.ArrayList;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Loader.ForceLoadContentObserver;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinyl.R;
import com.example.vinyl.activity.ActivityScan;
import com.example.vinyl.database.DBManager;
import com.example.vinyl.receiver.ReceiverForMain;
import com.example.vinyl.util.Constants;

public class FragmentLocalMusic extends Fragment {

	View view;
	public static BaseAdapter baseAdapter;
	ReceiverForMain receiverForMain;
	ArrayList<Integer> musicList;
	ArrayList<String[]> playList;
	ListView listView;
	ListView addListView;
	Context mContext = getActivity();

	private PopupWindow mPopWindow;
	private PopupWindow addPopWindow;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_local_music, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// 返回按钮，返回上一个碎片(主碎片)
		ImageView ll_back = (ImageView) view.findViewById(R.id.local_back);
		ll_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.popBackStack();
			}
		});

		// 切换到搜索碎片
		ImageView iv_search = (ImageView) view.findViewById(R.id.local_search);
		iv_search.setOnClickListener(new OnClickListener() {

			// 网络搜索功能还未开发，不作响应
			@Override
			public void onClick(View v) {
				// FragmentSearch fragmentSearch = new FragmentSearch();
				// FragmentManager fragmentManager = getFragmentManager();
				// FragmentTransaction transaction =
				// fragmentManager.beginTransaction();
				// transaction.replace(R.layout.fragment_model, fragmentSearch);
				// transaction.commit();
				// transaction.addToBackStack(null);
			}
		});

		// 切换到全盘扫描活动
		ImageView iv_scan = (ImageView) view.findViewById(R.id.scan);
		iv_scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityScan activityScan = new ActivityScan();
				Intent intent = new Intent(getActivity(), activityScan
						.getClass());
				startActivity(intent);

			}
		});

		// 播放模式监听
		LinearLayout ll_playMode = (LinearLayout) view.findViewById(R.id.playmode_linearlayout);
		final ImageView iv_playMode = (ImageView) view.findViewById(R.id.imageview_playmode);
		final TextView tv_playMode = (TextView) view.findViewById(R.id.textview_playmode);
		ll_playMode.setOnClickListener(new OnClickListener() {
			// sequence-->repeatall-->repeatsingle-->random-->sequence
			@Override
			public void onClick(View v) {
				// 获取当前的播放模式
				int playMode = getShared("playmode");
				switch (playMode) {
				case Constants.PLAYMODE_SEQUENCE:
					iv_playMode.setImageResource(R.drawable.repeat_all);
					tv_playMode.setText(Constants.PLAYMODE_REPEATALL_TEXT);
					setShared("playmode", Constants.PLAYMODE_REPEATALL);
					break;
				case Constants.PLAYMODE_REPEATALL:
					iv_playMode.setImageResource(R.drawable.repeat_single);
					tv_playMode.setText(Constants.PLAYMODE_REPEATSINGLE_TEXT);
					setShared("playmode", Constants.PLAYMODE_REPEATSINGLE);
					break;
				case Constants.PLAYMODE_REPEATSINGLE:
					iv_playMode.setImageResource(R.drawable.random);
					tv_playMode.setText(Constants.PLAYMODE_RANDOM_TEXT);
					setShared("playmode", Constants.PLAYMODE_RANDOM);
					break;
				case Constants.PLAYMODE_RANDOM:
					iv_playMode.setImageResource(R.drawable.sequence);
					tv_playMode.setText(Constants.PLAYMODE_SEQUENCE_TEXT);
					setShared("playmode", Constants.PLAYMODE_SEQUENCE);
					break;
				default:
					break;
				}
			}
		});


	}

	@Override
	public void onResume() {
		super.onStart();
		Log.d("local", "onResume");
		musicList = DBManager.getMusicList(Constants.LIST_ALLMUSIC); // 获取本地音乐列表
		TextView tv_musicCount =(TextView)getActivity().findViewById(R.id.local_music_count);
		tv_musicCount.setText(musicList.size()+"首");
		setShared("list",Constants.LIST_ALLMUSIC);
//		TextView tv_musicCount = (TextView) view
//				.findViewById(R.id.local_music_fragment_count);
//		if (musicList == null) {
//			tv_musicCount.setText("共0首歌曲");
//			Log.d("local", "musicList == null 共0首歌曲");
//		} else {
//			// 显示歌曲数量，由于布局高度不够显示不出来，有待改进
//			tv_musicCount.setText("共" + musicList.size() + "首歌曲");
//			Log.d("local", "musicList 共" + musicList.size() + "首歌曲");
//		}
		setListView();
	}

	// 设置ListView
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
			public int getCount() {						//指定一共包含musicList.size()个选项
				if (musicList == null) {
					return 0;
				}else {
					return musicList.size();
				}
			}
			
			//该方法返回的View将作为列表框
			@SuppressLint("InflateParams")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				/* 优化ListView，如果convertView为空，则加载布局，不为空则直接对convertView进行重用
				 * 新增一个内部类，对控件的实例进行缓存，当convertView为空时，创建对象，将控件实例都存放在viewHolder
				 * 中，不为空时把viewHolder重新取出，这样把所有控件的实例都缓存在ViewHoder中，不用每次都通过
				 * findViewById来获取实例。通过这两种方法可以大大提高ListView的效率。
				 */
				/* 此为优化版，但是由于解决不了处理textview设置字体颜色问题弃用
				LinearLayout ll_lvItem;
				ViewHolder viewHolder;
				
				if (convertView == null) {
					Log.d("local", "convertView==null,position = " + position);
					ll_lvItem = (LinearLayout)mInflater.inflate(R.layout.fragment_listview_item,null);
					viewHolder = new ViewHolder();
					viewHolder.tv_item = (TextView)ll_lvItem.findViewById(R.id.listview_tv);
					ll_lvItem.setTag(viewHolder);//将ViewHolder存储在LinearLayout中
				}else {
					Log.d("local", "convertView!=null,position = " + position);
					ll_lvItem = (LinearLayout)convertView;
					viewHolder = (ViewHolder)ll_lvItem.getTag();//重新获取ViewHoder
				}
				
				int curId = 0;
				if (musicList.size() > 0) {
					curId = musicList.get(position);
					String name = DBManager.getMusicInfo(curId).get(2);
					String singer = DBManager.getMusicInfo(curId).get(3);
					Log.d("local", "position = " + position);
					Log.d("local", "curId = " + curId);
					viewHolder.tv_item.setText(name+"-"+singer);
					//设置当前播放音乐在列表中的文字颜色为蓝色
					int musicId = getShared("id");
					if (curId == musicId) {
						Log.d("local","getview set blue musicId = "+musicId);
						viewHolder.tv_item.setTextColor(getResources().getColor(R.color.blue));
					}
				}
				
				return ll_lvItem;
			}

			class ViewHolder{
				TextView tv_item;
			}
			*/
				musicList = DBManager.getMusicList(Constants.LIST_ALLMUSIC); // 获取本地音乐列表
				TextView tv_musicCount =(TextView)getActivity().findViewById(R.id.local_music_count);
				tv_musicCount.setText(musicList.size()+"首");
				
				LinearLayout ll_lvItem;
				ll_lvItem = (LinearLayout) mInflater.inflate(R.layout.fragment_listview_item, null);
				TextView tv_item = (TextView) ll_lvItem.findViewById(R.id.listview_tv);

				int curId = 0;
				if (musicList.size() > 0) {
					curId = musicList.get(position);
					String name = DBManager.getMusicInfo(curId).get(2);
					String singer = DBManager.getMusicInfo(curId).get(3);
					Log.d("local", "position = " + position);
					Log.d("local", "curId = " + curId);
					tv_item.setText(name + "-" + singer);
					// 设置当前播放音乐在列表中的文字颜色为蓝色
					int musicId = getShared("id");
					if (curId == musicId) {
						Log.d("local", "getview set blue musicId = " + musicId);
						tv_item.setTextColor(getResources().getColor(R.color.blue));
					}
				}

				return ll_lvItem;
			}
		};	
		
		listView = (ListView)view.findViewById(R.id.localmusic_listview);
		listView.setAdapter(baseAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 获取所点歌曲的ID;
				int musicId = musicList.get(position);
				int playingId = getShared("id");
				
				Log.d("local", "item id = " + musicId);
				if (playingId == -1 || playingId != musicId) {
					//当前没有播放歌曲并且点击的不是正在播放的给其则播放所选歌曲
					//主活动下端的bar显示所选歌曲信息
					ArrayList<String>musciInfo = DBManager.getMusicInfo(musicId);
					TextView tv_name = (TextView)getActivity().findViewById(R.id.music_name);
					TextView tv_singer = (TextView)getActivity().findViewById(R.id.singer_name);
					tv_name.setText(musciInfo.get(2));
					tv_singer.setText(musciInfo.get(3));
					
					//发送播放音乐的广播
					String path = DBManager.getMusicPath(musicId);
					Log.d("local", "item path = " + path);
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd",Constants.COMMAND_PLAY);
					intent.putExtra("path",path);
					getActivity().sendBroadcast(intent);
					setShared("id",musicId);
					
					//通知适配器更新listview
					baseAdapter.notifyDataSetChanged();
					
				}else {
					//保持原来的状态,不做任何响应
				}
						
			}
			
		});
		
		
		
		//持久点击歌曲则由下而上滑出一个选项卡----添加（到歌单）、我喜欢、删除
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
		

			@SuppressLint("InflateParams") 
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				//设置contentView  
			    View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_local_music_menu, null);  
			    mPopWindow = new PopupWindow(contentView,  
			            LayoutParams.MATCH_PARENT,300, true); 
			  
			    mPopWindow.setAnimationStyle(R.style.contextMenuAnim);
			    
			    //设置外部是否可以点击，即点击PopupWindow以外的区域，PopupWindow是否会消失。
				//setBackgroundDrawable这行必须加，加了setOutsideTouchable才会起作用，返回退出popwindow才会有效果
			    mPopWindow.setBackgroundDrawable(new ColorDrawable(0));
			    mPopWindow.setOutsideTouchable(true);
				//获取焦点
			    mPopWindow.setFocusable(true);
			    //显示PopupWindow  父布局的底端
			    mPopWindow.showAtLocation(view,Gravity.BOTTOM, 0, 0);

				//设置添加到歌单监听
				LinearLayout ll_add =(LinearLayout)mPopWindow.getContentView().findViewById(R.id.local_menu_add_ll);
				ll_add.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
				
						Log.d("local", "add");
						
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							//ll_add.setBackgroundColor(getResources().getColor(R.color.blue));
							break;
						case MotionEvent.ACTION_UP:
							//滑出一个界面
							//设置contentView  
						    View add_contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_local_music_menu_add, null);  
						    addPopWindow = new PopupWindow(add_contentView,  LayoutParams.MATCH_PARENT,800, true); 
						  
						    addPopWindow.setAnimationStyle(R.style.contextMenuAnim);
						    
						    //设置外部是否可以点击，即点击PopupWindow以外的区域，PopupWindow是否会消失。
							//setBackgroundDrawable这行必须加，加了setOutsideTouchable才会起作用，返回退出popwindow才会有效果
						    addPopWindow.setBackgroundDrawable(new ColorDrawable(0));
						    addPopWindow.setOutsideTouchable(true);
							//获取焦点
						    addPopWindow.setFocusable(true);
						    playList = DBManager.getPlayList();
						    final int curId = musicList.get(position);
						   // setAddListView(curId);
						    
						    //显示PopupWindow  父布局的底端
						    addPopWindow.showAtLocation(getView(),Gravity.BOTTOM, 0, 0);
						    
							BaseAdapter addBaseAdapter = new BaseAdapter() {

								// 得到一个LayoutInflater对象用来导入布局
								private LayoutInflater mInflater = LayoutInflater.from(getActivity());

								@Override
								public long getItemId(int position) {
									return 0;
								}

								@Override
								public Object getItem(int position) {
									return null;
								}

								@Override
								public int getCount() {
									if (playList == null) {
										return 0;
									} else {
										return playList.size();
									}
								}

								public View getView(int position,
										View convertView, ViewGroup parent) {// 该方法返回的View将作为列表框
									LinearLayout ll_lvItem;
									ViewHolder viewHolder;

									if (convertView == null) {
										ll_lvItem = (LinearLayout) mInflater.inflate(R.layout.fragment_listview_item,null);
										viewHolder = new ViewHolder();
										viewHolder.tv_item = (TextView) ll_lvItem.findViewById(R.id.listview_tv);
										ll_lvItem.setTag(viewHolder);
									} else {
										ll_lvItem = (LinearLayout) convertView;
										viewHolder = (ViewHolder) ll_lvItem.getTag();
									}
									if (playList.size() > 0) {
										// 返回的是一个String[]的集合
										String name = playList.get(position)[1].toString();
										viewHolder.tv_item.setText(name);
									}
									// 设置当前播放音乐在列表中的文字颜色为黑色
									viewHolder.tv_item.setTextColor(getResources().getColor(R.color.black));
									return ll_lvItem;
								}

								class ViewHolder {
									TextView tv_item;
								}

							};

							addListView = (ListView) addPopWindow.getContentView().findViewById(R.id.local_menu_add_lv);
							addListView.setAdapter(addBaseAdapter);
							addListView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
								String listId = playList.get(position)[0].toString();
								ArrayList<Integer> list = new ArrayList<Integer>();

								list = DBManager.getMusicList(Integer.valueOf(listId));
								int befor_size = list.size();
								Log.d("local", "list befor size = "+ befor_size);
								DBManager.addToPlayList(curId,Integer.valueOf(listId));

								list = DBManager.getMusicList(Integer.valueOf(listId));
								int after_size = list.size();
								Log.d("local", "list after size = "+ list.size());

								if ((after_size - befor_size) == 1) {
									Log.d("local", "toat");
									Toast.makeText(getActivity(),"添加成功",Toast.LENGTH_SHORT).show();
								}

								addPopWindow.dismiss();
								mPopWindow.dismiss();
							}

						});

						//取消按钮点击事件
						LinearLayout ll_cancel = (LinearLayout)addPopWindow.getContentView().findViewById(R.id.local_button_cancel);
						ll_cancel.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								addPopWindow.dismiss();
							}
						});
						//此处不能break,因为如果brEakins，就执行不到下一步取消的操作，不能恢复背景
						case MotionEvent.ACTION_CANCEL:
						//ll_add.setBackgroundColor(getResources().getColor(R.color.black_popup));
							break;
							
						}
						
						return false;
						
					}//ontouch

					
				});//end add set ON touch

				// 设置设为我喜爱监听
				final LinearLayout ll_setMyLove = (LinearLayout)mPopWindow.getContentView().findViewById(R.id.local_menu_set_mylove);
				ll_setMyLove.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							ll_setMyLove.setBackgroundColor(getResources().getColor(R.color.blue));
							break;
						case MotionEvent.ACTION_UP:
							int curId = musicList.get(position);
							//String s_listId = playList.get(position)[0].toString();
							//int listId = Integer.valueOf(s_listId);
							DBManager.setMyLove(curId);
							
							mPopWindow.dismiss();
							Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_SHORT).show();
							// 此处不能break,因为如果break，就执行不到下一步取消的操作，不能恢复背景
						case MotionEvent.ACTION_CANCEL:
							//ll_setMyLove.setBackgroundColor(getResources().getColor(R.color.black_popup));
							break;
						}
						return false;
					}
				});
				
				// 设置删除监听
				final LinearLayout ll_delete = (LinearLayout) mPopWindow.getContentView().findViewById(R.id.local_menu_delete);
				ll_delete.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							ll_delete.setBackgroundColor(getResources().getColor(R.color.blue));
							break;
						case MotionEvent.ACTION_UP:
							/*
							
							int musicTemp=musiclist.get(selectTemp);
							DBUtil.deleteMusic(musicTemp);
							SharedPreferences sp = getActivity().getSharedPreferences
									("music",Context.MODE_MULTI_PROCESS);
							int musicid = sp.getInt(Constant.SHARED_ID, -1);
							musiclist = DBUtil.getMusicList(Constant.LIST_ALLMUSIC);
							if(musicid==musicTemp)
							{
								if(musiclist.isEmpty())
								{
									musicid=-1;
								}
								else
								{
									musicid=musiclist.get(0);
								}
								SharedPreferences.Editor spEditor=sp.edit();
								spEditor.putInt(Constant.SHARED_ID, musicid);
								spEditor.commit();
								Intent intent_start = new Intent(Constant.MUSIC_CONTROL);
								intent_start.putExtra("cmd", Constant.COMMAND_PLAY);
								intent_start.putExtra("path", DBUtil.getMusicPath(musicid));
								getActivity().sendBroadcast(intent_start);
								Intent intent_pause = new Intent(Constant.MUSIC_CONTROL);
								intent_pause.putExtra("cmd", Constant.COMMAND_PAUSE);
								getActivity().sendBroadcast(intent_pause);
							}
							ba.notifyDataSetChanged();*/
							
							
							
							
							
							int curId = musicList.get(position);
							DBManager.deleteMusicId(curId);
							int playId = getShared("id");
							if (curId == playId) {
								playId = DBManager.getFirstId(Constants.LIST_ALLMUSIC);
								setShared("id",playId);
								if (playId != -1) {
									//发送播放音乐的广播
									String path = DBManager.getMusicPath(playId);
									Intent intent = new Intent(Constants.MP_FILTER);
									intent.putExtra("cmd",Constants.COMMAND_PLAY);
									intent.putExtra("path",path);
									getActivity().sendBroadcast(intent);
									//为什么这里要发送两次广播？因为第一次是改变播放线程开启另一条线程，第二次是暂停
									//如果不这样，直接发送暂停广播，而广播端对于暂停命令只是改变图图标，暂停媒体播放而已
									//则有当下一次点击播放时，它还是会默认媒体直接由暂停状态切换到播放状态，这样不是想要的效果
									intent.putExtra("cmd",Constants.COMMAND_PAUSE);
									getActivity().sendBroadcast(intent);
								}
							}
							//通知适配器更新listview
							baseAdapter.notifyDataSetChanged();
							mPopWindow.dismiss();
							Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
						case MotionEvent.ACTION_CANCEL:
							//ll_delete.setBackgroundColor(getResources().getColor(R.color.black_popup));
							break;
						}
						return false;
					}
				});

				return false;
				
				
			}//on item
			
			
			
			
		});	//set listview long item
			
		
		
		
	}//end setlistview


	// 设置sharedPreferences
	public void setShared(String key, int value) {
		SharedPreferences pref = getActivity().getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	// 获取sharedPreferences
	public int getShared(String key) {
		SharedPreferences pref = getActivity().getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		int value = pref.getInt(key, -1);
		return value;
	}
}
