package com.example.smackdemo.listeners;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import com.example.smackdemo.model.MucHistory;
import com.example.smackdemo.util.SLog;


/**
 * 会议室 消息监听
 * 
 * @author tracyZhang
 * 
 * MultiUserChat.addMessageListener(MultiUserChatListener l)
 */
public class MultiUserChatListener implements PacketListener {
	
	private String tag = "MultiUserChatListener";
	private SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

	@Override
	public void processPacket(Packet packet) {
		SLog.i(tag, packet.toXML());
		Message msg = (Message) packet;
		String time = format.format(new Date());
		MucHistory mh = new MucHistory();
		String from = StringUtils.parseResource(msg.getFrom());
		mh.setFriendAccount(from);
		mh.setMhInfo(msg.getBody());
		mh.setMhTime(time);
		SLog.i(tag, mh.toString());
	}

}
