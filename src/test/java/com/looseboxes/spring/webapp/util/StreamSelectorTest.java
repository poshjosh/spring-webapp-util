package com.looseboxes.spring.webapp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamSelectorTest{

    private static final Logger log = LoggerFactory.getLogger(StreamSelectorTest.class);

    com.looseboxes.spring.webapp.util.DummyObject candidate1;
    com.looseboxes.spring.webapp.util.DummyObject candidate2;
    com.looseboxes.spring.webapp.util.DummyObject candidate3;

    List<com.looseboxes.spring.webapp.util.DummyObject> candidates;

    @BeforeEach
    void setup() {
        candidate1 = new com.looseboxes.spring.webapp.util.DummyObject("name1", true, 1, Instant.now().plusSeconds(1));
        candidate2 = new com.looseboxes.spring.webapp.util.DummyObject("name2", false, 2, Instant.now().plusSeconds(2));
        candidate3 = new com.looseboxes.spring.webapp.util.DummyObject("name3", true, 3, Instant.now().plusSeconds(3));
        candidates = Arrays.asList(candidate1, candidate2, candidate3);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 2}, ints = {1, 3, Integer.MAX_VALUE}) // Valid values of limit/pageSize starts at One
    void testSelectsNothing_givenFilterThatAcceptsNone(long offset, int limit) {
        Predicate<com.looseboxes.spring.webapp.util.DummyObject> filter = e -> false;
        Comparator<com.looseboxes.spring.webapp.util.DummyObject> comparator = (l, r) -> 0;
        selectionResultsTo(givenStreamSelector((int)offset, limit, filter, comparator), expectedResult((int)offset, limit, filter, comparator));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, Integer.MAX_VALUE}) // Valid values of limit/pageSize starts at One
    void testSelectsByLimit(int limit) {
        final int offset = 0;
        selectionResultsTo(givenStreamSelector(offset, limit), expectedResult(offset, limit));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2}) // Valid values of offset starts at Zero. If you want a larger offset, then add more candidates.
    void testSelectsByOffset(int offset) {
        final int limit = 1;
        selectionResultsTo(givenStreamSelector(offset, limit), expectedResult(offset, limit));
    }

    List<com.looseboxes.spring.webapp.util.DummyObject> expectedResult(int offset, int limit, Predicate<com.looseboxes.spring.webapp.util.DummyObject> filter, Comparator<com.looseboxes.spring.webapp.util.DummyObject> comparator) {
        return expectedResult(offset, limit).stream().filter(filter).sorted(comparator).collect(Collectors.toList());
    }

    List<com.looseboxes.spring.webapp.util.DummyObject> expectedResult(int offset, int limit) {
        int end = offset + limit;
        end = end > candidates.size() ? candidates.size() : end;
        return candidates.subList(offset, end);
    }

    StreamSelector<com.looseboxes.spring.webapp.util.DummyObject> givenStreamSelector(int offset, int pageSize, Predicate<com.looseboxes.spring.webapp.util.DummyObject> filter, Comparator<com.looseboxes.spring.webapp.util.DummyObject> comparator) {
        return givenStreamSelector(offset, pageSize).filter(filter).comparator(comparator);
    }

    StreamSelectorSpring<com.looseboxes.spring.webapp.util.DummyObject> givenStreamSelector(int offset, int pageSize) {
        return new StreamSelectorSpring<>(PageRequest.of(getPageNumberForOffset(offset, pageSize), pageSize));
    }

    int getPageNumberForOffset(int offset, int pageSize) {
//        return (offset / candidates.size()) + ((offset % candidates.size()) > 0 ? 1 : 0); // NOT WORKING
        return offset; // We can simply return offset for now because the pageSize is one.
    }

    <E> void selectionResultsTo(StreamSelector<com.looseboxes.spring.webapp.util.DummyObject> streamSelector, List<com.looseboxes.spring.webapp.util.DummyObject> expectedResult) {
        selectionResultsTo(candidates, streamSelector, expectedResult);
    }

    <E> void selectionResultsTo(List<E> candidates, StreamSelector<E> streamSelector, List<E> expectedResult) {

        log.debug("Selecting with: {}", streamSelector);

        List<E> result = streamSelector.select(candidates.stream()).collect(Collectors.toList());

        assertThat(result).isEqualTo(expectedResult);
    }
}
