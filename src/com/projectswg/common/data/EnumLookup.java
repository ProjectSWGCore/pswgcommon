package com.projectswg.common.data;

import java.util.HashMap;
import java.util.Map;

public class EnumLookup<K, T extends Enum<?>> {
	
	private final Map<K, T> lookup;
	
	public EnumLookup(Class<T> c, CustomLookupAdder<K, T> adder) {
		lookup = new HashMap<>();
		for (T t : c.getEnumConstants()) {
			lookup.put(adder.getKey(t), t);
		}
	}
	
	public T getEnum(K k, T def) {
		T t = lookup.get(k);
		if (t == null)
			return def;
		return t;
	}
	
	public boolean containsEnum(K k) {
		return lookup.containsKey(k);
	}
	
	public int size() {
		return lookup.size();
	}
	
	public interface CustomLookupAdder<K, T> {
		K getKey(T t);
	}
	
}
