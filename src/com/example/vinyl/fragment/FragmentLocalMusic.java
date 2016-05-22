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

		// ���ذ�ť��������һ����Ƭ(����Ƭ)
		ImageView ll_back = (ImageView) view.findViewById(R.id.local_back);
		ll_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.popBackStack();
			}
		});

		// �л���������Ƭ
		ImageView iv_search = (ImageView) view.findViewById(R.id.local_search);
		iv_search.setOnClickListener(new OnClickListener() {

			// �����������ܻ�δ������������Ӧ
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

		// �л���ȫ��ɨ��
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

		// ����ģʽ����
		LinearLayout ll_playMode = (LinearLayout) view.findViewById(R.id.playmode_linearlayout);
		final ImageView iv_playMode = (ImageView) view.findViewById(R.id.imageview_playmode);
		final TextView tv_playMode = (TextView) view.findViewById(R.id.textview_playmode);
		ll_playMode.setOnClickListener(new OnClickListener() {
			// sequence-->repeatall-->repeatsingle-->random-->sequence
			@Override
			public void onClick(View v) {
				// ��ȡ��ǰ�Ĳ���ģʽ
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
		musicList = DBManager.getMusicList(Constants.LIST_ALLMUSIC); // ��ȡ���������б�
		TextView tv_musicCount =(TextView)getActivity().findViewById(R.id.local_music_count);
		tv_musicCount.setText(musicList.size()+"��");
		setShared("list",Constants.LIST_ALLMUSIC);
//		TextView tv_musicCount = (TextView) view
//				.findViewById(R.id.local_music_fragment_count);
//		if (musicList == null) {
//			tv_musicCount.setText("��0�׸���");
//			Log.d("local", "musicList == null ��0�׸���");
//		} else {
//			// ��ʾ�������������ڲ��ָ߶Ȳ�����ʾ���������д��Ľ�
//			tv_musicCount.setText("��" + musicList.size() + "�׸���");
//			Log.d("local", "musicList ��" + musicList.size() + "�׸���");
//		}
		setListView();
	}

	// ����ListView
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
			public int getCount() {						//ָ��һ������musicList.size()��ѡ��
				if (musicList == null) {
					return 0;
				}else {
					return musicList.size();
				}
			}
			
			//�÷������ص�View����Ϊ�б��
			@SuppressLint("InflateParams")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				/* �Ż�ListView�����convertViewΪ�գ�����ز��֣���Ϊ����ֱ�Ӷ�convertView��������
				 * ����һ���ڲ��࣬�Կؼ���ʵ�����л��棬��convertViewΪ��ʱ���������󣬽��ؼ�ʵ���������viewHolder
				 * �У���Ϊ��ʱ��viewHolder����ȡ�������������пؼ���ʵ����������ViewHoder�У�����ÿ�ζ�ͨ��
				 * findViewById����ȡʵ����ͨ�������ַ������Դ�����ListView��Ч�ʡ�
				 */
				/* ��Ϊ�Ż��棬�������ڽ�����˴���textview����������ɫ��������
				LinearLayout ll_lvItem;
				ViewHolder viewHolder;
				
				if (convertView == null) {
					Log.d("local", "convertView==null,position = " + position);
					ll_lvItem = (LinearLayout)mInflater.inflate(R.layout.fragment_listview_item,null);
					viewHolder = new ViewHolder();
					viewHolder.tv_item = (TextView)ll_lvItem.findViewById(R.id.listview_tv);
					ll_lvItem.setTag(viewHolder);//��ViewHolder�洢��LinearLayout��
				}else {
					Log.d("local", "convertView!=null,position = " + position);
					ll_lvItem = (LinearLayout)convertView;
					viewHolder = (ViewHolder)ll_lvItem.getTag();//���»�ȡViewHoder
				}
				
				int curId = 0;
				if (musicList.size() > 0) {
					curId = musicList.get(position);
					String name = DBManager.getMusicInfo(curId).get(2);
					String singer = DBManager.getMusicInfo(curId).get(3);
					Log.d("local", "position = " + position);
					Log.d("local", "curId = " + curId);
					viewHolder.tv_item.setText(name+"-"+singer);
					//���õ�ǰ�����������б��е�������ɫΪ��ɫ
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
				musicList = DBManager.getMusicList(Constants.LIST_ALLMUSIC); // ��ȡ���������б�
				TextView tv_musicCount =(TextView)getActivity().findViewById(R.id.local_music_count);
				tv_musicCount.setText(musicList.size()+"��");
				
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
					// ���õ�ǰ�����������б��е�������ɫΪ��ɫ
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
				// ��ȡ���������ID;
				int musicId = musicList.get(position);
				int playingId = getShared("id");
				
				Log.d("local", "item id = " + musicId);
				if (playingId == -1 || playingId != musicId) {
					//��ǰû�в��Ÿ������ҵ���Ĳ������ڲ��ŵĸ����򲥷���ѡ����
					//����¶˵�bar��ʾ��ѡ������Ϣ
					ArrayList<String>musciInfo = DBManager.getMusicInfo(musicId);
					TextView tv_name = (TextView)getActivity().findViewById(R.id.music_name);
					TextView tv_singer = (TextView)getActivity().findViewById(R.id.singer_name);
					tv_name.setText(musciInfo.get(2));
					tv_singer.setText(musciInfo.get(3));
					
					//���Ͳ������ֵĹ㲥
					String path = DBManager.getMusicPath(musicId);
					Log.d("local", "item path = " + path);
					Intent intent = new Intent(Constants.MP_FILTER);
					intent.putExtra("cmd",Constants.COMMAND_PLAY);
					intent.putExtra("path",path);
					getActivity().sendBroadcast(intent);
					setShared("id",musicId);
					
					//֪ͨ����������listview
					baseAdapter.notifyDataSetChanged();
					
				}else {
					//����ԭ����״̬,�����κ���Ӧ
				}
						
			}
			
		});
		
		
		
		//�־õ�����������¶��ϻ���һ��ѡ�----��ӣ����赥������ϲ����ɾ��
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
		

			@SuppressLint("InflateParams") 
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				//����contentView  
			    View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_local_music_menu, null);  
			    mPopWindow = new PopupWindow(contentView,  
			            LayoutParams.MATCH_PARENT,300, true); 
			  
			    mPopWindow.setAnimationStyle(R.style.contextMenuAnim);
			    
			    //�����ⲿ�Ƿ���Ե���������PopupWindow���������PopupWindow�Ƿ����ʧ��
				//setBackgroundDrawable���б���ӣ�����setOutsideTouchable�Ż������ã������˳�popwindow�Ż���Ч��
			    mPopWindow.setBackgroundDrawable(new ColorDrawable(0));
			    mPopWindow.setOutsideTouchable(true);
				//��ȡ����
			    mPopWindow.setFocusable(true);
			    //��ʾPopupWindow  �����ֵĵ׶�
			    mPopWindow.showAtLocation(view,Gravity.BOTTOM, 0, 0);

				//������ӵ��赥����
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
							//����һ������
							//����contentView  
						    View add_contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_local_music_menu_add, null);  
						    addPopWindow = new PopupWindow(add_contentView,  LayoutParams.MATCH_PARENT,800, true); 
						  
						    addPopWindow.setAnimationStyle(R.style.contextMenuAnim);
						    
						    //�����ⲿ�Ƿ���Ե���������PopupWindow���������PopupWindow�Ƿ����ʧ��
							//setBackgroundDrawable���б���ӣ�����setOutsideTouchable�Ż������ã������˳�popwindow�Ż���Ч��
						    addPopWindow.setBackgroundDrawable(new ColorDrawable(0));
						    addPopWindow.setOutsideTouchable(true);
							//��ȡ����
						    addPopWindow.setFocusable(true);
						    playList = DBManager.getPlayList();
						    final int curId = musicList.get(position);
						   // setAddListView(curId);
						    
						    //��ʾPopupWindow  �����ֵĵ׶�
						    addPopWindow.showAtLocation(getView(),Gravity.BOTTOM, 0, 0);
						    
							BaseAdapter addBaseAdapter = new BaseAdapter() {

								// �õ�һ��LayoutInflater�����������벼��
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
										View convertView, ViewGroup parent) {// �÷������ص�View����Ϊ�б��
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
										// ���ص���һ��String[]�ļ���
										String name = playList.get(position)[1].toString();
										viewHolder.tv_item.setText(name);
									}
									// ���õ�ǰ�����������б��е�������ɫΪ��ɫ
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
									Toast.makeText(getActivity(),"��ӳɹ�",Toast.LENGTH_SHORT).show();
								}

								addPopWindow.dismiss();
								mPopWindow.dismiss();
							}

						});

						//ȡ����ť����¼�
						LinearLayout ll_cancel = (LinearLayout)addPopWindow.getContentView().findViewById(R.id.local_button_cancel);
						ll_cancel.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								addPopWindow.dismiss();
							}
						});
						//�˴�����break,��Ϊ���brEakins����ִ�в�����һ��ȡ���Ĳ��������ָܻ�����
						case MotionEvent.ACTION_CANCEL:
						//ll_add.setBackgroundColor(getResources().getColor(R.color.black_popup));
							break;
							
						}
						
						return false;
						
					}//ontouch

					
				});//end add set ON touch

				// ������Ϊ��ϲ������
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
							Toast.makeText(getActivity(), "���óɹ�", Toast.LENGTH_SHORT).show();
							// �˴�����break,��Ϊ���break����ִ�в�����һ��ȡ���Ĳ��������ָܻ�����
						case MotionEvent.ACTION_CANCEL:
							//ll_setMyLove.setBackgroundColor(getResources().getColor(R.color.black_popup));
							break;
						}
						return false;
					}
				});
				
				// ����ɾ������
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
									//���Ͳ������ֵĹ㲥
									String path = DBManager.getMusicPath(playId);
									Intent intent = new Intent(Constants.MP_FILTER);
									intent.putExtra("cmd",Constants.COMMAND_PLAY);
									intent.putExtra("path",path);
									getActivity().sendBroadcast(intent);
									//Ϊʲô����Ҫ�������ι㲥����Ϊ��һ���Ǹı䲥���߳̿�����һ���̣߳��ڶ�������ͣ
									//�����������ֱ�ӷ�����ͣ�㲥�����㲥�˶�����ͣ����ֻ�Ǹı�ͼͼ�꣬��ͣý�岥�Ŷ���
									//���е���һ�ε������ʱ�������ǻ�Ĭ��ý��ֱ������ͣ״̬�л�������״̬������������Ҫ��Ч��
									intent.putExtra("cmd",Constants.COMMAND_PAUSE);
									getActivity().sendBroadcast(intent);
								}
							}
							//֪ͨ����������listview
							baseAdapter.notifyDataSetChanged();
							mPopWindow.dismiss();
							Toast.makeText(getActivity(), "ɾ���ɹ�", Toast.LENGTH_SHORT).show();
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


	// ����sharedPreferences
	public void setShared(String key, int value) {
		SharedPreferences pref = getActivity().getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	// ��ȡsharedPreferences
	public int getShared(String key) {
		SharedPreferences pref = getActivity().getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		int value = pref.getInt(key, -1);
		return value;
	}
}
