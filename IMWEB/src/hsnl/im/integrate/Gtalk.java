package hsnl.im.integrate;

import java.util.Collection;
import java.util.HashMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class Gtalk implements ChatUser {
	XMPPConnection connection;
	//private volatile static Facebook fclient;
	private String username = "";
	private String password = "";
	private ExChatMgr exChat;
	Chat chat;
	
	Gtalk(String usr,String pwd) {
		username=usr;
		password=pwd;
	}	
	public void setExChatMgr(ExChatMgr Mgr) {
		exChat=Mgr;
	}

	public Gtalk getInstance() {
		/*if (fclient == null) {
			synchronized (Facebook.class) {
				if (fclient == null) {
					fclient = new Facebook();
				}
			}
		}
		return fclient;*/
		return this;
	}

	public void login(String userName, String password) throws XMPPException {
		connection = new XMPPConnection("gmail.com");
		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		connection.connect();
		connection.login(userName, password);
	}

	public void sendMessage(String message, String to) throws XMPPException {
	
		Chat Mychat;
		Mychat=getInstance().connection.getChatManager().createChat(to, this);
		Mychat.sendMessage(message);
	}

	public HashMap<String, String> displayBuddyList() {
		Roster roster = connection.getRoster();
		HashMap<String, String> result = new HashMap<String, String>();
		roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

		Collection<RosterEntry> entries = roster.getEntries();

		System.out.println("\n\n" + entries.size() + " buddy(ies):");

		for (RosterEntry r : entries) {
			result.put(r.getName(), r.getUser());

		}
		return result;
	}

	public void addRoster(String bot, String email, String input) {
		Gtalk c = getInstance();
		try {
			addRoster(bot, input);
			c.sendMessage(input, email);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addRoster(String bot, String input) {
		Gtalk c = getInstance();
		try {
			c.login(username, password);
			System.out.println("test...");
			Roster roster = connection.getRoster();

			System.out.println("test");
			roster.createEntry(input, null, null);

		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.disconnect();
	}

	public void disconnect() {
		connection.disconnect();
	}

	public void processMessage(Chat chat, Message message) {
		if (message.getType() == Message.Type.chat) {
			String from = chat.getParticipant();
			System.out.println(from + " from Gtalk:" + message.getBody());
			try {
				
				if (!message.getBody().equals("null")) {
					System.out.println("~~~");
					IntegratePool.sendMsg(from, message.getBody());
				
				}
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void run() throws XMPPException {
		// declare variables
		Gtalk c = getInstance();
		
		//String msg;
		//HashMap<String, String> friendlist = new HashMap<String, String>();
		// turn on the enhanced debugger
		XMPPConnection.DEBUG_ENABLED = false;

		// provide your login information here
		
			c.login(username, password);
		
		//friendlist = c.displayBuddyList();
		//String friend = null;
		//chat = c.connection.getChatManager().createChat(friendlist.get(Talkto),
		//		this);
		c.connection.getChatManager().addChatListener(
			    new ChatManagerListener() {
			        @Override
			        public void chatCreated(Chat chat, boolean createdLocally)
			        {
			            if (!createdLocally)
			                chat.addMessageListener(getInstance());
			        }
			    });
		// System.exit(0);
	}

	public void destroy() {
		Gtalk c = getInstance();
		c.disconnect();

	}

	public void alert(String bot, String email, String msg) {
		Gtalk c = getInstance();
		// turn on the enhanced debugger
		XMPPConnection.DEBUG_ENABLED = false;

		// provide your login information here

		try {
			c.login(username, password);
			c.sendMessage(msg, email);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		c.disconnect();
		
	}
	
}
