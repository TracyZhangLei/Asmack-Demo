package com.example.smackdemo.listeners;

import org.jivesoftware.smackx.muc.InvitationRejectionListener;

import com.example.smackdemo.util.SLog;


/**
 * 邀请被拒绝 加入聊天室 监听
 * 
 * @author tracyZhang
 *
 */
public class MyInvitationRejectionListener implements
		InvitationRejectionListener {
	
	private String tag = "邀请进入聊天室被拒绝";

	@Override
	public void invitationDeclined(String invitee, String reason) {
		SLog.i(tag, "被拒绝了......");
		SLog.i(tag, "被邀请者:"+invitee+" , 原因:"+reason);
	}

}
