package com.example.smackdemo.listeners;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;

import com.example.smackdemo.util.SLog;


/**
 * 收到会议室邀请 监听
 * 
 * @author tracyZhang
 *
 */
public class MyInvitationListener implements InvitationListener {
	
	private String tag = "我收到会议室邀请";

	@Override
	public void invitationReceived(Connection conn, String room, String inviter, String reason, String password, Message msg) {
		// TODO Auto-generated method stub
		SLog.i(tag, "room:"+room+" , inviter:" +inviter+" , reason" + reason +" , password:"+password+" , msg:"+msg.toXML());

	}

}
