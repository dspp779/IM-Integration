package hsnl.im.integrate;

import org.jivesoftware.smack.XMPPException;
/*This class is to manage chat exchange of the multiple service.
 *Also,It prepare for three service to exchange the chat  */
public class ExChatMgr  {
	private ChatUser side;
	private String to = "";
	
	public ExChatMgr(ChatUser user, String to){
		this.side = user;
		this.to = to;
	}
	
	public void sendMsg(String str) throws XMPPException{
		side.sendMessage(str, to);
	}
}
