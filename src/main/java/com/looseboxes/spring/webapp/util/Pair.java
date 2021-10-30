package com.looseboxes.spring.webapp.util;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * A key-value pair
 *
 * equals, hashcode and compareTo methods only operate on this pair's key
 * @param <K> The type of the key
 * @param <V> The type of the value
 */
public class Pair<K, V> implements Serializable, Map.Entry<K, V>, Comparable<Pair<K, V>> {

    private static final long serialVersionUID = 4138329143949025157L;

    private K key;
    private V value;

    public Pair() {}

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(Pair<K, V> o) {
        K lhs = this.key;
        K rhs = o.key;
        if (lhs == rhs) {
            return 0;
        }
        if (lhs == null) {
            return -1;
        }
        if (rhs == null) {
            return 1;
        }
        if(lhs instanceof Comparable) {
            return ((Comparable)lhs).compareTo(rhs);
        }else if(rhs instanceof Comparable) {
            return ((Comparable)rhs).compareTo(lhs);
        }else{
            return 0;
        }
    }

    @Override
    public K getKey() {
        return key;
    }

    public K setKey(K key) {
        K existingKey = this.key;
        this.key = key;
        return existingKey;
    }

    public Pair<K, V> key(K key) {
        setKey(key);
        return this;
    }

    public Pair<K, V> withKey(K key) {
        return new Pair(key, value);
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V existingValue = this.value;
        this.value = value;
        return existingValue;
    }

    public Pair<K, V> value(V value) {
        setValue(value);
        return this;
    }

    public Pair<K, V> withValue(V value) {
        return new Pair(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return key.equals(pair.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "Pair{" + key + '=' + value + '}';
    }
}
