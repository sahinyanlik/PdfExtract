import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class Extractor {
    public static String documentName = "";
    public static void main(String args[]) {

        try {
            PDDocument pddDocument = PDDocument.load(new File(args[0]));
            documentName = args[0];
           for (int i = 0; i < pddDocument.getNumberOfPages(); i++) {
                int pageNum = i + 1;
                PDPage page = (PDPage) pddDocument.getPage(i);
                List<PDAnnotation> la = page.getAnnotations();
                if (la.size() < 1) {
                    continue;
                }
                PDAnnotation pdfAnnot = la.get(0);
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);
                PDRectangle rect = pdfAnnot.getRectangle();
                float x = rect.getLowerLeftX() - 1;
                float y = rect.getUpperRightY() - 1;
                float width = rect.getWidth() + 2;
                float height = rect.getHeight() + rect.getHeight() / 4;
                int rotation = page.getRotation();
                if (rotation == 0) {
                    PDRectangle pageSize = page.getMediaBox();
                    y = pageSize.getHeight() - y;
                }
                Rectangle2D.Float awtRect = new Rectangle2D.Float(x, y, width, height);
                stripper.addRegion(Integer.toString(0), awtRect);
                stripper.extractRegions(page);
                if(pdfAnnot.getColor() != null ){
                    log((pageNum) + " - " + stripper.getTextForRegion(Integer.toString(0))+"\n");
                }
            }
            pddDocument.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void log(String message) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(documentName+".txt",true), true);
        out.write(message);
        out.close();
    }
}