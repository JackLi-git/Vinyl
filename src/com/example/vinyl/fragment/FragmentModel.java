package com.example.vinyl.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.example.vinyl.R;
import com.example.vinyl.database.DBManager;
import com.example.vinyl.util.Constants;
/* FragmentMain
 * ��ϲ����Ƭ-----------���⣨��ϲ����
 * ��������б�---------���⣨������ţ�
 * ���ع����б�---------���⣨���ع���
 * �ҵĸ赥����һ���赥��Ƭ
 */
public class FragmentModel extends Fragment {
	private String title;
	public static BaseAdapter baseAdapter;
	ListView listView;
	ArrayList<Integer> musicList;
	ArrayList<String[]> playList;
	int playListNumber = -1;
	View view;
	ListView addListView;
	Context mContext = getActivity();
	private PopupWindow mPopWindow;
	private PopupWindow addPopWindow;

	//����ϲ����������ţ����ع���ʹ��
	public FragmentModel(String title) {
		this.title = title;
		Log.d("model", "onCreate 0");
	}
	
	//���Ҵ������б�ʹ��
	public FragmentModel(String title,int number) {
		this.title = title;
		this.playListNumber = number;
		Log.d("model", "onCreate 1");
		Log.d("model", "onCreate 1 palyListNumber = "+playListNumber);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_model,container,false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//���ذ�ť��������һ����Ƭ(����Ƭ)
		ImageView ll_back = (ImageView)view.findViewById(R.id.model_back);
		ll_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.popBackStack();
			}
		});
		
		//���ñ���
		TextView ll_title = (TextView)view.findViewById(R.id.model_title);
		ll_title.setText(title);
		
		//�л���������Ƭ
//		ImageView iv_search = (ImageView)view.findViewById(R.id.model_search);
//		iv_search.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				FragmentSearch fragmentSearch = new FragmentSearch();
//				FragmentManager fragmentManager = getFragmentManager();
//				FragmentTransaction transaction = fragmentManager.beginTransaction();
//				transaction.replace(R.layout.fragment_model,fragmentSearch);
//				transaction.commit();
//				transaction.addToBackStack(null);
//			}
//		});
		
		// ����ģʽ����
		LinearLayout ll_playMode = (LinearLayout) view.findViewById(R.id.model_playmode_linearlayout);
		final ImageView iv_playMode = (ImageView) view.findViewById(R.id.model_imageview_playmode);
		final TextView tv_playMode = (TextView) view.findViewById(R.id.model_textview_playmode);
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
		super.onResume();
		//�����ݿ��ȡ��Ӧ�����б�	
		if (title.equals(Constants.FRAGMENT_MYLOVE)) {
			musicList = DBManager.getMusicList(Constants.LIST_MYLOVE);
			setShared("list",Constants.LIST_MYLOVE);
		} else if (title.equals(Constants.FRAGMENT_RECENTPLAY)) {
			musicList = DBManager.getMusicList(Constants.LIST_LASTPLAY);
			setShared("list",Constants.LIST_LASTPLAY);
		} else if (title.equals(Constants.FRAGMENT_DOWNLOAD)) {
			musicList = DBManager.getMusicList(Constants.LIST_DOWNLOAD);
			setShared("list",Constants.LIST_DOWNLOAD);
		} else {
			Log.d("model", "onResume musicList0000");
			musicList = DBManager.getMusicList(playListNumber);
			setShared("list",playListNumber);
			Log.d("model", "onResume musicList1111");
		}
		setShared("list",Constants.LIST_ALLMUSIC);
		Log.d("model", "onResume musicList.size = " + musicList.size());
		setListView();
	}

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
				
//				if (title.equals(Constants.FRAGMENT_MYLOVE)) {
//					musicList = DBManager.getMusicList(Constants.LIST_MYLOVE);
//				} else if (title.equals(Constants.FRAGMENT_RECENTPLAY)) {
//					musicList = DBManager.getMusicList(Constants.LIST_LASTPLAY);
//				} else if (title.equals(Constants.FRAGMENT_DOWNLOAD)) {
//					musicList = DBManager.getMusicList(Constants.LIST_DOWNLOAD);
//				} else {
//					musicList = DBManager.getMusicList(playListNumber);
//				}
					
				//������������ϸ��ĵ�ǰ�б����������
