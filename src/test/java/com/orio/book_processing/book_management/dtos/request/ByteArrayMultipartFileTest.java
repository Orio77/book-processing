package com.orio.book_processing.book_management.dtos.request;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;

import org.junit.jupiter.api.Test;

class ByteArrayMultipartFileTest {

    @Test
    void defaultsWhenNulls() {
        ByteArrayMultipartFile f = new ByteArrayMultipartFile(null, null, null);
        assertEquals("upload.pdf", f.getOriginalFilename());
        assertEquals("application/pdf", f.getContentType());
        assertTrue(f.isEmpty());
        assertEquals("upload.pdf", f.getName());
    }

    @Test
    void nonEmpty() {
        byte[] data = { 1, 2 };
        ByteArrayMultipartFile f = new ByteArrayMultipartFile("a.pdf", "application/x-pdf", data);
        assertFalse(f.isEmpty());
        assertEquals(2, f.getSize());
        assertArrayEquals(data, f.getBytes());
    }

    @Test
    void getInputStream_readsContent() throws Exception {
        byte[] data = { 9 };
        ByteArrayMultipartFile f = new ByteArrayMultipartFile("x", "t", data);
        assertArrayEquals(data, f.getInputStream().readAllBytes());
    }

    @Test
    void transferTo_writesBytes() throws Exception {
        byte[] data = { 3, 4, 5 };
        ByteArrayMultipartFile f = new ByteArrayMultipartFile("x", "t", data);
        var path = Files.createTempFile("bmf", ".bin");
        try {
            f.transferTo(path.toFile());
            assertArrayEquals(data, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }
}
