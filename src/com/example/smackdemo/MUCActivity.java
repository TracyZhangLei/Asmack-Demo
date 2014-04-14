package com.example.smackdemo;

import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.bookmark.BookmarkedConference;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smackdemo.model.MucRoom;
import com.example.smackdemo.util.XmppTool;


/***
 * MultiUserChat Main
 * 
 * @author tracyZhang
 *
 */
public class MUCActivity extends Activity implements OnItemClickListener {
	
	private EditText roomName , roomPwd , roomSubject;
	private ListView roomList;
	XMPPConnection con;
	
	SApp app;
	
	List<BookmarkedConference> joinedRooms;
	JoinedRoomAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.muc_home);
		
		roomName = (EditText) findViewById(R.id.roomName);
		roomPwd = (EditText) findViewById(R.id.roomPwd);
		roomSubject = (EditText) findViewById(R.id.roomSubject);
		roomList = (ListView) findViewById(R.id.roomList);
		roomList.setOnItemClickListener(this);
		app = SApp.getInstance();
		con = XmppTool.getInstance().getCon();
		
		
	}
	
	public void createRoom(View v){
		app.execRunnable(new Runnable(){

			@Override
			public void run() {
				final boolean result = XmppTool.getInstance().createRoom(roomName.getText().toString(), roomPwd.getText().toString(),roomSubject.getText().toString());
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						if(result)
							Toast.makeText(MUCActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(MUCActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
							
					}});
			}});
		
	}
	
	public void joinRoom(View v){
		String pwd = roomPwd.getText().toString();
		String roomNameStr = roomName.getText().toString();
		joinRoom(pwd,roomNameStr);
	}
	
	private void joinRoom(final String pwd , final String roomNameStr){
		app.execRunnable(new Runnable(){
			@Override
			public void run() {
				final MultiUserChat muc = XmppTool.getInstance().joinRoom(con.getUser(), pwd , roomNameStr);
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						if(null != muc){
							XmppTool.getInstance().setMuc(muc);
							Toast.makeText(MUCActivity.this, "进入成功", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(MUCActivity.this , MUCRoom.class);
							MUCActivity.this.startActivity(intent);
						}
						else
							Toast.makeText(MUCActivity.this, "进入失败", Toast.LENGTH_SHORT).show();
							
					}});
			}});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		getJoinedRooms();
		super.onResume();
	}
	
	private void getJoinedRooms(){
		app.execRunnable(new Runnable(){
			@Override
			public void run() {
				joinedRooms = XmppTool.getInstance().getJoinedRooms();
				adapter = new JoinedRoomAdapter();
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						roomList.setAdapter(adapter);
					}});
			}});
	}
	
	class JoinedRoomAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return joinedRooms.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(null==convertView){
				convertView = LayoutInflater.from(MUCActivity.this).inflate(android.R.layout.simple_list_item_1, null);
			}
			TextView text = (TextView) convertView.findViewById(android.R.id.text1);
			text.setText(joinedRooms.get(position).getName());
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
		// TODO Auto-generated method stub
		BookmarkedConference bc = joinedRooms.get(pos);
		joinRoom(bc.getPassword(),bc.getJid());
		
	}
	

}
