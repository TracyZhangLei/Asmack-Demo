package com.example.smackdemo.util;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.bookmark.BookmarkManager;
import org.jivesoftware.smackx.bookmark.BookmarkedConference;
import org.jivesoftware.smackx.bytestreams.ibb.provider.CloseIQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.OpenIQProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.os.Environment;
import android.util.Log;

import com.example.smackdemo.SApp;
import com.example.smackdemo.model.MucRoom;
import com.example.smackdemo.model.User;


/***
 * api
 * 
 * @author tracyZhang
 *
 */
public class XmppTool {
	
	private String tag = "XmppTool";
	public static final String HOST = "192.168.1.102"; 
	public static final String DOMAIN = "@zhanglei"; 
//	public static final String HOST = "192.168.0.36"; 
//	public static final String DOMAIN = "@bxq-PC"; 
	public static final int PORT = 5222; 
	
	public static final String CONFERENCE = "@conference.";
	
	private static XmppTool instance;
	private XMPPConnection con;
	
	private MultiUserChat muc;
	
	public MultiUserChat getMuc() {
		return muc;
	}

	public void setMuc(MultiUserChat muc) {
		this.muc = muc;
	}


	{
		try {
			Class.forName("org.jivesoftware.smack.ReconnectionManager");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private XmppTool(){
		configure(ProviderManager.getInstance());
		ConnectionConfiguration connConfig = new ConnectionConfiguration(
				HOST, PORT);
		connConfig.setSASLAuthenticationEnabled(false);
		connConfig.setReconnectionAllowed(true);
//		connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
		connConfig.setSendPresence(false);
		con = new XMPPConnection(connConfig);
		con.DEBUG_ENABLED = true;
		Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
		try {
			con.connect();
			
			con.addConnectionListener(new ConnectionListener() {
				
				@Override
				public void reconnectionSuccessful() {
					// TODO Auto-generated method stub
					SLog.i(tag, "重连成功");
				}
				
				@Override
				public void reconnectionFailed(Exception arg0) {
					// TODO Auto-generated method stub
					SLog.i(tag, "重连失败");
				}
				
				@Override
				public void reconnectingIn(int arg0) {
					// TODO Auto-generated method stub
					SLog.i(tag, "重连中");
				}
				
				@Override
				public void connectionClosedOnError(Exception e) {
					// TODO Auto-generated method stub
					SLog.i(tag, "连接出错");
					if(e.getMessage().contains("conflict")){
						SLog.i(tag, "被挤掉了");
						disConnectServer();
					}
				}
				
				@Override
				public void connectionClosed() {
					// TODO Auto-generated method stub
					SLog.i(tag, "连接关闭");
				}
			});
			
		} catch (XMPPException e) {
			SLog.e(tag, Log.getStackTraceString(e));
		}
	}
	
	public XMPPConnection getCon() {
		return con;
	}

	public static XmppTool getInstance(){
		if(null == instance)
			instance = new XmppTool();
		return instance;
	}
	
	public boolean connServer(){
		if(con.isConnected())
			return true;
		try {
			con.connect();
			return true;
		} catch (XMPPException e) {
			SLog.e(tag, Log.getStackTraceString(e));
		}
		return false;
	}
	
	public void disConnectServer(){
		if(null!=con && con.isConnected())
			con.disconnect();
	}
	
	
	/**
	 * 注册
	 * @param name
	 * @param pwd
	 * @return 0 服务端无响应  1成功  2已存在 3 失败
	 */
	public int regist(String name , String pwd){
		
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(con.getServiceName());
		reg.setUsername(name);
		reg.setPassword(pwd);
		reg.addAttribute("Android", "createUser");
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()));
		PacketCollector col = con.createPacketCollector(filter);
		con.sendPacket(reg);
		IQ result = (IQ) col.nextResult(SmackConfiguration.getPacketReplyTimeout());
		col.cancel();
		if(null==result){
			SLog.e(tag, "no response from server");
			return 0;
		}else if(result.getType() == IQ.Type.RESULT){
			SLog.e(tag, result.toString());
			return 1;
		}else if(result.getType() == IQ.Type.ERROR){
			SLog.e(tag, result.toString());
			if(result.getError().toString().equalsIgnoreCase("conflict(409)")){
				return 2;
			}else{
				return 3;
			}
		}
		return 3;
	}
	
