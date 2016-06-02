package net.woocheol.pdftoelasticsearch.common.io;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by social on 16. 5. 23.
 */
public class PDFFile extends Entry{
    private File file;
    private String name;
    private int size;
    private String dirName;

    public PDFFile(File file) {
        this.file = file;
    }

    public ArrayList<XContentBuilder> extractText() {
        try{
            if(file.isFile() && file.getName().contains("pdf")) {
                String fName = file.getName();
                System.out.println(fName);
                PDDocument pddDocument = PDDocument.load(file); // NOSONAR
                PDFTextStripper reader = new PDFTextStripper();
                int numberOfPages = pddDocument.getNumberOfPages();
                ArrayList<XContentBuilder> contents = new ArrayList<XContentBuilder>();
                for (int i = 1; i <= numberOfPages; i++) {
                    reader.setStartPage(i);
                    reader.setEndPage(i);
                    String pageText = reader.getText(pddDocument);
                    XContentBuilder content = XContentFactory.jsonBuilder() // NOSONAR
                            .startObject()
                            .field("title", fName)
                            .field("content", pageText)
                            .endObject();
                    contents.add(content);
                }
                pddDocument.close();
                System.out.println("pages : " + contents.size() + ", title : " + fName);
                return contents;
            }
        } catch(NoClassDefFoundError e) {
            return null;

        } catch(Exception e) {
            return null;
        }

        return null;
    }

    public String getFolderName() {
        if(this.dirName == null) {
            return this.file.getParent();
        }else {
            return this.dirName;
        }
    }

    @Override
    public String getName() {
        return this.file.getName();
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }
}
