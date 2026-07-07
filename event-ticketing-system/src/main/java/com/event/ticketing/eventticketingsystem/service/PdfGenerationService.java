package com.event.ticketing.eventticketingsystem.service;

import com.event.ticketing.eventticketingsystem.model.Ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfGenerationService {

    public byte[] generateTicketPdf(
            Ticket ticket
    ) throws Exception {

        log.info(
                "Starting PDF generation for ticket code: {}",
                ticket.getUniqueCode()
        );

        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        PdfWriter writer =
                new PdfWriter(baos);

        PdfDocument pdfDocument =
                new PdfDocument(writer);

        Document document =
                new Document(pdfDocument);

        PdfFont titleFont =
                PdfFontFactory.createFont(
                        StandardFonts.HELVETICA_BOLD
                );

        PdfFont regularFont =
                PdfFontFactory.createFont(
                        StandardFonts.HELVETICA
                );

        document.add(
                new Paragraph("Event Ticket")
                        .setFont(titleFont)
                        .setFontSize(24)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(10)
        );

        document.add(
                new Paragraph(
                        ticket.getEvent().getTitle()
                )
                        .setFont(regularFont)
                        .setFontSize(20)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20)
        );

        document.add(
                new Paragraph("Event Details")
                        .setFont(titleFont)
                        .setFontSize(16)
                        .setUnderline()
        );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "EEEE, MMMM d, yyyy 'at' h:mm a"
                );

        document.add(
                new Paragraph()
                        .add(
                                new Text("Date & Time: ")
                                        .setFont(titleFont)
                        )
                        .add(
                                new Text(
                                        ticket.getEvent()
                                                .getDate()
                                                .format(formatter)
                                ).setFont(regularFont)
                        )
        );

        document.add(
                new Paragraph()
                        .add(
                                new Text("Location: ")
                                        .setFont(titleFont)
                        )
                        .add(
                                new Text(
                                        ticket.getEvent()
                                                .getLocation()
                                ).setFont(regularFont)
                        )
        );

        document.add(
                new Paragraph("Ticket Holder")
                        .setFont(titleFont)
                        .setFontSize(16)
                        .setUnderline()
                        .setMarginTop(20)
        );

        document.add(
                new Paragraph()
                        .add(
                                new Text("Name: ")
                                        .setFont(titleFont)
                        )
                        .add(
                                new Text(
                                        ticket.getOrder()
                                                .getUser()
                                                .getName()
                                ).setFont(regularFont)
                        )
        );

        document.add(
                new Paragraph()
                        .add(
                                new Text("Email: ")
                                        .setFont(titleFont)
                        )
                        .add(
                                new Text(
                                        ticket.getOrder()
                                                .getUser()
                                                .getEmail()
                                ).setFont(regularFont)
                        )
        );

        document.add(
                new Paragraph("Your Unique Ticket Code")
                        .setFont(titleFont)
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(40)
        );

        document.add(
                new Paragraph(
                        ticket.getUniqueCode().toString()
                )
                        .setFont(
                                PdfFontFactory.createFont(
                                        StandardFonts.COURIER
                                )
                        )
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(ColorConstants.GRAY)
        );

        Image qrCodeImage =
                createQrCode(
                        ticket.getUniqueCode().toString()
                );

        qrCodeImage.setHorizontalAlignment(
                HorizontalAlignment.CENTER
        );

        qrCodeImage.setWidth(150);
        qrCodeImage.setHeight(150);

        document.add(qrCodeImage);

        document.close();

        log.info(
                "PDF generation completed successfully for ticket code: {}",
                ticket.getUniqueCode()
        );

        return baos.toByteArray();
    }

    private Image createQrCode(
            String ticketCode
    ) throws WriterException, IOException {

        log.debug(
                "Creating QR code for ticket code: {}",
                ticketCode
        );

        QRCodeWriter qrCodeWriter =
                new QRCodeWriter();

        BitMatrix bitMatrix =
                qrCodeWriter.encode(
                        ticketCode,
                        BarcodeFormat.QR_CODE,
                        200,
                        200
                );

        ByteArrayOutputStream pngOutputStream =
                new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(
                bitMatrix,
                "PNG",
                pngOutputStream
        );

        byte[] pngData =
                pngOutputStream.toByteArray();

        Image qrCodeImage =
                new Image(
                        ImageDataFactory.create(
                                pngData
                        )
                );

        log.debug(
                "Successfully created QR code image object."
        );

        return qrCodeImage;
    }
}