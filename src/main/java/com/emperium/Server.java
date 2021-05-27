package com.emperium;

import com.emperium.lucene.Indexer;
import com.emperium.scraper.MeteoScraper;
import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.quartz.SchedulerException;

import java.io.IOException;
import java.net.URI;

public class Server {

    private static Logger logger = Logger.getLogger(Server.class);
    private static MeteoScraper meteoScraper = new MeteoScraper();
    private static Indexer indexWriter = new Indexer();

    private static final String BASE_URI = "http://0.0.0.0:8087/";

    private static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("com.emperium");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException, ParseException, SchedulerException, InterruptedException {
        // Delay application start up so that database container starts first. FIXME: move this solution to docker / docker compose
        Thread.sleep(30*1000);
        final HttpServer server = startServer();
        server.start();
        logger.info("Http server started...");

        indexWriter.createOrUpdateIndex();

        meteoScraper.init();
    }
}
