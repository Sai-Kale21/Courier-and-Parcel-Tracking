package com.example.couriertracking.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.couriertracking.entity.Parcel;
import com.example.couriertracking.repository.ParcelRepository;
import java.io.IOException;
import java.math.BigDecimal;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.Color;
import java.time.format.DateTimeFormatter;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

@Controller
public class ReportController {

    private final ParcelRepository parcelRepository;

    public ReportController(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    @GetMapping("/reports")
    public String showReports(
            @RequestParam(required = false) String trackingNumber,
            @RequestParam(required = false) Parcel.ParcelStatus status,
            @RequestParam(required = false) Parcel.CourierType courierType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            Model model) {

        String normalizedTrackingNumber =
                trackingNumber == null || trackingNumber.isBlank()
                        ? null
                        : trackingNumber.trim();

        LocalDateTime fromDateTime =
                fromDate == null ? null : fromDate.atStartOfDay();

        // Include the entire selected end date.
        LocalDateTime toDateTime =
                toDate == null ? null : toDate.plusDays(1).atStartOfDay();

        if (fromDate != null && toDate != null
                && fromDate.isAfter(toDate)) {

            model.addAttribute(
                    "errorMessage",
                    "From date cannot be later than the To date.");

            model.addAttribute("parcels", List.of());
        } else {

            List<Parcel> parcels = parcelRepository.searchParcels(
                    normalizedTrackingNumber,
                    status,
                    courierType,
                    fromDateTime,
                    toDateTime == null
                            ? null
                            : toDateTime.minusNanos(1));

            model.addAttribute("parcels", parcels);
        }

        model.addAttribute("trackingNumber", trackingNumber);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCourierType", courierType);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        model.addAttribute(
                "statuses",
                Parcel.ParcelStatus.values());

        model.addAttribute(
                "courierTypes",
                Parcel.CourierType.values());

        return "reports";
    }
    