	/**
	 * 登录 取离线消息，在设置为在线
	 * @param name
	 * @param pwd
	 * @return
	 */
	public boolean login(String name,String pwd){
		try {
//			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			con.login(name, pwd);
			getOffLineMessages();
			setPresence(0);
			
			setPresence(4);
			
			return true;
		} catch (XMPPException e) {
			SLog.e(tag, Log.getStackTraceString(e));
		}
		return false;
	}
	
	
	/**
	 * 修改密码
	 * @param pwd
	 * @return
	 */
	public boolean changePwd(String pwd){
		try {
			con.getAccountManager().changePassword(pwd);
			return true;
		} catch (XMPPException e) {
			SLog.e(tag, Log.getStackTraceString(e));
		}
		return false;
	}
	
	
	/**
	 * 设置状态
	 * @param state
	 */
	public void setPresence(int state){
		Presence presence;
		switch(state){
		case 0:
			presence = new Presence(Presence.Type.available);
			con.sendPacket(presence);
			SLog.e(tag, "设置在线");
			break;
		case 1:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.chat);
			con.sendPacket(presence);
			SLog.e(tag, "Q我吧");
			SLog.e(tag, presence.toXML());
			break;
		case 2:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.dnd);
			con.sendPacket(presence);
			SLog.e(tag, "忙碌");
			SLog.e(tag, presence.toXML());
			break;
		case 3:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.away);
			con.sendPacket(presence);
			SLog.e(tag, "离开");
			SLog.e(tag, presence.toXML());
			break;
		case 4:
			Roster roster = con.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for(RosterEntry entity:entries){
				presence = new Presence(Presence.Type.unavailable);
				presence.setPacketID(Packet.ID_NOT_AVAILABLE);
				presence.setFrom(con.getUser());
				presence.setTo(entity.getUser());
				con.sendPacket(presence);
				SLog.e(tag, presence.toXML());
			}
			SLog.e(tag, "告知其他用户-隐身");
			
			presence = new Presence(Presence.Type.unavailable);
			presence.setPacketID(Packet.ID_NOT_AVAILABLE);
			presence.setFrom(con.getUser());
			presence.setTo(StringUtils.parseBareAddress(con.getUser()));
			con.sendPacket(presence);
			SLog.e(tag, "告知本用户的其他客户端-隐身");
			SLog.e(tag, presence.toXML());
			break;
		case 5:
			presence = new Presence(Presence.Type.unavailable);
			con.sendPacket(presence);
			SLog.e(tag, "离线");
			SLog.e(tag, presence.toXML());
			break;
		default:
			break;
		}
	}
	
	/**
	 * 删除账号
	 * @return
	 */
	public boolean deleteCount(){
		try {
			con.getAccountManager().deleteAccount();
			return true;
		} catch (XMPPException e) {
			SLog.e(tag, Log.getStackTraceString(e));
		}
		return false;
	}
	
	
	public void configure(ProviderManager pm) {

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			Log.w("TestClient",
					"Can't load class for org.jivesoftware.smackx.packet.Time");
		}

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());

		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());

		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());

		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());

		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());

		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());

		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());

		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());

		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());

		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}

		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());

		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());
		pm.addIQProvider("open", "http://jabber.org/protocol/ibb",
	            new OpenIQProvider());
	    pm.addIQProvider("close", "http://jabber.org/protocol/ibb",
	            new CloseIQProvider());
	    pm.addExtensionProvider("data", "http://jabber.org/protocol/ibb",
	            new DataPacketProvider());

		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}
	
	
	/**
	 * 获取所有分组
	 * @param roster
	 * @return
	 */
	public List<RosterGroup> getGroups(Roster roster){
		List<RosterGroup> list = new ArrayList<RosterGroup>();
		list.addAll(roster.getGroups());
		return list;
	}
	
	/**
	 * 添加分组
	 * @param roster
	 * @param groupName
	 * @return
	 */
	public boolean addGroup(Roster roster,String groupName){
		try{
			roster.createGroup(groupName);
			return true;
		}catch(Exception e){
			SLog.e(tag, Log.getStackTraceString(e));
		}
		return false; 
	}
	
	
	/**
	 * 添加到分组
	 * @param roster
	 * @param userName
	 * @param groupName
	 */
