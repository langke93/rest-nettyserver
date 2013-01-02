package org.langke.common.bdb;

import java.util.List;

public interface BrekeleyDB {
    public Object poll() ;
    public List<?> poll(int limit) ;
    public void put(Object key,Object value);
    public Object get(Object key);
	public List<?> get(int limit);
    public Object delete(Object key);
	public int size();
	public boolean isEmpty();
	public boolean contains(Object key);
	public void clear();
	public void close();
}
