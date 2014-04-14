package com.example.smackdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/***
 * 
 * @author tracyZhang
 *
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void login(View v){
		startActivity(new Intent(this , Login.class));
	}
	public void regist(View v){
		startActivity(new Intent(this , Regist.class));
	}


}
