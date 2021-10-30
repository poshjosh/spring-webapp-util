package com.looseboxes.spring.webapp.util;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

/**
 * A {@link org.springframework.scheduling.Trigger Trigger} to run a task only once.
 * @author hp
 */
public class TriggerOnce implements Trigger{

    private final Date date;

    public TriggerOnce(long initialDelay, TemporalUnit unit) {
        this(Instant.now().plus(initialDelay, unit));
    }

    public TriggerOnce(Instant instant) {
        this(Date.from(instant));
    }

    public TriggerOnce(Date date) {
        this.date = date;
    }

    /**
     * Determine the next execution time according to the given trigger context.
     * @param triggerContext context object encapsulating last execution times
     * and last completion time
     * @return the next execution time as defined by the trigger,
     * or {@code null} if the trigger won't fire anymore
     */
    @Nullable
    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        Date lastExecution = triggerContext.lastScheduledExecutionTime();
        Date lastCompletion = triggerContext.lastCompletionTime();
        return lastExecution == null || lastCompletion == null ? date : null;
    }
}
