package com.orio.book_processing.queue.events;

public record JobCompletionEvent(Long jobId, Long userId) {

}