//	public void addUserToGroup(Roster roster,String userName,String groupName){
//		RosterGroup group = roster.getGroup(groupName);
//		if( null == group){
//			group = roster.createGroup(groupName);
//		}
//		RosterEntry entry = roster.getEntry(userName);
//		try {
//			group.addEntry(entry);
//		} catch (XMPPException e) {
//			SLog.e(tag, Log.getStackTraceString(e));
//		}
//	}
	
	
	/**
	 * 获取所有成员
	 * @param roster
	 * @return
	 */
	public List<RosterEntry> getAllEntrys(Roster roster){
		List<RosterEntry> list = new ArrayList<RosterEntry>();
		list.addAll(roster.getEntries());
		return list;
	}
	
	/**
	 * 获取某一个分组的成员
	 * @param roster
	 * @param groupName
	 * @return
	 */
	public List<RosterEntry> getEntrysByGroup(Roster roster,String groupName){
		List<RosterEntry> list = new ArrayList<RosterEntry>();
		RosterGroup group = roster.getGroup(groupName);
		list.addAll(group.getEntries());
		return list;
	}
	
	/**
	 *  获取用户VCard信息
	 * @param user
	 * @return
	 */
	public VCard getVCard(String user){
		VCard vCard = new VCard();
		try {
			vCard.load(con, user);
		} catch (XMPPException e) {
			SLog.e(tag, Log.getStackTraceString(e));
			return null;
		}
		return vCard;
	}
	
	/**
	 * 发送好友请求
	 * @param userName
	 */
	public void sendAddFriendRequest(String userName , Presence.Type type){
		Presence subscription = new Presence(type);  
        subscription.setTo(userName); 
        con.sendPacket(subscription);
	}
	
	/**
	 * 添加好友
	 * @param roster
	 * @param userName
	 * @param name
	 * @param groupName 是否有分组
	 * @return
	 */
	public boolean addUser(Roster roster,String userName,String name,String groupName){
		try {
			roster.createEntry(userName, name, null==groupName?null:new String[]{groupName});
			return true;
		} catch (XMPPException e) {
			SLog.e(tag, Log.getStackTraceString(e));
		}
		return false;
	}
	
	/**
	 * 删除好友
	 * @param roster
	 * @param userName
	 * @return
	 */
	public boolean removeUser(Roster roster,String userName){
		try {
			if(!userName.contains(con.getServiceName()))
				userName = userName+con.getServiceName();
			
			XmppTool.getInstance().sendAddFriendRequest(userName,Presence.Type.unsubscribe);
			
			RosterEntry entry = roster.getEntry(userName);
			if(null!=entry){
				roster.removeEntry(entry);
				return true;
			}
		} catch (XMPPException e) {
			SLog.e(tag, Log.getStackTraceString(e));
		}
		return false;
	}
	
	
	/**
	 * 查找用户
	 * @param serverDomain
	 * @param userName
	 * @return
	 */
	public List<User> searchUsers(String serverDomain,String userName){
		List<User> list = new ArrayList<User>();
		UserSearchManager userSearchManager = new UserSearchManager(con);
		try {
			Form searchForm = userSearchManager.getSearchForm("search."+serverDomain);
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("Name", true);
			answerForm.setAnswer("search", userName);
			ReportedData data = userSearchManager.getSearchResults(answerForm, "search."+serverDomain);
			Iterator<Row> rows = data.getRows();
			while(rows.hasNext()){
				User user = new User();
				Row row = rows.next();
				user.setUserName(row.getValues("Username").next().toString());
				user.setName(row.getValues("Name").next().toString());
				SLog.i(tag, user.toString());
				list.add(user);
			}
		} catch (XMPPException e) {
			SLog.e(tag, Log.getStackTraceString(e));
		}
		return list;
	}
	
	/**
	 * 发送文件
	 * @param recvUser
	 * @param filePath
	 */
	public void sendFile(String recvUser,String filePath){
		FileTransferManager fileTransferManager = new FileTransferManager(con);
		try {
			final OutgoingFileTransfer outgoingFileTransfer =  fileTransferManager.createOutgoingFileTransfer(recvUser);
			SLog.i(tag, "上送文件"+filePath);
			outgoingFileTransfer.sendFile(new File(filePath),"outgoingFileTransfer ^_^");
			SApp.getInstance().execRunnable(new Runnable(){
				@Override
				public void run() {
					while (!outgoingFileTransfer.isDone()) {  
			            SLog.i(tag,"进度:"+outgoingFileTransfer.getProgress());
			            try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							SLog.e(tag, Log.getStackTraceString(e));
						}
			        }
					SLog.i(tag,"上送状态"+outgoingFileTransfer.getStatus());
					if(outgoingFileTransfer.getStatus().equals(Status.complete))
						SLog.i(tag,"上送完毕");
					else if(outgoingFileTransfer.getStatus().equals(Status.error))
						SLog.i(tag,"上送出错");
				}});
		} catch (XMPPException e) {
			SLog.i(tag, "上送文件异常");
			SLog.e(tag, Log.getStackTraceString(e));
		}
	}
	
	
	/**
	 * 注册文件接收器
	 */
	public void registRecvFileListener(){
		FileTransferManager fileTransferManager = new FileTransferManager(con);
		fileTransferManager.addFileTransferListener(new FileTransferListener() {  
            public void fileTransferRequest(final FileTransferRequest request) {  
            	final IncomingFileTransfer transfer = request.accept();  
                try{  
                    SLog.i(tag,"接受文件："+transfer.getFileName());  
                    transfer.recieveFile(new File(Environment.getExternalStorageDirectory()+"/"+request.getFileName()));
                    SApp.getInstance().execRunnable(new Runnable(){
						@Override
						public void run() {
							while (!transfer.isDone()) {  
		        	            SLog.i(tag,"进度:"+transfer.getProgress());
		        	            try {
		        					Thread.sleep(100);
		        				} catch (InterruptedException e) {
		        					SLog.e(tag, Log.getStackTraceString(e));
		        				}
		        	        }
		        			SLog.i(tag,"接受状态"+transfer.getStatus());
		        			if(transfer.getStatus().equals(Status.complete))
		        				SLog.i(tag,"接受完毕");
		        			else if(transfer.getStatus().equals(Status.error)){
		        				transfer.cancel();
		        				SLog.i(tag,"接受出错");
		        			}
						}});
                }catch(Exception e){  
                	SLog.e(tag, Log.getStackTraceString(e));
                	SLog.e(tag, "文件接收出错");
                	transfer.cancel();
                }  
            }  
        });
	}
	
	
	/**
	 * 获取离线消息
	 * @return
	 */
	public List<Message> getOffLineMessages(){
		List<Message> msgs = new ArrayList<Message>();
		OfflineMessageManager offLineMessageManager = new OfflineMessageManager(con);
		try {
			Iterator<Message> it = offLineMessageManager.getMessages();
			while(it.hasNext()){
				Message msg = it.next();
				SLog.i(tag, msg.toXML());
				msgs.add(msg);
			}
			offLineMessageManager.deleteMessages();
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return msgs;
	}
	
	
	
	/**
	 * 创建会议室 
	 * @param roomName
	 * @param roomPwd 会议室密码
	 */
	public boolean createRoom(String roomName , String roomPwd , String subject){
		MultiUserChat multiUserChat  = new MultiUserChat(con,roomName+CONFERENCE+con.getServiceName());
		try {
			multiUserChat.create(roomName);
			Form configForm = multiUserChat.getConfigurationForm();
			Form submitForm = configForm.createAnswerForm();
			for(Iterator<FormField> fields = configForm.getFields();fields.hasNext();){
				FormField formField = fields.next();
				if(!formField.getType().equals(FormField.TYPE_HIDDEN) && formField.getVariable()!=null){
					submitForm.setDefaultAnswer(formField.getVariable());
				}
			}
			
			List<String> owners = new ArrayList<String>();
			owners.add(con.getUser());
			submitForm.setAnswer("muc#roomconfig_roomowners", owners);
			submitForm.setAnswer("muc#roomconfig_roomname", roomName);//房间名字
			submitForm.setAnswer("muc#roomconfig_persistentroom", true);//永久存在，不会再用户都推出时destroy
			submitForm.setAnswer("muc#roomconfig_membersonly", true);//只许成员进入
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);//允许邀请
			submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);//凭密码进入
			submitForm.setAnswer("muc#roomconfig_roomsecret", roomPwd);//密码
//			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			
			multiUserChat.sendConfigurationForm(submitForm);
			
			
		} catch (XMPPException e) {
			SLog.e(tag, "创建聊天室 出错");
			SLog.e(tag, Log.getStackTraceString(e));
			return false;
		}
		return true;
	}
	
	/**
	 * 加入聊天室
	 * @param user
	 * @param pwd 会议室密码
	 * @param roomName
	 * @return
	 */
	public MultiUserChat joinRoom(String user,String pwd,String roomName){
		MultiUserChat muc = new MultiUserChat(con,roomName.contains(CONFERENCE)?roomName:roomName+CONFERENCE+con.getServiceName());
		DiscussionHistory history = new DiscussionHistory();
		history.setMaxStanzas(100);
		history.setSince(new Date(2014,01,01));
//		history.setSeconds(1000);
//		history.setSince(new Date());
		try {
			muc.join(user, pwd, history, SmackConfiguration.getPacketReplyTimeout());
			
			Message msg = muc.nextMessage(1000);
			if(null!=msg)
				SLog.i(tag, msg.toXML());
//			
//			Message msg2 = muc.nextMessage();
//			if(null!=msg2)
//				SLog.i(tag, msg2.toXML());
//			
//			Message msg3 = muc.nextMessage();
//			if(null!=msg3)
//				SLog.i(tag, msg3.toXML());
//			
//			Message msg4 = muc.nextMessage();
//			if(null!=msg4)
//				SLog.i(tag, msg4.toXML());
			
//			Message msg = null;
//			while(null!=(msg = muc.nextMessage())){
//				SLog.i(tag, msg.toXML());
//			}
		} catch (XMPPException e) {
			SLog.e(tag, " 加入 聊天室 出错");
			SLog.e(tag, Log.getStackTraceString(e));
			return null;
		}
		return muc;
	}
	
	/**
	 * 获取会议室成员名字
	 * @param muc
	 * @return
	 */
	public List<String> getMUCMembers(MultiUserChat muc){
		List<String> members = new ArrayList<String>();
		Iterator<String> it = muc.getOccupants();
		while(it.hasNext()){
			String name = StringUtils.parseResource(it.next());
			SLog.i("成员名字", name);
			members.add(name);
		}
		return members;
	}
	
	
	/**
	 * 获取Hostedrooms
	 * @return
	 */
	public List<MucRoom> getAllHostedRooms(){
		List<MucRoom> rooms = new ArrayList<MucRoom>();
		try {
			Collection<HostedRoom> hrooms = MultiUserChat.getHostedRooms(con, con.getServiceName());
			if(!hrooms.isEmpty()){
				for(HostedRoom r:hrooms){
					RoomInfo roominfo = MultiUserChat.getRoomInfo(con, r.getJid());
					SLog.i("会议室Info", roominfo.toString());
					MucRoom mr = new MucRoom();
					mr.setDescription(roominfo.getDescription());
					mr.setName(r.getName());
					mr.setJid(r.getJid());
					mr.setOccupants(roominfo.getOccupantsCount());
					mr.setSubject(roominfo.getSubject());
					rooms.add(mr);
				}
			}
		} catch (XMPPException e) {
			SLog.e(tag, " 获取Hosted Rooms 出错");
			SLog.e(tag, Log.getStackTraceString(e));
		}
		return rooms;
	}
	
	
	/**
	 * 获取已经加入的room列表
	 * @return
	 */
	public List<BookmarkedConference> getJoinedRooms(){
		List<BookmarkedConference> rooms = new ArrayList<BookmarkedConference>();
		try {
			BookmarkManager bm = BookmarkManager.getBookmarkManager(con);
			Collection<BookmarkedConference> cl = bm.getBookmarkedConferences();
			Iterator<BookmarkedConference> it = cl.iterator();
			while(it.hasNext()){
				BookmarkedConference bc = it.next();
				rooms.add(bc);
				SLog.i(tag, bc.toString());
			}
		} catch (XMPPException e) {
			SLog.e(tag, Log.getStackTraceString(e));
		}
		return rooms;
	}
	
	
	/**
	 * 创建给予聊天室的私聊
	 * @param participant   myroom@conference.jabber.org/johndoe
	 * @param listener
	 * @return
	 */
	public Chat createPrivateChat(String participant , MessageListener listener){
		return muc.createPrivateChat(participant, listener);
	}
	
	/**
	 * 用户是否支持聊天室
	 * @param user
	 * @return
	 */
//	public boolean isUserSupportMUC(String user){
//		return MultiUserChat.isServiceEnabled(con, user);
//	}
	
	/**
	 * 离开聊天室
	 */
	public void leaveRoom(){
		if(null!=muc)
			muc.leave();
		muc = null;
	}
	
	public String str2md5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			SLog.e(tag, Log.getStackTraceString(e));
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

}
