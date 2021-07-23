package com.minlessika.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Map that doesn't allow null value. It will simply skip it.
 * 
 * @author Olivier B. OURA (baudoliver7@gmail.com)
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public final class CleanMap<K, V> implements Map<K, V> {

	private final Map<K, V> origin;
	
	public CleanMap() {
		this.origin = new HashMap<>();
	}
	
	@Override
	public int size() {
		return origin.size();
	}

	@Override
	public boolean isEmpty() {
		return origin.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return origin.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return origin.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return origin.get(key);
	}

	@Override
	public V put(K key, V value) {
		if(value == null)
			return value;	// skip	
		return origin.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return origin.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		final Map<K, V> copy = new HashMap<>(m);
		copy.values().removeIf(Objects::isNull); // remove null object
		origin.putAll(copy);
	}

	@Override
	public void clear() {
		origin.clear();
	}

	@Override
	public Set<K> keySet() {
		return origin.keySet();
	}

	@Override
	public Collection<V> values() {
		return origin.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return origin.entrySet();
	}
	
	public CleanMap<K, V> add(K k, V v) {
		put(k, v);
		return this;
	}

}
