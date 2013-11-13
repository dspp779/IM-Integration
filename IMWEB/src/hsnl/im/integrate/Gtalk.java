package hsnl.im.integrate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class Gtalk implements ChatUserInterface {
	private List<String> friendlist = new ArrayList<String>();
	
	private XMPPConnection connection;
	private String username = "";
	private String password = "";
	
	Gtalk(String usr,String pwd) {
		username = usr;
		password = pwd;
	}

	public Gtalk getInstance() {
		return this;
	}

	public void login(String userName, String password) throws XMPPException {
		connection = new XMPPConnection("gmail.com");
		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		connection.connect();
		connection.login(userName, password);
	}

	public void sendMessage(String message, String to) throws XMPPException {
		Chat Mychat = this.connection.getChatManager().createChat(to, this);
		Mychat.sendMessage(message);
	}

	public void sendMessage(Message message, String to) throws XMPPException {
		Chat Mychat = this.connection.getChatManager().createChat(to, this);
		Mychat.sendMessage(message);
	}

	public void refreshBuddyList() {
		Roster roster = connection.getRoster();
		roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

		Collection<RosterEntry> entries = roster.getEntries();

		System.out.println("\n\n" + entries.size() + " buddy(ies):");

		for (RosterEntry r : entries) {
			friendlist.add(r.getUser());
			System.out.println(r.getUser());
		}
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
			this.login(username, password);
			System.out.println("test...");
			Roster roster = connection.getRoster();

			System.out.println("test");
			roster.createEntry(input, null, null);

		} catch (XMPPException e) {
			e.printStackTrace();
		}
		this.disconnect();
	}

	public void disconnect() {
		connection.disconnect();
	}

	public void processMessage(Chat chat, Message message) {
		if (message.getType() == Message.Type.chat) {
			String msg = message.getBody();
			String from = chat.getParticipant();
			System.out.println(from + " from Gtalk:" + msg);
			try {
				IntegratePool.sendMsg(from, msg);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() throws XMPPException {
		//String msg;
		//HashMap<String, String> friendlist = new HashMap<String, String>();
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
	}

	public void destroy() {
		this.disconnect();
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
