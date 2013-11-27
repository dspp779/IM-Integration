package hsnl.im.integrate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class Facebook implements ChatUserInterface {
	private Map<String, String> friendlist = new HashMap<String, String>();
	
	private XMPPConnection connection;
	private String username = "";
	private String password = "";
	
	public Facebook(String usr,String pwd) {
		username = usr;
		password = pwd;
	}
	
	public Facebook getInstance() {
		return this;
	}

	public void login(String userName, String password) throws XMPPException {
		SASLAuthentication.registerSASLMechanism("DIGEST-MD5",
				MySASLDigestMD5Mechanism.class);
		ConnectionConfiguration config = new ConnectionConfiguration(
				"chat.facebook.com", 5222, "chat.facebook.com");
		config.setCompressionEnabled(true);
		config.setSASLAuthenticationEnabled(true);

		connection = new XMPPConnection(config);
		connection.connect();
		connection.login(userName, password);
	}

	public void sendMessage(String message, String to) throws XMPPException {
		to = friendlist.containsKey(to) ? friendlist.get(to) : to;
		Chat Mychat = this.connection.getChatManager().createChat(to, this);
		Mychat.sendMessage(message);
	};
	
	public void sendMessage(Message message, String to) throws XMPPException {
		to = friendlist.containsKey(to) ? friendlist.get(to) : to;
		Chat Mychat = this.connection.getChatManager().createChat(to, this);
		Mychat.sendMessage(message);
	};
	
	public void refreshBuddyList() {
		Roster roster = connection.getRoster();
		roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

		Collection<RosterEntry> entries = roster.getEntries();

		System.out.println("\n\n" + entries.size() + " buddy(ies):");

		for (RosterEntry r : entries) {
			friendlist.put(r.getName(), r.getUser());
			System.out.println(r.getName() + ":" + r.getUser());
		}
	}

	public Set<String> getBuddyList() {
		refreshBuddyList();
		return friendlist.keySet();
	}

	public void addRoster(String bot, String email, String input) {
		try {
			addRoster(bot, input);
			this.sendMessage(input, email);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public void addRoster(String bot, String input) {
		try {
			Roster roster = connection.getRoster();
			roster.createEntry(input, null, null);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		connection.disconnect();
	}

	public void processMessage(Chat chat, Message message) {
		if (message.getType() == Message.Type.chat) {
			String msg = message.getBody();
			String from = chat.getParticipant();
			System.out.println(from + " from FB:" + msg);
			try {
				IntegrationPool.sendMsg(from, msg);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() throws XMPPException {
		// turn on the enhanced debugger
		XMPPConnection.DEBUG_ENABLED = false;

		// provide your login information here		
		this.login(username, password);
	
		this.refreshBuddyList();
		//String friend = null;
		//chat = c.connection.getChatManager().createChat(friendlist.get(Talkto),
		//		this);
		this.connection.getChatManager().addChatListener(
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
		this.disconnect();
	}

	public void alert(String bot, String email, String msg) {
		// turn on the enhanced debugger
		XMPPConnection.DEBUG_ENABLED = false;

		// provide your login information here
		try {
			this.login(username, password);
			this.sendMessage(msg, email);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		this.disconnect();
	}
	
	public String getId(String name)
	{
		return friendlist.containsKey(name) ? friendlist.get(name) : name;
	}
}
