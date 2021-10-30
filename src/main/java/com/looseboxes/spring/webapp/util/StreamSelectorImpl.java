package com.looseboxes.spring.webapp.util;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StreamSelectorImpl<E> implements StreamSelector<E> {

    private long offset;
    private int limit;
    private Predicate<E> filter;
    private Comparator<E> comparator;

    public StreamSelectorImpl() {
        offset = 0;
        limit = 20;
        filter = null;
        comparator = null;
    }

    @Override
    public Stream<E> select(Stream<E> stream) {
        if (limit < 1) {
            return Stream.empty();
        } else {
            if (filter != null) {
                stream = stream.filter(filter);
            }
            if (comparator != null) {
                stream = stream.sorted(comparator);
            }
            return stream.skip(offset).limit(limit);
        }
    }

    public long getOffset() {
        return offset;
    }

    public StreamSelectorImpl<E> offset(long offset) {
        setOffset(offset);
        return this;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return this.limit;
    }

    public StreamSelectorImpl<E> limit(int limit) {
        setLimit(limit);
        return this;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Predicate<E> getFilter() {
        return filter;
    }

    public StreamSelectorImpl<E> filter(Predicate<E> filter) {
        setFilter(filter);
        return this;
    }

    public void setFilter(Predicate<E> filter) {
        this.filter = filter;
    }

    public Comparator<E> getComparator() {
        return comparator;
    }

    public StreamSelectorImpl<E> comparator(Comparator<E> comparator) {
        setComparator(comparator);
        return this;
    }

    public void setComparator(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public String toString() {
        return "StreamSelectorImpl{" +
            "offset=" + offset +
            ", limit=" + limit +
            ", filter=" + filter +
            ", comparator=" + comparator +
            '}';
    }
}
