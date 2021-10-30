package com.looseboxes.spring.webapp.util;

/**
 * @author hp
 */
public interface ReadWriteSyncList<E>{

    E get(int index);

    default boolean isEmpty() {
        return size() == 0;
    }

    int size();

    boolean add(E e);

    E set(int index, E element);

    E remove(int index);

    int indexOf(Object o);
}
