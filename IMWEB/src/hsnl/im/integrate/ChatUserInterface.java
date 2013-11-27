package hsnl.im.integrate;

import java.util.Set;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public interface ChatUserInterface extends MessageListener {
	
	public void sendMessage(String message, String to) throws XMPPException;
	public void sendMessage(Message message, String to) throws XMPPException;
	
	public Set<String> getBuddyList();
}
