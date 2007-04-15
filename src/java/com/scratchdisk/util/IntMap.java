/**
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.scratchdisk.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.ArrayList;

/**
 *  Hashtable-based map with integer keys that allows values to be removed 
 *  by the garbage collector.<P>
 *
 *  The algorithms used are basically the same as those
 *  in {@link java.util.HashMap}.  In particular, you 
 *  can specify a load factor and capacity to suit your
 *  needs.
 *
 *  This map does <I>not</I> allow null values.  Attempting to add a null 
 *  value to the map will raise a <Code>NullPointerException</Code>.<P>
 *
 *  This data structure is not synchronized.
 *
 *  @see java.lang.ref.Reference
 */
public class IntMap extends AbstractMap {

	protected static class Entry {
		protected int key;
		protected Entry next;
		
		/**
		 * Reference value.  Note this can never be null.
		 */
		protected Object value;

		public Entry(int key, Object value, Entry next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}

		public Object getValue() {
			return value;
		}
	}

	int entryCount;

	/**
	 *  The threshold variable is calculated by multiplying
	 *  table.length and loadFactor.  
	 *  Note: I originally marked this field as final, but then this class
	 *   didn't compile under JDK1.2.2.
	 *  @serial
	 */
	float loadFactor;

	/**
	 *  Number of mappings in this map.
	 */
	transient int size;

	/**
	 *  The hash table.  Its length is always a power of two.  
	 */
	transient Entry[] table;

	/**
	 *  When size reaches threshold, the map is resized.  
	 *  @see #resize()
	 */
	transient int threshold;

	/**
	 * Number of times this map has been modified.
	 */
	transient volatile int modCount;

	/**
	 * Cached key set. May be null if key set is never accessed.
	 */
	transient Set keySet;


    /**
	 * Cached entry set. May be null if entry set is never accessed.
	 */
	transient Set entrySet;

	/**
	 * Cached values. May be null if values() is never accessed.
	 */
	transient Collection values;

    /**
     * Constructs a new <Code>IntMap</Code>
     */
    public IntMap() {
        this(16, 0.75f);
    }

	/**
	 *  Constructs a new <Code>IntMap</Code> with the
	 *  specified load factor and initial capacity.
	 *
	 *  @param capacity  the initial capacity for the map
	 *  @param loadFactor  the load factor for the map
	 */
	public IntMap(int capacity, float loadFactor) {
		super();
		if (capacity <= 0)
			throw new IllegalArgumentException("capacity must be positive");
		if ((loadFactor <= 0.0f) || (loadFactor >= 1.0f))
			throw new IllegalArgumentException("Load factor must be greater than 0 and less than 1.");

		int initialSize = 1;
		while (initialSize < capacity)
			initialSize *= 2;

		this.table = new Entry[initialSize];
		this.loadFactor = loadFactor;
		this.threshold = (int) (initialSize * loadFactor);
	}

	/**
	 * @param key
	 * @return
	 */
	protected Object doRemove(int key) {
		int index = indexFor(key);
		Entry previous = null;
		Entry entry = table[index];
		while (entry != null) {
			if (key == entry.key) {
				if (previous == null)
					table[index] = entry.next;
				else
					previous.next = entry.next;
				this.size--;
				modCount++;
				return entry.getValue();
			}
			previous = entry;
			entry = entry.next;
		}
		return null;
	}

	/**
	 *  Converts the given hash code into an index into the
	 *  hash table.
	 */
	private int indexFor(int hash) {
		// mix the bits to avoid bucket collisions...
		hash += ~(hash << 15);
		hash ^= (hash >>> 10);
		hash += (hash << 3);
		hash ^= (hash >>> 6);
		hash += ~(hash << 11);
		hash ^= (hash >>> 16);
		return hash & (table.length - 1);
	}

    /**
     *  Returns the entry associated with the given key.
     *
     *  @param key  the key of the entry to look up
     *  @return  the entry associated with that key, or null
     *    if the key is not in this map
     */
	public Entry getEntry(int key) {
		for (Entry entry = table[indexFor(key)]; entry != null; entry = entry.next)
			if (entry.key == key)
				return entry;
		return null;
	}

	protected void purge() {
	}

    /**
	 * Returns the size of this map.
	 * 
	 * @return the size of this map
	 */
	public int size() {
		purge();
		return size;
	}

	/**
	 * Returns <Code>true</Code> if this map is empty.
	 * 
	 * @return <Code>true</Code> if this map is empty
	 */
	public boolean isEmpty() {
		purge();
		return size == 0;
	}
	
    /**
     *  Returns <Code>true</Code> if this map contains the given key.
     *
     *  @return true if the given key is in this map
     */
    public boolean containsKey(int key) {
        purge();
        Entry entry = getEntry(key);
        if (entry == null) return false;
        return entry.getValue() != null;
    }