//				TextView tv_musicCount =(TextView)getActivity().findViewById(R.id.local_music_count);
//				tv_musicCount.setText(musicList.size()+"��");
				
				LinearLayout ll_lvItem;
				ll_lvItem = (LinearLayout) mInflater.inflate(R.layout.fragment_listview_item, null);
				TextView tv_item = (TextView) ll_lvItem.findViewById(R.id.listview_tv);

				int curId = 0;
				if (musicList.size() > 0) {
					curId = musicList.get(position);
					String name = DBManager.getMusicInfo(curId).get(2);
					String singer = DBManager.getMusicInfo(curId).get(3);
					Log.d("model", "position = " + position);
					Log.d("model", "curId = " + curId);
					tv_item.setText(name + "-" + singer);
					// ���õ�ǰ�����������б��е�������ɫΪ��ɫ
					int musicId = getShared("id");
					if (curId == musicId) {
						Log.d("model", "getview set blue musicId = " + musicId);
						tv_item.setTextColor(getResources().getColor(R.color.blue));
					}
				}

				return ll_lvItem;
			}
		};	
		
		listView = (ListView)view.findViewById(R.id.model_listview);
		listView.setAdapter(baseAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SharedPreferences prev = getActivity().getSharedPreferences("music",Context.MODE_MULTI_PROCESS);
				SharedPreferences.Editor editor=prev.edit();
				if (title.equals(Constants.FRAGMENT_MYLOVE)) {
					editor.putInt("musiclist", Constants.LIST_MYLOVE);
				} else if (title.equals(Constants.FRAGMENT_RECENTPLAY)) {
					editor.putInt("musiclist", Constants.LIST_LASTPLAY);
				} else if (title.equals(Constants.FRAGMENT_DOWNLOAD)) {
					editor.putInt("musiclist", Constants.LIST_DOWNLOAD);
				} else {
					editor.putInt("musiclist",playListNumber);
				}
				
				
				editor.commit();
				
				// ��ȡ���������ID;
				int musicId = musicList.get(position);
				int playingId = getShared("id");
				
				Log.d("model", "item id = " + musicId);
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
					Log.d("model", "item path = " + path);
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
						Log.d("model", "add");
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
								Log.d("model", "list befor size = "+ befor_size);
								DBManager.addToPlayList(curId,Integer.valueOf(listId));

								list = DBManager.getMusicList(Integer.valueOf(listId));
								int after_size = list.size();
								Log.d("model", "list after size = "+ list.size());

								if ((after_size - befor_size) == 1) {
									Log.d("model", "toat");
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
							int curId = musicList.get(position);
							
							if (title.equals(Constants.FRAGMENT_MYLOVE)) {
								DBManager.deleteMusicInList(curId,Constants.LIST_MYLOVE);
								musicList = DBManager.getMusicList(Constants.LIST_MYLOVE);
							} else if (title.equals(Constants.FRAGMENT_RECENTPLAY)) {
								DBManager.deleteMusicInList(curId,Constants.LIST_LASTPLAY);
								musicList = DBManager.getMusicList(Constants.LIST_LASTPLAY);
							} else if (title.equals(Constants.FRAGMENT_DOWNLOAD)) {
								DBManager.deleteMusicInList(curId,Constants.LIST_DOWNLOAD);
								musicList = DBManager.getMusicList(Constants.LIST_DOWNLOAD);
							} else {
								DBManager.deleteMusicInList(curId, playListNumber);
								musicList = DBManager.getMusicList(playListNumber);
							}
							
							int playId = getShared("id");
							if (curId == playId) {
								playId = DBManager.getFirstId(playListNumber);
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

	}
	
	

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
