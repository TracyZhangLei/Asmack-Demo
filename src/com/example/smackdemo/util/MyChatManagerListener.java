package com.example.smackdemo.util;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import android.app.Activity;
import android.widget.Toast;


/***
 * 
 * @author tracyZhang
 *
 */
public class MyChatManagerListener implements ChatManagerListener {
	
	private String tag = "MyChatManagerListener";
	private Activity cxt;

	public MyChatManagerListener(Activity cxt) {
		super();
		this.cxt = cxt;
	}

	@Override
	public void chatCreated(Chat chat, boolean arg1) {
		chat.addMessageListener(new MessageListener(){
			@Override
			public void processMessage(Chat c, final Message msg) {
				SLog.i(tag, msg.getFrom()+" 说："+msg.getBody());
				if(null!=msg.getBody()){
					cxt.runOnUiThread(new Runnable(){
						@Override
						public void run() {
							Toast.makeText(cxt, msg.getFrom()+" 说："+msg.getBody(), Toast.LENGTH_SHORT).show();
						}});
				}
			}});
	}
	
}
