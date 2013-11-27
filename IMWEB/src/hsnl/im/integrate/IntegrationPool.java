package hsnl.im.integrate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.XMPPException;

public class IntegrationPool {
	public static boolean isFinish = false;
	public static boolean isRecevie = false;
	public static boolean isEnd = false;
	public static boolean isRunning = false;
	public static Gtalk Gt;
	public static Facebook FB;
	
	private static Map<String, List<ExChatMgr>> integrationList = new HashMap<String, List<ExChatMgr>>();

	/**
	 * Startup XMPP client and login
	 * @throws IOException
	 */
	public static void init() throws IOException{
		if(isRunning)
			return;
		// 請輸入人頭帳號
		Gt = new Gtalk("testforim1@gmail.com","qa147258");
		FB = new Facebook("testforim1@gmail.com","qa147258");

		System.out.println("Login in.....");
		try {
			FB.run();
			Gt.run();
			isRunning = true;
			System.out.println("Login Finish");
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set an integration instance
	 * @param from
	 * @param to
	 * @throws IOException
	 */
	public static void SetIntegration(String from, String to) throws IOException{
		List<String> list = new ArrayList<String>();
		list.add(to);
		AddIntegration(from, list);
	}
	
	/**
	 * Set an integration instance
	 * @param from
	 * @param toList
	 * @throws IOException
	 */
	public static void SetIntegration(String from, List<String> toList) throws IOException{
		if(integrationList.containsKey(from)) {
			List<ExChatMgr> list = integrationList.get(from);
			list.clear();
			AddIntegration(from, toList);
		} else
			AddIntegration(from, toList);
	}

	/**
	 * Add receiver to a integration instance
	 * @param from
	 * @param to
	 * @throws IOException
	 */
	public static void AddIntegration(String from, String to) throws IOException{
		List<String> list = new ArrayList<String>();
		list.add(to);
		AddIntegration(from, list);	
	}
	
	/**
	 * Add receivers to a integration instance
	 * @param from
	 * @param toList
	 * @throws IOException
	 */
	public static void AddIntegration(String from, List<String> toList) throws IOException{
		from = FB.getId(from);
		if(integrationList.containsKey(from))
		{
			List<ExChatMgr> list = integrationList.get(from);
			for (String to : toList)
				list.add(new ExChatMgr(getSendSide(to), to));
		}
		else
		{
			List<ExChatMgr> list = new ArrayList<ExChatMgr>();
			for (String to : toList)
				list.add(new ExChatMgr(getSendSide(to), to));
			integrationList.put(from, list);
		}
	}

	public static void removeIntegration(String from) {
		from = FB.getId(from);
		integrationList.remove(from);
	}
	
	public static Set<String> getFriendList()
	{
		Set<String> set = new HashSet<String>();
		set.addAll(FB.getBuddyList());
		set.addAll(Gt.getBuddyList());
		return set;
	}
	/**
	 * send messages to integrated receivers
	 * @param from
	 * @param msg
	 * @throws XMPPException
	 */
	public static void sendMsg(String from, String msg) throws XMPPException {
		for( ExChatMgr mgr:integrationList.get(from) )
			mgr.sendMsg(msg);
	}
	
	/**
	 * get sending side to specified id
	 * @param to
	 * @return
	 */
	private static ChatUserInterface getSendSide(String to) {
		String domain = to.substring(to.indexOf('@')+1);
		if(domain.equalsIgnoreCase("gmail.com"))
			return Gt;
		else if(domain.equalsIgnoreCase("chat.facebook.com"))
			return FB;
		else
			return FB;
	}
	
	private static String getUID(String name) {
		if(name.indexOf('@') < 0)
			return name;
		else {
			return FB.getId(name);
		}
	}
}
