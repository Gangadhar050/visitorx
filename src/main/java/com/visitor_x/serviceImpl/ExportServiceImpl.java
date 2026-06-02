package com.visitor_x.service.impl;

import com.visitor_x.entity.Visitor;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.service.ExportService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl
        implements ExportService {

    private final VisitorRepository repository;

    @Override
    public void exportVisitors(
            HttpServletResponse response) {

        try {

            List<Visitor> visitors =
                    repository.findAll();

            Workbook workbook =
                    new XSSFWorkbook();

            Sheet sheet =
                    workbook.createSheet(
                            "Visitors");

            Row header =
                    sheet.createRow(0);

            header.createCell(0)
                    .setCellValue("ID");

            header.createCell(1)
                    .setCellValue("Name");

            header.createCell(2)
                    .setCellValue("Mobile");

            header.createCell(3)
                    .setCellValue("Email");

            header.createCell(4)
                    .setCellValue("Purpose");

            int rowNum = 1;

            for (Visitor visitor : visitors) {

                Row row =
                        sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(
                                visitor.getVisitorId());

                row.createCell(1)
                        .setCellValue(
                                visitor.getName());

                row.createCell(2)
                        .setCellValue(
                                visitor.getMobileNumber());

                row.createCell(3)
                        .setCellValue(
                                visitor.getEmail());

                row.createCell(4)
                        .setCellValue(
                                visitor.getPurposeOfVisit());
            }

            response.setContentType(
                    "application/octet-stream");

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=visitors.xlsx");

            ServletOutputStream outputStream =
                    response.getOutputStream();

            workbook.write(outputStream);

            workbook.close();

            outputStream.close();

        } catch (Exception ex) {

            throw new RuntimeException(
                    ex.getMessage());
        }
    }
}