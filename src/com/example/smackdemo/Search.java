package com.example.smackdemo;

import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnSuggestionListener;
import android.widget.TextView;

import com.example.smackdemo.model.User;
import com.example.smackdemo.util.XmppTool;


/***
 * Search
 * 
 * @author tracyZhang
 *
 */
public class Search extends Activity implements OnClickListener, OnSuggestionListener, OnItemClickListener {
	
	private EditText searchView;
	private ListView listView;
	private XMPPConnection con;
	private List<User> users;
	private MyAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		con = XmppTool.getInstance().getCon();
		searchView = (EditText) findViewById(R.id.searchView);
		
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		users = XmppTool.getInstance().searchUsers(con.getServiceName(), searchView.getText().toString());
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
	}

	@Override
	public boolean onSuggestionClick(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSuggestionSelect(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return users.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if(null == view){
				view = LayoutInflater.from(Search.this).inflate(android.R.layout.simple_list_item_1, null);
			}
			TextView txt = (TextView) view.findViewById(android.R.id.text1);
			txt.setText(users.get(pos).getUserName());
			return view;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
//		Toast.makeText(this, users.get(pos).toString(), Toast.LENGTH_SHORT).show();
		
		XmppTool.getInstance().sendAddFriendRequest(users.get(pos).getUserName()+"@"+con.getServiceName(),Presence.Type.subscribe);
		Toast.makeText(this, "已经发出添加好友请求", Toast.LENGTH_SHORT).show();
		finish();
		
//		if(XmppTool.getInstance().addUser(con.getRoster(), users.get(pos).getUserName()+"@"+con.getServiceName(), users.get(pos).getName(), null)){
//			XmppTool.getInstance().addUserToGroup(con.getRoster(), users.get(pos).getUserName()+"@"+con.getServiceName(), "我的好友");
//			Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
//			setResult(1);
//			finish();
//		}
//		else
//			Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
	}

}
