package com.example.smackdemo;

import java.io.File;
import java.io.IOException;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.smackdemo.util.MyChatManagerListener;
import com.example.smackdemo.util.SLog;
import com.example.smackdemo.util.XmppTool;


/***
 * chat
 * 
 * @author tracyZhang
 *
 */
public class ChatActivity extends Activity implements ChatManagerListener, PacketListener {
	
	private String tag = "ChatActivity";
	
	private ListView listview;
	private EditText replyContent;
	private String userId;
	
	private XMPPConnection con;
	private ChatManager chatManager;
	private Chat chat;
	private MessageListener msgListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		userId = getIntent().getStringExtra("userId");
		setTitle(String.format(getString(R.string.chatTitle), userId));
		initViews();
		initData();
		
	}

	private void initData() {
		con = XmppTool.getInstance().getCon();
		chatManager = con.getChatManager();
		chat = chatManager.createChat(userId,null);
		
//		chatManager.addChatListener(this);
		msgListener = new MessageListener(){
			@Override
			public void processMessage(Chat arg0, final Message message) {
				SLog.i(tag, message.toXML());
				if(null!=message.getBody() && StringUtils.parseBareAddress(message.getFrom()).equals(userId)){
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(ChatActivity.this, message.getFrom()+" 说："+message.getBody(), Toast.LENGTH_SHORT).show();
						}});
					
				}
			}};
		chat = chatManager.createChat(userId,msgListener);
//		PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class),new FromContainsFilter(userId));
//		PacketCollector packetCollector = con.createPacketCollector(filter);
//		con.addPacketListener(this, filter);
		
	}

	private void initViews() {
		listview = (ListView) findViewById(R.id.listview);
		replyContent = (EditText) findViewById(R.id.replyContent);
	}
	
	public void send(View v){
		try {
			chat.sendMessage(replyContent.getText().toString());
			replyContent.setText("");
		} catch (XMPPException e) {
			SLog.e(tag, Log.getStackTraceString(e));
			Toast.makeText(this, "发送失败", Toast.LENGTH_SHORT).show();
		} 
	}

	@Override
	public void chatCreated(Chat chat, boolean createdLocally) {
//		SLog.i(tag, "chatCreated");
	}

	@Override
	public void processPacket(Packet packet) {
//		SLog.i(tag, packet.toString());
	}
	
	public void sendFile(View v){
		File tmpFile = new File(Environment.getExternalStorageDirectory()+"/tmp.txt");
		if(!tmpFile.exists())
			try {
				tmpFile.createNewFile();
			} catch (IOException e) {
				SLog.i(tag, Log.getStackTraceString(e));
			}
		XmppTool.getInstance().sendFile(userId+"/Spark 2.6.3", tmpFile.getPath());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, "删除好友");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		boolean result = XmppTool.getInstance().removeUser(con.getRoster(), userId);
		if(result){
			Toast.makeText(this, "删除好友成功", Toast.LENGTH_SHORT).show();
			setResult(1);
			finish();
		}else
			Toast.makeText(this, "删除好友失败", Toast.LENGTH_SHORT).show();
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		chat.removeMessageListener(msgListener);
//		chatManager.removeChatListener(this);
	}

}
