package com.looseboxes.spring.webapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

public class StreamSelectorSpring<T> extends StreamSelectorImpl<T>{

    private static final Logger log = LoggerFactory.getLogger(StreamSelectorSpring.class);

    public StreamSelectorSpring(Pageable pageable) {
        this(null, pageable);
    }
    public StreamSelectorSpring(@Nullable Example<T> example, Pageable pageable) {
        log.trace("{}, {}", example, pageable);
        this.setOffset(pageable.getOffset());
        this.setLimit(pageable.getPageSize());
        this.setComparator(new ComparatorFromSort<>(pageable.getSort()));
        if(example != null) {
            this.setFilter(new FilterFromExample<>(example));
        }
        log.trace("{}", this);
    }
}
