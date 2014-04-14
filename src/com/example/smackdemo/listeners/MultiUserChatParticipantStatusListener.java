package com.example.smackdemo.listeners;

import org.jivesoftware.smackx.muc.ParticipantStatusListener;

import com.example.smackdemo.util.SLog;


/**
 * 会议室 成员 离开、进入状态监听
 * @author tracyZhang
 * 
 * MultiUserChat.addParticipantStatusListener()
 *
 */
public class MultiUserChatParticipantStatusListener implements
		ParticipantStatusListener {
	
	private String tag = "会议室成员状态（离开、进入）";

	@Override
	public void adminGranted(String arg0) {
		// TODO Auto-generated method stub
		SLog.i(tag, "adminGranted"+arg0);
	}

	@Override
	public void adminRevoked(String arg0) {
		// TODO Auto-generated method stub
		SLog.i(tag, "adminRevoked"+arg0);
	}

	@Override
	public void banned(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		SLog.i(tag, "banned"+arg0+","+arg1+","+arg2);
	}

	@Override
	public void joined(String arg0) {
		// TODO Auto-generated method stub
		SLog.i(tag, "joined"+arg0);
	}

	@Override
	public void kicked(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		SLog.i(tag, "kicked"+arg0+" , "+arg1+" , "+arg2);
	}

	@Override
	public void left(String arg0) {
		// TODO Auto-generated method stub
		SLog.i(tag, "left"+arg0);
	}

	@Override
	public void membershipGranted(String arg0) {
		// TODO Auto-generated method stub
		SLog.i(tag, "membershipGranted"+arg0);
	}

	@Override
	public void membershipRevoked(String arg0) {
		// TODO Auto-generated method stub
		SLog.i(tag, "membershipRevoked"+arg0);
	}

	@Override
	public void moderatorGranted(String arg0) {
		// TODO Auto-generated method stub
		SLog.i(tag, "moderatorGranted"+arg0);
	}

	@Override
	public void moderatorRevoked(String arg0) {
		// TODO Auto-generated method stub
		SLog.i(tag, "moderatorRevoked"+arg0);
	}

	@Override
	public void nicknameChanged(String arg0, String arg1) {
		// TODO Auto-generated method stub
		SLog.i(tag, "nicknameChanged"+arg0);
	}

	@Override
	public void ownershipGranted(String arg0) {
		// TODO Auto-generated method stub
		SLog.i(tag, "ownershipGranted"+arg0);
	}

	@Override
	public void ownershipRevoked(String arg0) {
		// TODO Auto-generated method stub
		SLog.i(tag, "ownershipRevoked"+arg0);
	}

	@Override
	public void voiceGranted(String arg0) {
		// TODO Auto-generated method stub
		SLog.i(tag, "voiceGranted"+arg0);
	}

	@Override
	public void voiceRevoked(String arg0) {
		// TODO Auto-generated method stub
		SLog.i(tag, "voiceRevoked"+arg0);
	}

}