	/**
	 *  Returns the value associated with the given key, if any.
	 *
	 *  @return the value associated with the given key, or <Code>null</Code>
	 *   if the key maps to no value
	 */
	public Object get(int key) {
        purge();
        Entry entry = getEntry(key);
        if (entry == null) return null;
        return entry.getValue();
	}

	/**
	 * java.util.Map compatible version of get
	 */
	public Object get(Object key) {
		return key instanceof Number ? get(((Number) key).intValue()) : null;
	}

	/**
	 * Constructs a new table entry for the given data
	 * 
	 * @param key The entry key
	 * @param value The entry value
	 * @param next The next value in the entry's collision chain
	 * @return The new table entry
	 */
	protected Entry createEntry(int key, Object value, Entry next) {
		return new Entry(key, value, next);
	}
	
	/**
	 * Associates the given key with the given value.
	 * <P>
	 * Neither the key nor the value may be null.
	 * 
	 * @param key the key of the mapping
	 * @param value the value of the mapping
	 * @throws NullPointerException if either the key or value is null
	 */
	public Object put(int key, Object value) {
		if (value == null)
			throw new NullPointerException("null values not allowed");

		purge();

		if (size + 1 > threshold)
			resize();

		int index = indexFor(key);
		Entry previous = null;
		Entry entry = table[index];
		while (entry != null) {
			if (key == entry.key) {
				Object result = entry.getValue();
				if (previous == null)
					table[index] = createEntry(key, value, entry.next);
				else
					previous.next = createEntry(key, value, entry.next);
				return result;
			}
			previous = entry;
			entry = entry.next;
		}
		this.size++;
		modCount++;
		table[index] = createEntry(key, value, table[index]);
		entryCount++;
		return null;
	}

	public Object put(Object key, Object value) {
		if (key instanceof Number)
			return put(((Number) key).intValue(), value);
		return null;
	}

	/**
	 * Removes the key and its associated value from this map.
	 * 
	 * @param key the key to remove
	 * @return the value associated with that key, or null if the key was not in
	 * the map
	 */
	public Object remove(int key) {
		purge();
		return doRemove(key);
	}
	
	public Object remove(Object key) {
		if (key instanceof Number)
			return remove(((Number) key).intValue());
		return null;
	}

    /**
	 * Clears this map.
	 */
	public void clear() {
		Arrays.fill(table, null);
		size = 0;
	}

	/**
	 *  Resizes this hash table by doubling its capacity.
	 *  This is an expensive operation, as entries must
	 *  be copied from the old smaller table to the new 
	 *  bigger table.
	 */
	private void resize() {
		Entry[] old = table;
		table = new Entry[old.length * 2];

		for (int i = 0; i < old.length; i++) {
			Entry next = old[i];
			while (next != null) {
				Entry entry = next;
				next = next.next;
				int index = indexFor(entry.key);
				entry.next = table[index];
				table[index] = entry;
			}
			old[i] = null;
		}
		threshold = (int) (table.length * loadFactor);
	}

    /**
	 * Returns a set view of this map's entries.
	 * 
	 * @return a set view of this map's entries
	 */
	public Set entrySet() {
		if (entrySet != null)
			return entrySet;
		entrySet = new AbstractSet() {
			public int size() {
				return IntMap.this.size();
			}

			public void clear() {
				IntMap.this.clear();
			}

			public boolean contains(Object o) {
				if (o != null && o instanceof Map.Entry) {
					Map.Entry e = (Map.Entry) o;
					Object k = e.getKey();
					if (k instanceof Number) {
						Entry e2 = getEntry(((Number) k).intValue());
						return (e2 != null) && e.equals(e2);
					}
				}
				return false;
			}

			public boolean remove(Object o) {
				boolean r = contains(o);
				if (r) {
					Map.Entry e = (Map.Entry) o;
					Object k = e.getKey();
					if (k instanceof Number)
						IntMap.this.remove(((Number) k).intValue());
					else
						r = false;
				}
				return r;
			}

			public Iterator iterator() {
				return new EntryIterator();
			}

			public Object[] toArray() {
				return toArray(new Object[0]);
			}

			public Object[] toArray(Object[] arr) {
				ArrayList list = new ArrayList();
				Iterator iterator = iterator();
				while (iterator.hasNext()) {
					Entry e = (Entry) iterator.next();
					list.add(new DefaultMapEntry(e.key, e.getValue()));
				}
				return list.toArray(arr);
			}
		};
		return entrySet;
	}

