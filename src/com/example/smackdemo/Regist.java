package com.example.smackdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smackdemo.util.SLog;
import com.example.smackdemo.util.XmppTool;


/***
 * Regist
 * 
 * @author tracyZhang
 *
 */
public class Regist extends Activity {
	
	private String tag = "Regist";
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				SLog.i(tag, "成功");
				Toast.makeText(Regist.this, "注册成功", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 2:
				SLog.i(tag, "用户已存在");
				Toast.makeText(Regist.this, "用户已存在", Toast.LENGTH_SHORT).show();
				break;
			case 0:
				SLog.i(tag, "服务器无响应");
				Toast.makeText(Regist.this, "服务器无响应", Toast.LENGTH_SHORT).show();
				break;
			default:
				SLog.i(tag, "注册失败");
				Toast.makeText(Regist.this, "注册失败", Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist);
	}
	
	public void regist(View v){
		EditText nameEt = (EditText) findViewById(R.id.userName);
		EditText pwdEt = (EditText) findViewById(R.id.pwd);
		final String name = nameEt.getText().toString();
		final String pwd = pwdEt.getText().toString();
		SApp.getInstance().execRunnable(new Runnable(){
			@Override
			public void run() {
				int result = XmppTool.getInstance().regist(name, pwd);
				SLog.i(tag, String.valueOf(result));
				handler.sendEmptyMessage(result);
			}});
	}

}
