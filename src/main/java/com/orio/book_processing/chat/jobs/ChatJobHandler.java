package com.orio.book_processing.chat.jobs;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.chat.ChatWorkflowService;
import com.orio.book_processing.chat.dtos.PDFChatRequest;
import com.orio.book_processing.queue.Job.JobType;
import com.orio.book_processing.queue.JobHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatJobHandler implements JobHandler {

    private final ObjectMapper objectMapper;
    private final ChatWorkflowService chatWorkflowService;

    @Override
    public boolean supports(JobType jobType) {
        return jobType == JobType.CHAT;
    }

    @Override
    public Long handle(String payload) throws Exception {
        PDFChatRequest chatRequest = objectMapper.readValue(payload, PDFChatRequest.class);
        return chatWorkflowService.chat(chatRequest.context(), chatRequest.query(), chatRequest.chapterId());
    }

}