	/**
	 *  Returns a set view of this map's keys.
	 *
	 *  @return a set view of this map's keys
	 */
	public Set keySet() {
		if (keySet != null)
			return keySet;
		keySet = new AbstractSet() {
			public int size() {
				return size;
			}

			public Iterator iterator() {
				return new KeyIterator();
			}

			public boolean contains(Object o) {
				return o instanceof Number ? containsKey(((Number) o).intValue()) : false;
			}

			public boolean remove(Object o) {
				return o instanceof Number ? IntMap.this.remove(((Number) o).intValue()) != null : false;
			}

			public void clear() {
				IntMap.this.clear();
			}

		};
		return keySet;
	}

	/**
	 *  Returns a collection view of this map's values.
	 *
	 *  @return a collection view of this map's values.
	 */
	public Collection values() {
		if (values != null)
			return values;
		values = new AbstractCollection() {
			public int size() {
				return size;
			}

			public void clear() {
				IntMap.this.clear();
			}

			public Iterator iterator() {
				return new ValueIterator();
			}
		};
		return values;
	}

	private class EntryIterator implements Iterator {
		// These fields keep track of where we are in the table.
		int index;
		Entry entry;
		Entry previous;

		// These Object fields provide hard references to the
		// current and next entry; this assures that if hasNext()
		// returns true, next() will actually return a valid element.
		int currentKey, nextKey;
		Object currentValue, nextValue;

		int expectedModCount;

		public EntryIterator() {
			index = (size() != 0 ? table.length : 0);
			// have to do this here!  size() invocation above
			// may have altered the modCount.
			expectedModCount = modCount;
		}

		public boolean hasNext() {
			checkMod();
			while (nextNull()) {
				Entry e = entry;
				int i = index;
				while ((e == null) && (i > 0)) {
					i--;
					e = table[i];
				}
				entry = e;
				index = i;
				if (e == null) {
					currentKey = -1;
					currentValue = null;
					return false;
				}
				nextKey = e.key;
				nextValue = e.getValue();
				if (nextNull())
					entry = entry.next;
			}
			return true;
		}

		private void checkMod() {
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}

		private boolean nextNull() {
			return (nextKey == -1) || (nextValue == null);
		}

		protected Entry nextEntry() {
			checkMod();
			if (nextNull() && !hasNext())
				throw new NoSuchElementException();
			previous = entry;
			entry = entry.next;
			currentKey = nextKey;
			currentValue = nextValue;
			nextKey = -1;
			nextValue = null;
			return previous;
		}

		public Object next() {
			return nextEntry();
		}

		public void remove() {
			checkMod();
			if (previous == null)
				throw new IllegalStateException();
			IntMap.this.remove(currentKey);
			previous = null;
			currentKey = -1;
			currentValue = null;
			expectedModCount = modCount;
		}

	}

	private class ValueIterator extends EntryIterator {
		public Object next() {
			return nextEntry().getValue();
		}
	}

	private class KeyIterator extends EntryIterator {
		public Object next() {
			return new Integer(nextEntry().key);
		}
	}

	/** A default implementation of {@link java.util.Map.Entry}
	 *
	 * @since 1.0
	 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
	 * @author <a href="mailto:mas@apache.org">Michael A. Smith</a>
	 */

	private class DefaultMapEntry implements Map.Entry {
		private int key;
		private Object value;

		/**
		 *  Constructs a new <Code>DefaultMapEntry</Code> with a null key
		 *  and null value.
		 */
		DefaultMapEntry() {
		}

		/**
		 *  Constructs a new <Code>DefaultMapEntry</Code> with the given
		 *  key and given value.
		 *
		 *  @param key  the key for the entry, may be null
		 *  @param value  the value for the entyr, may be null
		 */
		DefaultMapEntry(int key, Object value) {
			this.key = key;
			this.value = value;
		}

		/**
		 *  Implemented per API documentation of
		 *  {@link java.util.Map.Entry#equals(java.lang.Object)}
		 **/
		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (o == this)
				return true;

			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry e2 = (Map.Entry) o;
			return ((getKey() == null ? e2.getKey() == null : getKey().equals(e2.getKey())) && (getValue() == null ? e2.getValue() == null
				: getValue().equals(e2.getValue())));
		}

		/**
		 *  Implemented per API documentation of
		 *  {@link java.util.Map.Entry#hashCode()}
		 **/
		public int hashCode() {
			return ((getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode()));
		}

		/**
		 *  Returns the key.
		 *
		 *  @return the key
		 */
		public Object getKey() {
			return new Integer(key);
		}

		/**
		 *  Returns the value.
		 *
		 *  @return the value
		 */
		public Object getValue() {
			return value;
		}

		/**
		 *  Sets the key.  This method does not modify any map.
		 *
		 *  @param key  the new key
		 */
		public void setKey(int key) {
			this.key = key;
		}

		/** Note that this method only sets the local reference inside this object and
		 * does not modify the original Map.
		 *
		 * @return the old value of the value
		 * @param value the new value
		 */
		public Object setValue(Object value) {
			Object answer = this.value;
			this.value = value;
			return answer;
		}

	}
}