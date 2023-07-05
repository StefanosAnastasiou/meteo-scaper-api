package com.emperium.resources;

import com.emperium.DAO.ResourceDAO;
import com.emperium.DAO.ResourceDAOImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Path("predictions")
public class Resource {

    private final Logger logger = Logger.getLogger(Resource.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private ResourceDAO resource = new ResourceDAOImpl();

    @GET
    @Path("/{city}")
    @Produces(MediaType.APPLICATION_JSON)
    public String cityPredictions(@PathParam("city") String city) {
        logger.info("Fetching predictions for " + city);

        List<Object[]> result = resource.getCityPredictions(city);

        if(result.size() != 0) return createPredictionsNode(result);

        return resourceNotFoundMessage();
    }

    @GET
    @Path("/{city}/{day}")
    @Produces(MediaType.APPLICATION_JSON)
    public String cityDailyPrediction(@PathParam("city") String city, @PathParam("day") String date) {
        logger.info("Fetching daily predictions for  " + city );

        List<Object[]> result = resource.getCityDailyPredictions(city, date);

        if(result.size() != 0) return createNode(result);

        return resourceNotFoundMessage();
    }

    @GET
    @Path("{city}/{day}/{time}")
    @Produces(MediaType.APPLICATION_JSON)
    public String cityHourPrediction(@PathParam("city") String city, @PathParam("day") String day, @PathParam("time") String time) {
        logger.info("Fetching predictions for " + city + " on: " + day + " at: " + time);

        List<Object[]> result = resource.getCityPredictionsPerHour(city, day, time);

        if(result.size() !=0) return createNode(result);

        return resourceNotFoundMessage();
    }

    private String createNode(List<Object[]> result ) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("city", (String)result.get(0)[0])
                .put("date", sqlDateToString(result.get(0)[1]));

        ArrayNode arrayNode = node.arrayNode();

        for (int i = 0; i < result.size(); i++){
            ArrayNode dataNode = arrayNode.arrayNode();
            dataNode.addObject()
                    .put("temperature", result.get(i)[3].toString())
                    .put("wind", result.get(i)[4].toString())
                    .put("humidity", result.get(i)[5].toString())
                    .put("phenomeno", result.get(i)[6].toString());

            arrayNode.addObject().putArray(result.get(i)[2].toString()).addAll(dataNode);
        }
        node.putArray("measurements").addAll(arrayNode);

        return String.valueOf(node);
    }

    private String createPredictionsNode(List<Object[]> result) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("city", (String)result.get(0)[0]);

        ArrayNode dateArray = node.arrayNode();

        String dateStart = null;
        String dateEnd = null;

        dateStart = sqlDateToString(result.get(0)[1]);

        node.putArray(dateStart);

        for(int i =0 ; i < result.size(); i++) {
            ArrayNode timeArray = dateArray.arrayNode();
            dateStart = sqlDateToString(result.get(i)[1]);

            if(dateStart.equals(dateEnd)) {

                getDataArrayNode(timeArray, result, i, dateArray);

                dateStart = sqlDateToString(result.get(i)[1]);
            } else {
                dateArray.removeAll();
                node.putArray(dateStart);

                getDataArrayNode(timeArray, result, i, dateArray);
            }

            node.putArray(dateStart).addAll(dateArray);

            dateEnd = sqlDateToString(result.get(i)[1]);
        }

        return String.valueOf(node);
    }

    private ArrayNode getDataArrayNode(ArrayNode timeArray, List<Object[]> result, int incr, ArrayNode dateArray) {
        timeArray.addObject()
                .put("temperature", result.get(incr)[3].toString())
                .put("wind", result.get(incr)[4].toString())
                .put("humidity", result.get(incr)[5].toString())
                .put("phenomeno", result.get(incr)[6].toString());

        dateArray.addObject().putArray(result.get(incr)[2].toString()).addAll(timeArray);
        return dateArray;
    }

    private String sqlDateToString(Object date) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String dateToString = df.format(date);

        return dateToString;
    }

    private String resourceNotFoundMessage() {
        return "Resource not found! Please try again";
    }
}