package com.orio.book_processing;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.auth.repositories.UserRepository;
import com.orio.book_processing.book_management.repositories.ChapterRepository;
import com.orio.book_processing.book_management.repositories.PDFRepository;
import com.orio.book_processing.book_management.repositories.SentenceRepository;
import com.orio.book_processing.chat.repositories.ChatResponseContextRepository;
import com.orio.book_processing.chat.repositories.ChatResponseRepository;
import com.orio.book_processing.queue.repositories.JobRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@EnableAutoConfiguration
public abstract class IntegrationTestBase {

    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // Database Cleanup Hook
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private PDFRepository pdfRepository;
    @Autowired
    private SentenceRepository sentenceRepository;
    @Autowired
    private ChatResponseContextRepository chatResponseContextRepository;
    @Autowired
    private ChatResponseRepository chatResponseRepository;
    @Autowired
    private JobRepository jobRepository;

    @AfterEach
    void cleanUpDB() {
        jobRepository.deleteAll();
        chatResponseContextRepository.deleteAll();
        chatResponseRepository.deleteAll();
        sentenceRepository.deleteAll();
        chapterRepository.deleteAll();
        pdfRepository.deleteAll();
        userRepository.deleteAll();
    }
}
