package com.example.smackdemo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;

import com.example.smackdemo.util.XmppTool;

public class SApp extends Application {
	
	private String tag = "App";
	
	private static SApp instance;
	public static SApp getInstance(){
		return instance;
	}
	
	private ExecutorService es;
	
	public void execRunnable(Runnable r){
		es.execute(r);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		Thread.setDefaultUncaughtExceptionHandler(AppException.getInstance());
		es = Executors.newFixedThreadPool(3);
	}
	
	public void exit(){
		XmppTool.getInstance().disConnectServer();
	}

}
