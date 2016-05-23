package net.woocheol.pdftoelasticsearch.common.io;


import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by social on 16. 5. 23.
 */
public class ListVisitor extends Visitor{
    private final Client client;
    private String currentDir = "";

    public ListVisitor(Client client) {
        this.client = client;
    }

    @Override
    public void visit(PDFFile file) {
        ArrayList<XContentBuilder> contents = file.extractText();
        if(contents!=null) {
            String folderName = file.getFolderName();
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for(int i=0; i < contents.size(); i++) {
                bulkRequest.add(client.prepareIndex("pdf", folderName, String.valueOf(i+1))
                        .setSource(
                                contents.get(i)
                        )
                );

            }
            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()) {
                System.err.println("bulk insert failed : " + file.getName());
                // process failures by iterating through each bulk response item
            }

        }
    }

    @Override
    public void visit(Directory directory) {
        String saveDir = currentDir;
        currentDir = currentDir + "/" + directory.getName();
        Iterator it = directory.iterator();
        while(it.hasNext()) {
            Entry entry = (Entry) it.next();
            entry.accept(this);
        }
        currentDir = saveDir;
    }
}
