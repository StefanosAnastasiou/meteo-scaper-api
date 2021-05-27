package com.emperium.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class Utils {

    public static final String INDEX_PATH = System.getProperty("user.dir") + "/indices/cities";

    public static IndexWriter getIndexWriter() throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        FSDirectory dir = FSDirectory.open(Paths.get(INDEX_PATH));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(dir, config);

        return indexWriter;
    }

    public static IndexSearcher getIndexSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_PATH));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        return searcher;
    }
}