package com.visitor_x;


import com.visitor_x.Enum.PurposeOfVisit;
import com.visitor_x.entity.Visitor;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.serviceImpl.ExportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportServiceImplTest {

    @Mock
    private VisitorRepository repository;

    @InjectMocks
    private ExportServiceImpl exportService;

    private Visitor visitor;

    @BeforeEach
    void setUp() {

        visitor = Visitor.builder()
                .visitorId(1L)
                .name("Gangadhar")
                .mobileNumber("9876543210")
                .email("gangadhar@gmail.com")
                .address("Bangalore")
                .purposeOfVisit(PurposeOfVisit.INTERVIEW)
                .visitDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void exportVisitors_ShouldGenerateExcelSuccessfully() {

        when(repository.findAll())
                .thenReturn(List.of(visitor));

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        assertDoesNotThrow(() ->
                exportService.exportVisitors(response));

        assertEquals(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                response.getContentType()
        );

        assertTrue(
                response.getHeader("Content-Disposition")
                        .contains("visitors.xlsx")
        );

        assertTrue(response.getContentAsByteArray().length > 0);
    }

    @Test
    void exportVisitors_WhenNoVisitors_ShouldStillGenerateExcel() {

        when(repository.findAll())
                .thenReturn(List.of());

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        assertDoesNotThrow(() ->
                exportService.exportVisitors(response));

        assertEquals(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                response.getContentType()
        );

        assertTrue(response.getContentAsByteArray().length > 0);
    }

    @Test
    void exportVisitors_WhenRepositoryReturnsData_ShouldSetHeaders() {

        when(repository.findAll())
                .thenReturn(List.of(visitor));

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        exportService.exportVisitors(response);

        String disposition =
                response.getHeader("Content-Disposition");

        assertNotNull(disposition);
        assertEquals(
                "attachment; filename=visitors.xlsx",
                disposition
        );
    }
}
