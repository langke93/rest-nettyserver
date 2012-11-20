package org.langke.common.server;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/*
 * @copyright (c) langke 2011 
 * @author langke    2011-5-1 
 */
@SuppressWarnings({"unused","unchecked","rawtypes"})
public class PathTrie<T> {
	private final TrieNode<T> root;
	private final Pattern pattern;
	private T rootValue;

	public PathTrie() {
		this("/", "*");
	}

	public PathTrie(String separator, String wildcard) {
		pattern = Pattern.compile(separator);
		root = new TrieNode<T>(separator, null, null, wildcard);
	}

	public static class TrieNode<T> {
		private transient String key;
		private transient T value;
		private boolean isWildcard;
		private final String wildcard;

		private transient String namedWildcard;

		private Map<String, TrieNode<T>> children;

		private final TrieNode<T> parent;

		public TrieNode(String key, T value, TrieNode<T> parent, String wildcard) {
			this.key = key;
			this.wildcard = wildcard;
			this.isWildcard = (key.equals(wildcard));
			this.parent = parent;
			this.value = value;
			this.children = Collections.emptyMap();
			if (isNamedWildcard(key)) {
				namedWildcard = key.substring(key.indexOf('{') + 1,
						key.indexOf('}'));
			} else {
				namedWildcard = null;
			}
		}

		public void updateKeyWithNamedWildcard(String key) {
			this.key = key;
			namedWildcard = key.substring(key.indexOf('{') + 1,
					key.indexOf('}'));
		}

		public boolean isWildcard() {
			return isWildcard;
		}

		public synchronized void addChild(TrieNode<T> child) {
			Map m = new ConcurrentHashMap(children);
			m.put(child.key, child);
			children = Collections.unmodifiableMap(m);

		}

		public TrieNode getChild(String key) {
			return children.get(key);
		}

		public synchronized void insert(String[] path, int index, T value) {
			if (index >= path.length)
				return;

			String token = path[index];
			String key = token;
			if (isNamedWildcard(token)) {
				key = wildcard;
			}
			TrieNode<T> node = children.get(key);
			if (node == null) {
				if (index == (path.length - 1)) {
					node = new TrieNode<T>(token, value, this, wildcard);
				} else {
					node = new TrieNode<T>(token, null, this, wildcard);
				}
				Map m = new ConcurrentHashMap(children);
				m.put(key, node);
				children = Collections.unmodifiableMap(m);

			} else {
				if (isNamedWildcard(token)) {
					node.updateKeyWithNamedWildcard(token);
				}

				// in case the target(last) node already exist but without a
				// value
				// than the value should be updated.
				if (index == (path.length - 1)) {
					assert (node.value == null || node.value == value);
					if (node.value == null) {
						node.value = value;
					}
				}
			}

			node.insert(path, index + 1, value);
		}

		private boolean isNamedWildcard(String key) {
			return key.indexOf('{') != -1 && key.indexOf('}') != -1;
		}

		private String namedWildcard() {
			return namedWildcard;
		}

		private boolean isNamedWildcard() {
			return namedWildcard != null;
		}

		public T retrieve(String[] path, int index, Map<String, String> params) {
			if (index >= path.length)
				return null;

			String token = path[index];
			TrieNode<T> node = children.get(token);
			boolean usedWildcard = false;
			if (node == null) {
				node = children.get(wildcard);
				if (node == null) {
					return null;
				} else {
					usedWildcard = true;
					if (params != null && node.isNamedWildcard()) {
						put(params, node.namedWildcard(), token);
					}
				}
			}

			if (index == (path.length - 1)) {
				return node.value;
			}

			T res = node.retrieve(path, index + 1, params);
			if (res == null && !usedWildcard) {
				node = children.get(wildcard);
				if (node != null) {
					if (params != null && node.isNamedWildcard()) {
						put(params, node.namedWildcard(), token);
					}
					res = node.retrieve(path, index + 1, params);
				}
			}

			return res;
		}
	}

	public void insert(String path, T value) {
		String[] strings = pattern.split(path);
		if (strings.length == 0) {
			rootValue = value;
			return;
		}
		int index = 0;
		// supports initial delimiter.
		if (strings.length > 0 && strings[0].isEmpty()) {
			index = 1;
		}
		root.insert(strings, index, value);
	}

	public T retrieve(String path) {
		return retrieve(path, null);
	}

	public T retrieve(String path, Map<String, String> params) {
		if (path.length() == 0) {
			return rootValue;
		}
		String[] strings = pattern.split(path);
		if (strings.length == 0) {
			return rootValue;
		}
		int index = 0;
		// supports initial delimiter.
		if (strings.length > 0 && strings[0].isEmpty()) {
			index = 1;
		}
		return root.retrieve(strings, index, params);
	}

	private static void put(Map<String, String> params, String key, String value) {
		params.put(key, value);
	}
}
