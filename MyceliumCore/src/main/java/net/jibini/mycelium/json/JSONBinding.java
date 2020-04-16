package net.jibini.mycelium.json;

import net.jibini.mycelium.map.KeyValueMap;
import net.jibini.mycelium.map.VariedTypeMap;

public interface JSONBinding<K> extends VariedTypeMap<K>, KeyValueMap<K, Object>
{
	String toString();
	
	String toString(int indentFactor);
}
