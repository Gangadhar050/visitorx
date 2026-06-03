package com.visitor_x.serviceImpl;

import com.visitor_x.entity.Visitor;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final VisitorRepository repository;

    @Override
    public void exportVisitors(HttpServletResponse response) {
        List<Visitor> visitors = repository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Visitors");

            // Bold header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            String[] columns = {
                    "ID", "Name", "Mobile", "Email",
                    "Purpose", "Address", "Visit Time"
            };

            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Visitor v : visitors) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getVisitorId());
                row.createCell(1).setCellValue(v.getName());
                row.createCell(2).setCellValue(v.getMobileNumber());
                row.createCell(3).setCellValue(v.getEmail());
                row.createCell(4).setCellValue(v.getPurposeOfVisit());
                row.createCell(5).setCellValue(v.getAddress());
                row.createCell(6).setCellValue(
                        v.getVisitDateTime() != null
                                ? v.getVisitDateTime().toString() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=visitors.xlsx");

            workbook.write(response.getOutputStream());

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to export Excel: " + ex.getMessage());
        }
    }
}