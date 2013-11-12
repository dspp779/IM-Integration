package hsnl.im.integrate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.XMPPException;

public class IntegratePool {
	public static boolean isFinish = false;
	public static boolean isRecevie = false;
	public static boolean isEnd = false;
	public static boolean isRunning = false;
	public static Gtalk Gt;
	public static Facebook FB;
	
	private static Map<String, List<ExChatMgr>> integrationList = new HashMap<String, List<ExChatMgr>>();

	public static void init() throws IOException{
		// 請輸入人頭帳號
		Gt = new Gtalk("testforim1@gmail.com","qa147258");
		FB = new Facebook("testforim1@gmail.com","qa147258");

		System.out.println("Login in.....");
		try {
			FB.run();
			Gt.run();
			System.out.println("Login Finish");
			isRunning = true;
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void SetIntegration(String from, String to) throws IOException{
		List<String> list = new ArrayList<String>();
		list.add(to);
		AddIntegration(from, list);
	}
	public static void SetIntegration(String from, List<String> toList) throws IOException{
		if(integrationList.containsKey(from)) {
			List<ExChatMgr> list = integrationList.get(from);
			list.clear();
			AddIntegration(from, toList);
		} else
			AddIntegration(from, toList);
	}

	public static void AddIntegration(String from, String to) throws IOException{
		List<String> list = new ArrayList<String>();
		list.add(to);
		AddIntegration(from, list);	
	}
	public static void AddIntegration(String from, List<String> toList) throws IOException{
		if(!isRunning)
			init();

		from = FB.getId(from);
		if(integrationList.containsKey(from))
		{
			List<ExChatMgr> list = integrationList.get(from);

			for (String to : toList) {
				ExChatMgr mgr = new ExChatMgr(getSendSide(to), to);
				list.add(mgr);
			}
		}
		else
		{
			List<ExChatMgr> list = new ArrayList<ExChatMgr>();
			for (String to : toList) {
				ExChatMgr mgr = new ExChatMgr(getSendSide(to), to);
				list.add(mgr);
			}
			integrationList.put(from, list);
		}
	}

	public static void sendMsg(String from, String msg) throws XMPPException {
		List<ExChatMgr> list = integrationList.get(from);
		for( ExChatMgr mgr:list )
		{
			mgr.sendMsg(msg);
		}
	}
	
	private static ChatUser getSendSide(String to)
	{
		if(to.indexOf('@') < 0)
			return FB;
		else {
			String domain = to.substring(to.indexOf('@')+1);
			if(domain.equalsIgnoreCase("gmail.com"))
				return Gt;
			else
				return FB;
		}
	}
}
