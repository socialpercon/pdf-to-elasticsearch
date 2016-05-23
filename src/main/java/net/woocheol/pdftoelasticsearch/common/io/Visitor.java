package net.woocheol.pdftoelasticsearch.common.io;


import java.io.File;

/**
 * Created by social on 16. 5. 23.
 */
public abstract class Visitor {
    public abstract void visit(PDFFile file);
    public abstract void visit(Directory directory);
}
