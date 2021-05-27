package com.emperium.resources;


import com.emperium.lucene.Searcher;
import com.emperium.lucene.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Path("suggest")
public class Cities {

    private Searcher searcher = new Searcher();
    private ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Path("/{city}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCitiesSuggestions(@PathParam("city") String city) throws IOException, ParseException {
        IndexSearcher indexSearcher = Utils.getIndexSearcher();
        Map<String, String> results = new HashMap<>();

        TopDocs found = searcher.searchByWildCard(city, indexSearcher);
        for (ScoreDoc sd : found.scoreDocs) {
            Document d = indexSearcher.doc(sd.doc);

            results.put(String.format(d.get("city")), String.format(d.get("id")));
        }

        if(!results.isEmpty()) return getSuggestionsNode(results);

        return noCitiesFoundMessage();
    }

    private String getSuggestionsNode(Map<String, String> cities) {
        ObjectNode node = objectMapper.createObjectNode();

        ArrayNode arrayNode = node.arrayNode();

        for(Map.Entry<String, String> city: cities.entrySet()){
            arrayNode.addObject()
                    .put("city", city.getKey())
                    .put("id", city.getValue());
        }

        JsonNode jsonNode = node.arrayNode().addAll(arrayNode);

        return String.valueOf(jsonNode);
    }

    private String noCitiesFoundMessage() { return "No cities found"; }
}
