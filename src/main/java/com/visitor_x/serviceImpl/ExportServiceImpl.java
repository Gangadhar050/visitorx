package com.visitor_x.serviceImpl;

import com.visitor_x.entity.Visitor;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final VisitorRepository repository;

    @Value("${app.export.save-path:exports/}")
    private String savePath;

    // ── Download via Postman / browser ──────────────────────────
    @Override
    public void exportVisitors(HttpServletResponse response) {
        List<Visitor> visitors = repository.findAll();

        try (Workbook workbook = buildWorkbook(visitors)) {
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=visitors.xlsx");
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to export Excel: " + ex.getMessage());
        }
    }

    // ── Auto-save to disk after every registration ───────────────
    @Override
    public void autoSaveToFile() {
        List<Visitor> visitors = repository.findAll();

        try {
            Path dir = Paths.get(savePath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            // Always overwrites the same file — latest snapshot
            Path filePath = dir.resolve("visitors.xlsx");

            try (Workbook workbook = buildWorkbook(visitors);
                 FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                workbook.write(fos);
            }

        } catch (IOException ex) {
            throw new RuntimeException(
                    "Failed to auto-save Excel: " + ex.getMessage());
        }
    }

    // ── Shared workbook builder ──────────────────────────────────
    private Workbook buildWorkbook(List<Visitor> visitors) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Visitors");

        // Header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        String[] columns = {
                "ID", "Name", "Mobile", "Email",
                "Purpose", "Address", "Visit Time","PhotoURL"
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
            row.createCell(0).setCellValue(
                    v.getVisitorId() != null ? v.getVisitorId() : 0L);
            row.createCell(1).setCellValue(
                    v.getName() != null ? v.getName() : "");
            row.createCell(2).setCellValue(
                    v.getMobileNumber() != null ? v.getMobileNumber() : "");
            row.createCell(3).setCellValue(
                    v.getEmail() != null ? v.getEmail() : "");
            row.createCell(4).setCellValue(
                    v.getPurposeOfVisit() != null
                            ? v.getPurposeOfVisit() : ""); // ← .name()
            row.createCell(5).setCellValue(
                    v.getAddress() != null ? v.getAddress() : "");
            row.createCell(6).setCellValue(
                    v.getVisitDateTime() != null
                            ? v.getVisitDateTime().toString() : "");
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }
}