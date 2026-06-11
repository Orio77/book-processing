package com.orio.book_processing.queue.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class JobEventsTest {

    @Test
    void jobCreationEvent_exposesJobIdAndEqualsByValue() {
        JobCreationEvent a = new JobCreationEvent(100L);
        JobCreationEvent same = new JobCreationEvent(100L);
        JobCreationEvent other = new JobCreationEvent(101L);

        assertEquals(100L, a.jobId());
        assertEquals(same, a);
        assertEquals(same.hashCode(), a.hashCode());
        assertNotEquals(other, a);
    }

    @Test
    void jobCompletionEvent_exposesJobIdAndEqualsByValue() {
        JobCompletionEvent a = new JobCompletionEvent(200L);
        JobCompletionEvent same = new JobCompletionEvent(200L);

        assertEquals(200L, a.jobId());
        assertEquals(same, a);
        assertEquals(same.hashCode(), a.hashCode());
    }
}
