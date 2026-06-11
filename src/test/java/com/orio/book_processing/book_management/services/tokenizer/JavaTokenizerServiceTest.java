package com.orio.book_processing.book_management.services.tokenizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JavaTokenizerServiceTest {

    @InjectMocks
    private JavaTokenizerService tokenizerService;

    static Stream<Arguments> argumentProvider() {
        return Stream.of(
                Arguments.of("This is a test. This is a T.L.A. test. Now with a Dr. in it.",
                        List.of("This is a test. ", "This is a T.L.A. test. ", "Now with a Dr. in it.")),
                Arguments.of("", List.of()),
                Arguments.of("One sentence only.", List.of("One sentence only.")));
    }

    @ParameterizedTest
    @MethodSource("argumentProvider")
    void test_tokenize(String in, List<String> expectedOut) {
        List<String> sentences = tokenizerService.tokenize(in);
        assertEquals(expectedOut, sentences);
    }

}
