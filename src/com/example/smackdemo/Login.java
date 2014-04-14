package com.example.smackdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smackdemo.util.SLog;
import com.example.smackdemo.util.XmppTool;


/***
 * login
 * 
 * @author tracyZhang
 *
 */
public class Login extends Activity {
	
private String tag = "Login";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}
	
	public void login(View v){
		
		EditText nameEt = (EditText) findViewById(R.id.userName);
		EditText pwdEt = (EditText) findViewById(R.id.pwd);
		final String name = nameEt.getText().toString();
		final String pwd = pwdEt.getText().toString();
		SApp.getInstance().execRunnable(new Runnable(){
			@Override
			public void run() {
				boolean result = XmppTool.getInstance().login(name, pwd);
				SLog.i(tag, String.valueOf(result));
				if(result){
//					XmppTool.getInstance().setPresence(0);
					XmppTool.getInstance().registRecvFileListener();
					startActivity(new Intent(Login.this,MyHome.class));
//					startService(new Intent(Login.this,PresenceService.class));
					finish();
				}else{
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							Toast.makeText(Login.this, "登陆失败", Toast.LENGTH_LONG).show();
						}});
				}
			}});
		
	}

}
