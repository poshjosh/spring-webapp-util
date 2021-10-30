package com.looseboxes.spring.webapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class DelayComputer {

    private final Logger log = LoggerFactory.getLogger(DelayComputer.class);

    public boolean hasDelay(Instant startTime, Instant endTime) {

        final long delay = computeDelayTillStart(Instant.now(), startTime, endTime, Long.MIN_VALUE);

        return delay != Long.MIN_VALUE;
    }


    /**
     * Compute delay starting from offset up till the specified startTime, also taking into consideration endTime.
     * @param offset The offset to start counting the delay from.
     * @param startTime The start time of an event/period for which we want a delay till that time.
     * @param endTime The end time.
     * @param resultIfNone The delay to return if none could be computed.
     * @return The delay in milliseconds.
     */
    public long computeDelayTillStart(Instant offset, Instant startTime, Instant endTime, long resultIfNone) {

        final long diff = startTime.toEpochMilli() - offset.toEpochMilli();

        final long delay;

        if(startTime.isAfter(endTime)) {

            throw new IllegalArgumentException("starTime is later than endTime");

        }else if(startTime.compareTo(endTime) == 0) {

            delay = resultIfNone;

        }else if(endTime.isBefore(offset)) { // auction has already ended

            delay = resultIfNone;

        }else {

            delay = diff;
        }

        log.debug("Delay till start: {} millis, result: {} millis", diff, delay);

        return delay;
    }
}
