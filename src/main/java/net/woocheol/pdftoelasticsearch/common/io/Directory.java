package net.woocheol.pdftoelasticsearch.common.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by social on 16. 5. 23.
 */
public class Directory extends Entry {
    private File file;
    private String name;
    private int size;
    private ArrayList<Entry> dir = new ArrayList<Entry>();

    public Directory(File file) {
        this.file = file;
        File[] files = this.file.listFiles();
        for(File path: files) {
            if(path.isDirectory()) {
                Directory directory = new Directory(path);
                dir.add(directory);
            }else if(path.isFile()) {
                PDFFile pdfFile = new PDFFile(path);
                pdfFile.setDirName(file.getName());
                dir.add(pdfFile);
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    public Entry add(Entry entry) {
        dir.add(entry);
        return this;
    }

    public Iterator iterator() {
        return dir.iterator();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
