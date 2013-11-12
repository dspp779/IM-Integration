package hsnl.im.integrate;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;

public interface ChatUser extends MessageListener {
	
	public void sendMessage(String message, String to) throws XMPPException;
}
