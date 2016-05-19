package net.woocheol.pdftoelasticsearch;

import org.apache.commons.cli.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by choi on 2016-05-18.
 */
public class PDFReader {

    private static void extractText(String host, File f) throws IOException {

        PDDocument pddDocument = PDDocument.load(f);
        Client client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
        PDFTextStripper reader = new PDFTextStripper();
        int numberOfPages = pddDocument.getNumberOfPages();
        String fName = f.getName();
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (int i = 1; i <= numberOfPages; i++) {
            reader.setStartPage(i);
            reader.setEndPage(i);
            String pageText = reader.getText(pddDocument);
            bulkRequest.add(client.prepareIndex("pdf", fName, String.valueOf(i))
                    .setSource(XContentFactory.jsonBuilder()
                            .startObject()
                            .field("content", pageText)
                            .endObject()
                    )
            );
            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()) {
                System.err.println("bulk insert failed : " + fName);
                // process failures by iterating through each bulk response item
            }

// either use client#prepare, or use Requests# to directly build index/delete requests
        }


        //System.out.println(content.substring(0, 500));
       /*
       PDDocumentInformation info = pddDocument.getDocumentInformation();
       System.out.println( "Page Count=" + pddDocument.getNumberOfPages() );
       System.out.println( "Title=" + info.getTitle() );
       System.out.println( "Author=" + info.getAuthor() );
       System.out.println( "Subject=" + info.getSubject() );
       System.out.println( "Keywords=" + info.getKeywords() );
       System.out.println( "Creator=" + info.getCreator() );
       System.out.println( "Producer=" + info.getProducer() );
       System.out.println( "Creation Date=" + info.getCreationDate() );
       System.out.println( "Modification Date=" + info.getModificationDate());
       System.out.println( "Trapped=" + info.getTrapped() );
       */
    }

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("f", "folder", true, "folderpath");
        options.addOption("h", "host", true, "hostname");
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("folder")) {
                String folderPath = commandLine.getOptionValue("folder");
                String hostName = commandLine.getOptionValue("host");
                System.out.println(folderPath);
                File path = new File(folderPath);
                File[] list = path.listFiles();
                for(File filePath:list){
                    System.out.println(filePath);
//                    extractText(hostName, filePath);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
