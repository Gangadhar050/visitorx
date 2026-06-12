package com.visitor_x;

import com.visitor_x.entity.Visitor;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.serviceImpl.ExportServiceImpl;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceImplTest {

    @Mock
    private VisitorRepository repository;

    @InjectMocks
    private ExportServiceImpl exportService;

    @TempDir
    Path tempDir;

    private Visitor visitor;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(
                exportService,
                "savePath",
                tempDir.toString()
        );

        visitor = Visitor.builder()
                .visitorId(1L)
                .name("Gangadhar")
                .mobileNumber("9876543210")
                .email("gangadhar@gmail.com")
                .purposeOfVisit(com.visitor_x.enums.PurposeOfVisit.INTERVIEW)
                .address("Bangalore")
                .photo("test photo data".getBytes())
                .visitDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void exportVisitors_Success() throws IOException {

        when(repository.findAll())
                .thenReturn(List.of(visitor));

        HttpServletResponse response = mock(HttpServletResponse.class);

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        ServletOutputStream servletOutputStream =
                new ServletOutputStream() {
                    @Override
                    public void write(int b) {
                        outputStream.write(b);
                    }

                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(
                            WriteListener writeListener) {
                    }
                };

        when(response.getOutputStream())
                .thenReturn(servletOutputStream);

        exportService.exportVisitors(response);

        verify(response).setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        verify(response).setHeader(
                eq("Content-Disposition"),
                contains("visitors.xlsx"));

        assertTrue(outputStream.size() > 0);
    }

    @Test
    void exportVisitors_EmptyList() throws IOException {

        when(repository.findAll())
                .thenReturn(Collections.emptyList());

        HttpServletResponse response = mock(HttpServletResponse.class);

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        ServletOutputStream servletOutputStream =
                new ServletOutputStream() {
                    @Override
                    public void write(int b) {
                        outputStream.write(b);
                    }

                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(
                            WriteListener writeListener) {
                    }
                };

        when(response.getOutputStream())
                .thenReturn(servletOutputStream);

        exportService.exportVisitors(response);

        assertTrue(outputStream.size() > 0);
    }

    @Test
    void autoSaveToFile_Success() {

        when(repository.findAll())
                .thenReturn(List.of(visitor));

        exportService.autoSaveToFile();

        Path file =
                tempDir.resolve("visitors.xlsx");

        assertTrue(Files.exists(file));
    }

    @Test
    void autoSaveToFile_EmptyList() {

        when(repository.findAll())
                .thenReturn(Collections.emptyList());

        exportService.autoSaveToFile();

        Path file =
                tempDir.resolve("visitors.xlsx");

        assertTrue(Files.exists(file));
    }

    @Test
    void autoSaveToFile_CreatesDirectory() {

        Path customDir =
                tempDir.resolve("exports");

        ReflectionTestUtils.setField(
                exportService,
                "savePath",
                customDir.toString()
        );

        when(repository.findAll())
                .thenReturn(List.of(visitor));

        exportService.autoSaveToFile();

        assertTrue(Files.exists(customDir));
        assertTrue(
                Files.exists(
                        customDir.resolve("visitors.xlsx")
                )
        );
    }
}