    @GetMapping("/reports/excel")
    @ResponseBody
    public void exportExcel(
            @RequestParam(required = false) String trackingNumber,
            @RequestParam(required = false) Parcel.ParcelStatus status,
            @RequestParam(required = false) Parcel.CourierType courierType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            HttpServletResponse response) throws IOException {

        // Validate the date range.
        if (fromDate != null && toDate != null
                && fromDate.isAfter(toDate)) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "From date cannot be later than To date.");
            return;
        }

        // Normalize the tracking number.
        String normalizedTrackingNumber =
                trackingNumber == null || trackingNumber.isBlank()
                        ? null
                        : trackingNumber.trim();

        // Convert dates to date-time boundaries.
        LocalDateTime fromDateTime =
                fromDate == null ? null : fromDate.atStartOfDay();

        LocalDateTime toDateTime =
                toDate == null
                        ? null
                        : toDate.plusDays(1).atStartOfDay()
                                .minusNanos(1);

        // Retrieve parcels using the same filters as the Reports page.
        List<Parcel> parcels = parcelRepository.searchParcels(
                normalizedTrackingNumber,
                status,
                courierType,
                fromDateTime,
                toDateTime);

        // Configure the download response.
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"Courier_Parcel_Report.xlsx\"");

        // Create the Excel workbook.
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Parcel Reports");

            // Create the header row.
            String[] headers = {
                    "Tracking Number",
                    "Sender",
                    "Receiver",
                    "Receiver Phone",
                    "Pickup Address",
                    "Delivery Address",
                    "Booking Date",
                    "Courier Type",
                    "Weight",
                    "Charge",
                    "Status"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Add parcel records.
            int rowNumber = 1;

            for (Parcel parcel : parcels) {

                Row row = sheet.createRow(rowNumber++);

                row.createCell(0)
                        .setCellValue(parcel.getTrackingNumber());

                row.createCell(1)
                        .setCellValue(parcel.getSender().getName());

                row.createCell(2)
                        .setCellValue(parcel.getReceiverName());

                row.createCell(3)
                        .setCellValue(parcel.getReceiverPhone());

                row.createCell(4)
                        .setCellValue(parcel.getPickupAddress());

                row.createCell(5)
                        .setCellValue(parcel.getDeliveryAddress());

                row.createCell(6)
                        .setCellValue(
                                parcel.getBookingDate() == null
                                        ? ""
                                        : parcel.getBookingDate().toString());

                row.createCell(7)
                        .setCellValue(
                                parcel.getCourierType() == null
                                        ? ""
                                        : parcel.getCourierType().name());

                BigDecimal weight = parcel.getWeight();
                row.createCell(8).setCellValue(
                        weight == null ? 0 : weight.doubleValue());

                BigDecimal charge = parcel.getCharge();
                row.createCell(9).setCellValue(
                        charge == null ? 0 : charge.doubleValue());

                row.createCell(10)
                        .setCellValue(
                                parcel.getStatus() == null
                                        ? ""
                                        : parcel.getStatus().name());
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(response.getOutputStream());
        }
    }
    
    @GetMapping("/reports/pdf")
    public void exportPdf(
            @RequestParam(required = false) String trackingNumber,
            @RequestParam(required = false) Parcel.ParcelStatus status,
            @RequestParam(required = false) Parcel.CourierType courierType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            HttpServletResponse response) throws IOException {

        // Validate the date range.
        if (fromDate != null && toDate != null
                && fromDate.isAfter(toDate)) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "From date cannot be later than To date.");

            return;
        }

        // Normalize the tracking number.
        String normalizedTrackingNumber =
                trackingNumber == null || trackingNumber.isBlank()
                        ? null
                        : trackingNumber.trim();

        // Convert the dates to date-time boundaries.
        LocalDateTime fromDateTime =
                fromDate == null ? null : fromDate.atStartOfDay();

        LocalDateTime toDateTime =
                toDate == null
                        ? null
                        : toDate.plusDays(1).atStartOfDay()
                                .minusNanos(1);

        // Retrieve parcels matching the selected filters.
        List<Parcel> parcels = parcelRepository.searchParcels(
                normalizedTrackingNumber,
                status,
                courierType,
                fromDateTime,
                toDateTime);

        // Configure the PDF download.
        response.setContentType("application/pdf");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"Courier_Parcel_Report.pdf\"");

        // Create a landscape PDF for the wide parcel table.
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 25, 25);

        try {
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            Font titleFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 18, Color.BLUE);

            Font normalFont = FontFactory.getFont(
                    FontFactory.HELVETICA, 9, Color.BLACK);

            Font headerFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 8, Color.WHITE);

            // Report title.
            Paragraph title = new Paragraph(
                    "COURIER & PARCEL TRACKING REPORT",
                    titleFont);

            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);

            document.add(title);

            // Report generation date.
            Paragraph generatedAt = new Paragraph(
                    "Generated on: "
                            + LocalDateTime.now().format(
                                    DateTimeFormatter.ofPattern(
                                            "dd-MM-yyyy HH:mm")),
                    normalFont);

            generatedAt.setSpacingAfter(8);
            document.add(generatedAt);

            // Applied filters.
            String filterSummary =
                    "Tracking Number: "
                    + (normalizedTrackingNumber == null
                            ? "All"
                            : normalizedTrackingNumber)
                    + " | Status: "
                    + (status == null ? "All" : status)
                    + " | Courier Type: "
                    + (courierType == null ? "All" : courierType)
                    + " | From: "
                    + (fromDate == null ? "All" : fromDate)
                    + " | To: "
                    + (toDate == null ? "All" : toDate);

            Paragraph filters = new Paragraph(
                    filterSummary, normalFont);

            filters.setSpacingAfter(12);
            document.add(filters);

            // Create the parcel table.
            String[] headers = {
                    "Tracking Number",
                    "Sender",
                    "Receiver",
                    "Booking Date",
                    "Type",
                    "Weight",
                    "Charge",
                    "Status"
            };

            PdfPTable table = new PdfPTable(
                    new float[] { 2.2f, 1.3f, 1.3f, 1.8f,
                                  1.0f, 0.8f, 1.0f, 1.5f });

            table.setWidthPercentage(100);
            table.setHeaderRows(1);

            for (String heading : headers) {
                PdfPCell cell = new PdfPCell(
                        new Phrase(heading, headerFont));

                cell.setBackgroundColor(new Color(23, 54, 93));
                cell.setPadding(6);

                table.addCell(cell);
            }

            // Add matching parcel records.
            for (Parcel parcel : parcels) {

                table.addCell(new Phrase(
                        safe(parcel.getTrackingNumber()), normalFont));

                table.addCell(new Phrase(
                        safe(parcel.getSender().getName()), normalFont));

                table.addCell(new Phrase(
                        safe(parcel.getReceiverName()), normalFont));

                table.addCell(new Phrase(
                        parcel.getBookingDate() == null
                                ? "-"
                                : parcel.getBookingDate().format(
                                        DateTimeFormatter.ofPattern(
                                                "dd-MM-yyyy HH:mm")),
                        normalFont));

                table.addCell(new Phrase(
                        parcel.getCourierType() == null
                                ? "-"
                                : parcel.getCourierType().name(),
                        normalFont));

                table.addCell(new Phrase(
                        parcel.getWeight() == null
                                ? "-"
                                : parcel.getWeight().toPlainString(),
                        normalFont));

                table.addCell(new Phrase(
                        parcel.getCharge() == null
                                ? "-"
                                : "INR " + parcel.getCharge().toPlainString(),
                        normalFont));

                table.addCell(new Phrase(
                        parcel.getStatus() == null
                                ? "-"
                                : parcel.getStatus().name(),
                        normalFont));
            }

            document.add(table);
            Paragraph summary = new Paragraph(
                    "Total parcels in this report: " + parcels.size(),
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD, 11, Color.BLACK));

            summary.setSpacingBefore(12);
            document.add(summary);
        } catch (DocumentException ex) {
            throw new IOException(
                    "Unable to generate the PDF report.", ex);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}