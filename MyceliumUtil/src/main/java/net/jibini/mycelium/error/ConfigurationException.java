package net.jibini.mycelium.error;

public class ConfigurationException extends RuntimeException
{
	public ConfigurationException(String message) { super(message); }
	
	public ConfigurationException(String message, Throwable t) { super(message, t); }
	
	public ConfigurationException(Throwable t) { super(t); }
}
