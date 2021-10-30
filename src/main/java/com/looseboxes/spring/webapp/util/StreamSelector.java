package com.looseboxes.spring.webapp.util;

import java.util.stream.Stream;

public interface StreamSelector<E> {
    Stream<E> select(Stream<E> stream);
}
