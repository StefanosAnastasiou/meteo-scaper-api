package com.emperium.lucene;

import com.emperium.utils.Mappings;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class Indexer {

    private Logger logger = Logger.getLogger(Indexer.class);

    private Searcher searcher = new Searcher();

    public void createOrUpdateIndex() throws IOException, ParseException {
        if(indexExists()) {
            logger.info(">>>> Cities index found.");
            searcher.searchDocuments();
        } else {
            createIndex();
        }
    }

    private void createIndex() throws IOException {
        IndexWriter indexWriter = Utils.getIndexWriter();

        for(Map.Entry<Integer, String> city : Mappings.cityMappings.entrySet()) {
            Document document = createDocument(city.getValue(), city.getKey());
            indexWriter.addDocument(document);
        }

        indexWriter.close();
        logger.info(">>>>> Cities index created");
    }

    /** Use TextField so that values can be analyzed. */
    private Document createDocument(String city, Integer id) {
        Document document = new Document();
        document.add(new TextField("city", city, Field.Store.YES));
        document.add(new TextField("id", String.valueOf(id), Field.Store.YES));

        return document;
    }

    private boolean indexExists() throws IOException {
        try(Directory citiesIndex = FSDirectory.open(Paths.get(Utils.INDEX_PATH))){
            if(DirectoryReader.indexExists(citiesIndex)) return true;
            return false;
        }
    }
}