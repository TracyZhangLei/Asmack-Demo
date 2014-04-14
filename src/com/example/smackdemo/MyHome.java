package com.example.smackdemo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.example.smackdemo.adapter.MyFriendExpadableAdapter;
import com.example.smackdemo.util.SLog;
import com.example.smackdemo.util.XmppTool;


/***
 * MyHome
 * 
 * @author tracyZhang
 *
 */
public class MyHome extends Activity implements OnChildClickListener, InvitationListener {
	
	private String tag = "MyHome";
	private ExpandableListView exList;
	private XMPPConnection con;
	private List<RosterGroup> groups;
	private MyFriendExpadableAdapter adapter;
	private Map<RosterGroup,List<RosterEntry>> childs;
//	private ChatManager chatManager;
	private Roster roster;
	
	PacketListener listener;
	PacketFilter filter;
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				setAdapter();
				break;
			}
		}
	};
	
	private void addListener(){
		MultiUserChat.addInvitationListener(con, this);
		con.addPacketListener(listener, filter);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_home);
		exList = (ExpandableListView) findViewById(R.id.exList);
		exList.setOnChildClickListener(this);
		con = XmppTool.getInstance().getCon();
		roster = con.getRoster();
		initListeners();
		initRosterData();
	}
	
	private void initListeners() {
		// TODO Auto-generated method stub
		filter = new AndFilter(new PacketTypeFilter(
				Packet.class));
		listener = new PacketListener() {
			@Override
			public void processPacket(final Packet packet) {
				SLog.i(tag, packet.toXML());
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						// 看API可知道 Presence是Packet的子类
						if (packet instanceof Presence) {
							Presence presence = (Presence) packet;
							
//							SLog.i(tag, presence.toXML());
							
							// Presence还有很多方法，可查看API
							final String from = presence.getFrom();// 发送方
							final String to = presence.getTo();// 接收方
							// Presence.Type有7中状态
							if (presence.getType().equals(Presence.Type.subscribe)) {// 好友申请
								
								SLog.i(tag, from+"申请添加"+to+"为好友");
								Toast.makeText(getApplicationContext(),
										from+"申请添加"+to+"为好友",
										Toast.LENGTH_SHORT).show();
								
								AlertDialog.Builder builder = new AlertDialog.Builder(MyHome.this);
								builder.setTitle("Subscribe");
								builder.setMessage(from+"申请添加"+to+"为好友");
								builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										Presence ok = new Presence(Presence.Type.subscribed);
										ok.setTo(from);
										con.sendPacket(ok);
									}
								});
								builder.setNeutralButton("同意并加对方为好友", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										Presence ok = new Presence(Presence.Type.subscribed);
										ok.setTo(from);
										con.sendPacket(ok);
										
										Presence ok2 = new Presence(Presence.Type.subscribe);
										ok2.setTo(from);
										con.sendPacket(ok2);
										
									}
								});
								builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										Presence ok = new Presence(Presence.Type.unsubscribe);
										ok.setTo(from);
										con.sendPacket(ok);
									}
								});
								builder.create().show();

							} else if (presence.getType().equals(
									Presence.Type.subscribed)) {// 同意添加好友

								SLog.i(tag, from+"同意了"+to+"的好友请求");
								Toast.makeText(getApplicationContext(),
										from+"同意了"+to+"的好友请求",
										Toast.LENGTH_SHORT).show();
								
								XmppTool.getInstance().addUser(con.getRoster(), from, StringUtils.parseName(from), "我的好友");
								SLog.i(tag, "addUser");
								initRosterData();

							} else if (presence.getType().equals(
									Presence.Type.unsubscribe)) {// 拒绝添加好友 和 删除好友
								
								SLog.i(tag, from+"拒绝了"+to+"的好友请求");
								Toast.makeText(getApplicationContext(),
										from+"拒绝了"+to+"的好友请求",
										Toast.LENGTH_SHORT).show();

							} else if (presence.getType().equals(
									Presence.Type.unsubscribed)) {// 被拒绝
								
								SLog.i(tag, from+"拒绝了"+to+"的好友请求");
								Toast.makeText(getApplicationContext(),
										from+"拒绝了"+to+"的好友请求",
										Toast.LENGTH_SHORT).show();
								initRosterData();
								
							} else if (presence.getType().equals(
									Presence.Type.unavailable)) {// 好友下线
																	// 要更新好友列表，可以在这收到包后，发广播到指定页面
																	// 更新列表
								SLog.i(tag, from + " 下线啦！");
								Toast.makeText(getApplicationContext(),
										from + " 下线啦！",
										Toast.LENGTH_SHORT).show();
								

							} else if (presence.getType().equals(
									Presence.Type.available)) {// 好友上线
								SLog.i(tag, from + " 上线啦！");
								Toast.makeText(getApplicationContext(),
										from + " 上线啦！",
										Toast.LENGTH_SHORT).show();

							}
						} else if (packet instanceof Message) {
							Message msg = (Message) packet;
							if(null!=msg.getBody() && msg.getType().equals(Message.Type.chat))
									Toast.makeText(getApplicationContext(),
										msg.getFrom() + "说：" + msg.getBody(),
										Toast.LENGTH_SHORT).show();
						}
						
						
					}});
				
			}
		};
	}

	protected void setAdapter() {
		adapter = new MyFriendExpadableAdapter(this,groups,childs);
		exList.setAdapter(adapter);
	}

	private void initRosterData(){
		SApp.getInstance().execRunnable(new Runnable(){
			@Override
			public void run() {
				groups = XmppTool.getInstance().getGroups(roster);
				childs = new HashMap<RosterGroup,List<RosterEntry>>();
				for(RosterGroup g:groups){
					List<RosterEntry> child = XmppTool.getInstance().getEntrysByGroup(roster, g.getName());
					childs.put(g, child);
				}
				handler.sendEmptyMessage(1);
			}});
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		RosterEntry child = (RosterEntry) adapter.getChild(groupPosition, childPosition);
		Intent intent = new Intent(this,ChatActivity.class);
		intent.putExtra("userId", child.getUser());
		startActivityForResult(intent,1);
		return false;
	}
	
	private String[] states = {"在线","Q我吧","忙碌","离开","隐身","离线"};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, 0, 0, "Search");
		menu.add(2, 2, 2, "更改状态");
		menu.add(3, 3, 3, "聊天室");
		item.setIcon(android.R.drawable.ic_menu_search);
		return super.onCreateOptionsMenu(menu);
	}
	
	
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if(item.getItemId() ==0){
			Intent intent = new Intent(this,Search.class);
			startActivityForResult(intent,1);
		}else if(item.getItemId() == 2){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("设置状态");
			builder.setItems(states, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, final int which) {
					// TODO Auto-generated method stub
					SApp.getInstance().execRunnable(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							XmppTool.getInstance().setPresence(which);
						}});
				}
			});
			builder.create().show();
		}else if(item.getItemId() == 3){
			startActivity(new Intent(this,MUCActivity.class));
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(1 == resultCode && 1==requestCode){
			initRosterData();
		}
	}
	

	@Override
	public void invitationReceived(Connection conn, final String room, final String inviter, String reason, final String password, org.jivesoftware.smack.packet.Message msg) {
		// TODO Auto-generated method stub
		SLog.i(tag, "room:"+room+" , inviter:" +inviter+" , reason" + reason +" , password:"+password+" , msg:"+msg.toXML());
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("您有群聊邀请");
		builder.setMessage(inviter+" 邀请我加入 "+room);
		builder.setPositiveButton("加入", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
				
				MultiUserChat tmp = XmppTool.getInstance().joinRoom(con.getUser(), password, room);
				if(null!=tmp){
					if(null!=XmppTool.getInstance().getMuc()){
						XmppTool.getInstance().leaveRoom();
						XmppTool.getInstance().setMuc(tmp);
						Toast.makeText(MyHome.this, "切换房间成功", Toast.LENGTH_SHORT).show();
						SLog.i(tag, "重新初始化房间.....");
						startActivity(new Intent(MyHome.this,MUCRoom.class));
					}else{
						XmppTool.getInstance().setMuc(tmp);
						startActivity(new Intent(MyHome.this,MUCRoom.class));
					}
				}else{
					Toast.makeText(MyHome.this, "进入新房间失败", Toast.LENGTH_SHORT).show();
					SLog.i(tag, "呆着不动.....");
				}
			}
		});
		builder.setNeutralButton("拒绝", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
				MultiUserChat.decline(con, room, inviter, "I'm too busy");
			}});
		
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				builder.create().show();
			}});
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		listener = null;
		filter = null;
		SApp.getInstance().execRunnable(new Runnable(){
			@Override
			public void run() {
				XmppTool.getInstance().disConnectServer();
			}});
		super.onDestroy();
	}
	
	private void removeListener(){
		MultiUserChat.removeInvitationListener(con, this);
		con.removePacketListener(listener);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		removeListener();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		addListener();
	}

}
