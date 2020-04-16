package net.jibini.mycelium.network;

import java.io.IOException;

import org.json.JSONObject;
import org.slf4j.Logger;

import net.jibini.mycelium.Mycelium;
import net.jibini.mycelium.api.RequestHandler;
import net.jibini.mycelium.conf.ConfigFile;
import net.jibini.mycelium.plugin.AbstractSpore;
import net.jibini.mycelium.plugin.PluginManager;
import net.jibini.mycelium.routing.AsyncPatch;
import net.jibini.mycelium.routing.Patch;
import net.jibini.mycelium.routing.RequestRouter;
import net.jibini.mycelium.routing.StitchLink;

public class NetworkingSpore extends AbstractSpore
{
	private Logger log;
	
	private ConfigFile networkConfig;
	private RequestRouter router;
	
	private void createConfigs() throws IOException
	{
//			networkConfig = new ConfigFile()
//					.at("config/network.json")
//					.load()
//					
//					.defaultValue("tick-millis", 500)
//					
//					.pushNode("node-bind")
//					.defaultValue("address", "0.0.0.0")
//					.defaultValue("port", 25605)
//					.popNode()
//					
//					.pushNode("link")
//					.defaultValue("max-connections", 100)
//					.defaultValue("persistence-ticks", 10)
//					.defaultValue("close-timeout-ticks", 2)
//					.popNode()
//					
//					.defaultValue("redirects", new JSONArray()
//							.put(new JSONObject()
//									.put("target", "ExampleTarget")
//									.put("address", "127.0.0.1")
//									.put("port", 25605)))
//					
//					.popNode()
//					.write()
//					.close();
			
			System.out.println("YOYOYO" + networkConfig.toString());
	}
	
	@Override
	public void create(PluginManager master, JSONObject manifest, StitchLink uplink)
	{
		log = getLogger();
		router = master.getPluginRouter();
		
		try
		{
			log.debug("Creating default configs");
			createConfigs();
		} catch (IOException ex)
		{
			throw new RuntimeException("Failed to create configs", ex);
		}
		
		super.create(master, manifest, uplink);
	}
	
	public void registerRedirect(String target, String address, int port)
	{
		Patch redirect = AsyncPatch.create();
		router.registerEndpoint(target, redirect.getDownstream());
		//TODO: Throw patch upstream to network handling
		log.info("Registered network redirect from target '" + target + "' to node '" + address + ":" + port + "'");
	}
	
	@Override
	public void registerRequests(RequestHandler requestHandler)
	{
		requestHandler.attachRequestCallback("RegisterRedirectRequest", (s, r) ->
		{
			registerRedirect(r.getBody().getString("target"), r.getBody().getString("address"), r.getBody().getInt("port"));
		});
		
		JSONObject redirects = (JSONObject)networkConfig.value("redirects");
		for (String target : redirects.keySet())
			try
			{
				JSONObject t = redirects.getJSONObject(target);
				registerRedirect(target, t.getString("address"), t.getInt("port"));
			} catch (Throwable t)
			{
				log.error("Failed to register redirect for '" + target + "'", t);
			}
	}

	@Override
	public void start()
	{
		
	}
	
	// Test entry point
	public static void main(String[] args)
	{
		try
		{
			NetworkingSpore instance = new NetworkingSpore();
			Mycelium.getInstance().getPluginManager().registerPlugin(instance, PluginManager.getPluginManifest(instance.getClass().getClassLoader()));
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		new Thread(() ->
		{
			try
			{
				Thread.sleep(15000);
				System.err.println("Test period timeout");
				
				Mycelium.kill();
			} catch (InterruptedException ex)
			{ }
		}).start();
		
		Mycelium.main(args);
	}
}
