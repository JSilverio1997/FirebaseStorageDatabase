package com.example.firebasecursods.Utilidades;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.util.concurrent.Phaser;

public class PdfCreator  extends PdfPageEventHelper {

    Phrase[] pharses = new Phrase[2];
    int numero_pagina;

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {

        pharses[0] = new Phrase("PDF");
}

    @Override
    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
        pharses[1] = new Phrase(title.getContent());
        numero_pagina = 1;

    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        numero_pagina ++;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        Rectangle rectangle = writer.getBoxSize("box_a");

        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase("")
                                  , rectangle.getRight(), rectangle.getTop(),  0);

        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase(String.format("id", numero_pagina))
                , rectangle.getRight( ) + rectangle.getRight() / 2 , rectangle.getBottom() - 10,  0);
    }
}
