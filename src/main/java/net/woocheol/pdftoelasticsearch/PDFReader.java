package net.woocheol.pdftoelasticsearch;

import net.woocheol.pdftoelasticsearch.common.io.Directory;
import net.woocheol.pdftoelasticsearch.common.io.Entry;
import net.woocheol.pdftoelasticsearch.common.io.ListVisitor;
import net.woocheol.pdftoelasticsearch.common.io.PDFFile;
import org.apache.commons.cli.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
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
 * Created by choi on 2016-05-18.
 */
public class PDFReader {


    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        Option option = new Option("p", "path", true, "path");
        option.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(option);
        options.addOption("h", "host", true, "hostname");
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("path")) {
                String[] paths = commandLine.getOptionValues("path");
                String host = commandLine.getOptionValue("host");
                ArrayList<Entry> lists = new ArrayList<Entry>();
                for(String path:paths) {
                    File file = new File(path);
                    if(file.isDirectory()) {
                        lists.add(new Directory(file));
                    }else if(file.isFile()) {
                        lists.add(new PDFFile(file));
                    }
                }
                Settings settings = Settings.settingsBuilder().put("cluster.name", "woocheol").build();
                InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName(host), 9300);
                Client client = TransportClient.builder().settings(settings).build()
                        .addTransportAddress(transportAddress);
                ListVisitor listVisitor = new ListVisitor(client);

                for(Entry entry:lists) {
                    entry.accept(listVisitor);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
