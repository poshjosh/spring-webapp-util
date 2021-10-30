package com.looseboxes.spring.webapp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author hp
 */
public class ReadWriteSyncListImpl<E> implements ReadWriteSyncList<E>{

    private final List<E> delegate;

    private final ReadWriteLock lock;

    public ReadWriteSyncListImpl() {
        delegate = new ArrayList<>();
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public E get(int index) {
        try{
            lock.readLock().lock();
            return delegate.get(index);
        }finally{
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        try{
            lock.readLock().lock();
            return delegate.size();
        }finally{
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean add(E e) {
        try{
            lock.writeLock().lock();
            return delegate.add(e);
        }finally{
            lock.writeLock().unlock();
        }
    }

    @Override
    public E set(int index, E element) {
        try{
            lock.writeLock().lock();
            return delegate.set(index, element);
        }finally{
            lock.writeLock().unlock();
        }
    }

    @Override
    public E remove(int index) {
        try{
            lock.writeLock().lock();
            return delegate.remove(index);
        }finally{
            lock.writeLock().unlock();
        }
    }

    @Override
    public int indexOf(Object o) {
        try{
            lock.readLock().lock();
            return delegate.indexOf(o);
        }finally{
            lock.readLock().unlock();
        }
    }
}
