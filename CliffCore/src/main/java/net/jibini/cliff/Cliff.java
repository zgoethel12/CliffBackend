package net.jibini.cliff;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.plugin.PluginManager;
import net.jibini.cliff.util.StreamUtil;

public class Cliff
{
	private static Cliff INSTANCE = null;
	private static Object KILL_SWITCH = new Object();
	
	private Cliff()
	{}
	
	public static Cliff create()
	{
		Cliff result = new Cliff();
		INSTANCE = result;
		return result;
	}
	
	public static Cliff getInstance()
	{
		if (INSTANCE == null)
			Cliff.create();
		return INSTANCE;
	}
	
	public static void kill()
	{
		synchronized (KILL_SWITCH)
		{
			KILL_SWITCH.notify();
		}
	}
	
	public static void main(String[] args)
	{
		Thread cliffThread = new Thread(() ->
		{
			Cliff cliff = Cliff.getInstance();
			cliff.start();
		});
		
		cliffThread.setName("CliffThread");
		cliffThread.start();
		
		synchronized (KILL_SWITCH)
		{
			try
			{
				KILL_SWITCH.wait();
			} catch (InterruptedException ex)
			{}
		}
		
		System.exit(0);
	}
	
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private PluginManager pluginManager = PluginManager.create();
	
	public JSONObject getCliffManifest()
	{
		try
		{
			InputStream manifestStream = getClass().getClassLoader().getResourceAsStream("cliff.json");
			JSONObject cliffManifest = new JSONObject(StreamUtil.readTextFile(manifestStream));
			return cliffManifest;
		} catch (IOException ex)
		{
			log.error("Failed to load application manifest", ex);
			return null;
		}
	}
	
	public void start()
	{
		JSONObject cliffManifest = getCliffManifest();
		log.info("Starting " + cliffManifest.getString("app-name") + " " + cliffManifest.getString("version"));
		
		//TODO: Load plugins from file
		pluginManager.notifyPluginStart();
	}
	
	public PluginManager getPluginManager()  { return pluginManager; }
}