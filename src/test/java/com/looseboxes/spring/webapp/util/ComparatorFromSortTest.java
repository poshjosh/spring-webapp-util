package com.looseboxes.spring.webapp.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ComparatorFromSortTest {

    @Test
    void testSortMany() {

        List<com.looseboxes.spring.webapp.util.DummyObject> toSort = new ArrayList();
        for(int i=0; i<100; i++) {
            int n = (i + 1);
            com.looseboxes.spring.webapp.util.DummyObject dummyObject = new com.looseboxes.spring.webapp.util.DummyObject("name", false, n, Instant.now().plusSeconds(n));
            toSort.add(dummyObject);
        }

        List<com.looseboxes.spring.webapp.util.DummyObject> expectedResult = new ArrayList<>(toSort);

        Collections.shuffle(toSort);

        testSort(toSort, expectedResult);
    }

    @Test
    void testSortByStringField() {

        com.looseboxes.spring.webapp.util.DummyObject candidate1 = new com.looseboxes.spring.webapp.util.DummyObject("name1", true, 1, Instant.now().plusSeconds(1));
        com.looseboxes.spring.webapp.util.DummyObject candidate2 = new com.looseboxes.spring.webapp.util.DummyObject("name2", false, 2, Instant.now().plusSeconds(2));
        com.looseboxes.spring.webapp.util.DummyObject candidate3 = new com.looseboxes.spring.webapp.util.DummyObject("name3", true, 3, Instant.now().plusSeconds(3));

        List<com.looseboxes.spring.webapp.util.DummyObject> toSort = Arrays.asList(candidate3, candidate1, candidate2);

        List<com.looseboxes.spring.webapp.util.DummyObject> expectedResult = Arrays.asList(candidate1, candidate2, candidate3);

        testSort(toSort, expectedResult);
    }

    @Test
    void testSortByBooleanField() {

        com.looseboxes.spring.webapp.util.DummyObject candidate1 = new com.looseboxes.spring.webapp.util.DummyObject("name", true, 1, Instant.now().plusSeconds(1));
        com.looseboxes.spring.webapp.util.DummyObject candidate2 = new com.looseboxes.spring.webapp.util.DummyObject("name", false, 2, Instant.now().plusSeconds(2));
        com.looseboxes.spring.webapp.util.DummyObject candidate3 = new com.looseboxes.spring.webapp.util.DummyObject("name", true, 3, Instant.now().plusSeconds(3));

        List<com.looseboxes.spring.webapp.util.DummyObject> toSort = Arrays.asList(candidate3, candidate1, candidate2);

        List<com.looseboxes.spring.webapp.util.DummyObject> expectedResult = Arrays.asList(candidate2, candidate1, candidate3);

        testSort(toSort, expectedResult);
    }

    @Test
    void testSortByIntegerField() {

        com.looseboxes.spring.webapp.util.DummyObject candidate1 = new com.looseboxes.spring.webapp.util.DummyObject("name", false, 3, Instant.now().plusSeconds(1));
        com.looseboxes.spring.webapp.util.DummyObject candidate2 = new com.looseboxes.spring.webapp.util.DummyObject("name", false, 2, Instant.now().plusSeconds(2));
        com.looseboxes.spring.webapp.util.DummyObject candidate3 = new com.looseboxes.spring.webapp.util.DummyObject("name", false, 1, Instant.now().plusSeconds(3));

        List<com.looseboxes.spring.webapp.util.DummyObject> toSort = Arrays.asList(candidate3, candidate1, candidate2);

        List<com.looseboxes.spring.webapp.util.DummyObject> expectedResult = Arrays.asList(candidate3, candidate2, candidate1);

        testSort(toSort, expectedResult);
    }

    @Test
    void testByInstantField() {

        com.looseboxes.spring.webapp.util.DummyObject candidate1 = new com.looseboxes.spring.webapp.util.DummyObject("name", false, 1, Instant.now().plusSeconds(3));
        com.looseboxes.spring.webapp.util.DummyObject candidate2 = new com.looseboxes.spring.webapp.util.DummyObject("name", false, 1, Instant.now().plusSeconds(1));
        com.looseboxes.spring.webapp.util.DummyObject candidate3 = new com.looseboxes.spring.webapp.util.DummyObject("name", false, 1, Instant.now().plusSeconds(2));

        List<com.looseboxes.spring.webapp.util.DummyObject> toSort = Arrays.asList(candidate3, candidate1, candidate2);

        List<com.looseboxes.spring.webapp.util.DummyObject> expectedResult = Arrays.asList(candidate2, candidate3, candidate1);

        testSort(toSort, expectedResult);
    }

    void testSort(List<com.looseboxes.spring.webapp.util.DummyObject> toSort, List<com.looseboxes.spring.webapp.util.DummyObject> expectedResult) {

        Sort sort = Sort.by("name", "disabled", "stat", "timeCreated");

        Collections.sort(toSort, new ComparatorFromSort<>(sort));

        assertThat(toSort).isEqualTo(expectedResult);
    }
}
