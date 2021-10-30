package com.looseboxes.spring.webapp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DelayComputerTest {

    private final long periodInSeconds = 10; // period between startTime and endTime
    private final long resultIfNone = -1;
    private Instant currentTime; // This is the offsetTime we use for this test
    private Instant beforeCurrentTime;
    private Instant afterCurrentTime;

    private DelayComputer instance;

    @BeforeEach void initTest() {
        currentTime = Instant.now();
        beforeCurrentTime = currentTime.minusSeconds(periodInSeconds);
        afterCurrentTime = currentTime.plusSeconds(periodInSeconds);
        instance = new DelayComputer();
    }

    @Test void givenStartTimeAfterCurrentTime_shouldReturnPositiveNumber() {
        computeWithExpectation(afterCurrentTime, afterCurrentTime.plusSeconds(periodInSeconds), computeDelayTill(afterCurrentTime));
    }

    @Test void givenStartTimeBeforeCurrentTime_shouldReturnDefault() {
        computeWithExpectation(beforeCurrentTime, currentTime, computeDelayTill(beforeCurrentTime));
    }

    @Test void givenEndTimeBeforeCurrentTime_shouldReturnDefault() {
        computeWithExpectation(beforeCurrentTime.minusSeconds(periodInSeconds), beforeCurrentTime, resultIfNone);
    }

    @Test void givenStartTimeIsEqualToEndTime_shouldReturnDefault() {
        computeWithExpectation(afterCurrentTime, afterCurrentTime, resultIfNone);
    }

    @Test void givenStartTimeIsGreaterThanEndTime_shouldThrowException() {
        assertThatThrownBy(() -> computeWithExpectation(afterCurrentTime.plusSeconds(periodInSeconds), afterCurrentTime, resultIfNone));
    }

    @Test void givenStartTimeIsEqualToOffsetTime_shouldReturnZero() {
        computeWithExpectation(currentTime, afterCurrentTime, 0);
    }

    private long computeDelayTill(Instant startTime) {
        return startTime.toEpochMilli() - currentTime.toEpochMilli();
    }

    void computeWithExpectation(Instant startTime, Instant endTime, long expectedResult) {
        long result = instance.computeDelayTillStart(currentTime, startTime, endTime, resultIfNone);
        assertThat(result).isEqualTo(expectedResult);
    }
}
