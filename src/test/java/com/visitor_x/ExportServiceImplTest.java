package com.visitor_x;

import com.visitor_x.entity.Visitor;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.serviceImpl.ExportServiceImpl;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceImplTest {

    @Mock
    private VisitorRepository repository;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private ExportServiceImpl exportService;

    @Test
    void exportVisitors_ShouldExportSuccessfully() throws Exception {

        Visitor visitor = new Visitor();
        visitor.setVisitorId(1L);
        visitor.setName("John");
        visitor.setMobileNumber("9876543210");
        visitor.setEmail("john@gmail.com");
        visitor.setAddress("Bangalore");
        visitor.setVisitDateTime(LocalDateTime.now());

        when(repository.findAll())
                .thenReturn(List.of(visitor));

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

        verify(repository, times(1))
                .findAll();

        verify(response).setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        verify(response).setHeader(
                eq("Content-Disposition"),
                contains("visitors.xlsx"));
    }

    @Test
    void exportVisitors_ShouldHandleEmptyVisitorList()
            throws Exception {

        when(repository.findAll())
                .thenReturn(List.of());

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

        verify(repository).findAll();
    }

    @Test
    void exportVisitors_ShouldThrowRuntimeException_WhenOutputFails()
            throws Exception {

        when(repository.findAll())
                .thenReturn(List.of());

        when(response.getOutputStream())
                .thenThrow(new RuntimeException("Output Error"));

        assertThrows(RuntimeException.class,
                () -> exportService.exportVisitors(response));
    }
}