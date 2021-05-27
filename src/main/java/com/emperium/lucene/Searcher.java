package com.emperium.lucene;

import com.emperium.utils.Mappings;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.Map;

public class Searcher {

    private Logger logger = Logger.getLogger(Searcher.class);

    public void searchDocuments() throws IOException, ParseException {
        IndexSearcher searcher = Utils.getIndexSearcher();

        for (Map.Entry<Integer, String> entry : Mappings.cityMappings.entrySet()) {
            TopDocs docs = searchByCity(entry.getValue(), searcher);

            if (docs.scoreDocs.length == 0) {
                IndexWriter writer = Utils.getIndexWriter();
                Document document = updateIndex("city", entry.getValue(), "id", String.valueOf(entry.getKey()));
                writer.addDocument(document);
                writer.close();
            }
        }
    }

    public TopDocs searchByCity(String city, IndexSearcher searcher) throws ParseException, IOException {
        QueryParser qp = new QueryParser("city", new SimpleAnalyzer());
        Query query = qp.parse(QueryParserBase.escape(city));

        TopDocs docs = searcher.search(query, 10);

        return docs;
    }

    public TopDocs searchByWildCard(String city, IndexSearcher searcher) throws IOException, ParseException {
        QueryParser qp = new QueryParser("city", new SimpleAnalyzer());
        qp.setAllowLeadingWildcard(true);

        Query query = qp.parse("*" + city + "*");
        TopDocs hits = searcher.search(query, 100);

        return hits;
    }

    private Document updateIndex(String cityField, String cityValue, String idField, String idValue) {
        Document document = new Document();

        document.add(new TextField(cityField, cityValue, Field.Store.YES));
        document.add(new TextField(idField, idValue, Field.Store.YES));

        logger.info(">>>> New document added: city: " + cityValue + " id: " + idValue);
        return document;
    }
}
