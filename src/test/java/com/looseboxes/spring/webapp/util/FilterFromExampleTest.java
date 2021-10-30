package com.looseboxes.spring.webapp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterFromExampleTest{

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

    @Test
    void testFilterByMultipleFields() {
        this.testFilter(candidates, candidate1, Arrays.asList(candidate1));
    }

    @Test
    void testFiltersByNoField() {

        com.looseboxes.spring.webapp.util.DummyObject params = new com.looseboxes.spring.webapp.util.DummyObject(null, null, null, null);

        this.testFilter(candidates, params, candidates);
    }

    @Test
    void testFilterByStringField() {

        com.looseboxes.spring.webapp.util.DummyObject expectedResult = candidate3;

        com.looseboxes.spring.webapp.util.DummyObject params = new com.looseboxes.spring.webapp.util.DummyObject(expectedResult.getName(), null, null, null);

        this.testFilter(candidates, params, Arrays.asList(expectedResult));
    }

    @Test
    void testFilterByBooleanField() {

        com.looseboxes.spring.webapp.util.DummyObject expectedResult = candidate2;

        com.looseboxes.spring.webapp.util.DummyObject params = new com.looseboxes.spring.webapp.util.DummyObject(null, expectedResult.getDisabled(), null, null);

        this.testFilter(candidates, params, Arrays.asList(expectedResult));
    }

    @Test
    void testFilterByIntegerField() {

        com.looseboxes.spring.webapp.util.DummyObject expectedResult = candidate1;

        com.looseboxes.spring.webapp.util.DummyObject params = new com.looseboxes.spring.webapp.util.DummyObject(null, null, expectedResult.getStat(), null);

        this.testFilter(candidates, params, Arrays.asList(expectedResult));
    }

    @Test
    void testFilterByInstantField() {

        com.looseboxes.spring.webapp.util.DummyObject expectedResult = candidate1;

        com.looseboxes.spring.webapp.util.DummyObject params = new com.looseboxes.spring.webapp.util.DummyObject(null, null, null, expectedResult.getTimeCreated());

        this.testFilter(candidates, params, Arrays.asList(expectedResult));
    }

    void testFilter(List<com.looseboxes.spring.webapp.util.DummyObject> toFilter, com.looseboxes.spring.webapp.util.DummyObject params, List<com.looseboxes.spring.webapp.util.DummyObject> expectedResult) {

        Predicate<com.looseboxes.spring.webapp.util.DummyObject> filter = new FilterFromExample<>(Example.of(params));

        List<com.looseboxes.spring.webapp.util.DummyObject> result = toFilter.stream().filter(filter).collect(Collectors.toList());

        assertThat(result).isEqualTo(expectedResult);
    }
}
