package com.emperium;

import com.emperium.scraper.CityScraper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;

public class CityScraperTest {

    private CityScraper cityScraper;
    private ClassLoader classLoader;

    public CityScraperTest() throws IOException {
        cityScraper = new CityScraper(54);
        classLoader = getClass().getClassLoader();
        disableHtmlUnitLogging();
    }

    @Test
    public void test_measurement_data_with_holidays_1() throws Exception {
        CityScraper testCityData = getScraperTestData("Trikala-halloween-2021.html");

        Assert.assertEquals("First measurement of first day should be at 20:00 ", testCityData.city.days.get(0).measurements.get(0).eventTime, LocalTime.parse("20:00"));
        Assert.assertEquals("Third measurement of second day should be at 08:00", testCityData.city.days.get(1).measurements.get(2).eventTime, LocalTime.parse("08:00"));
        Assert.assertEquals("Humidity at 11:00 on second day should be 47", testCityData.city.days.get(1).measurements.get(3).humidity, Integer.valueOf(47));
        Assert.assertEquals("Wind at 14:00 on second day should be \'2 Μπφ Α\' ", testCityData.city.days.get(1).measurements.get(4).wind, "2 Μπφ Α");
        Assert.assertEquals("Phenomeno on the third day at 05:00 should be \'ΚΑΘΑΡΟΣ\'", testCityData.city.days.get(2).measurements.get(1).phenomeno, "ΑΡΑΙΗ ΣΥΝΝΕΦΙΑ");
        Assert.assertEquals("Phenomeno at 02:00 on the last day should be \'ΑΣΘΕΝΗΣ ΒΡΟΧΗ\'", testCityData.city.days.get(6).measurements.get(0).phenomeno, "ΑΣΘΕΝΗΣ ΒΡΟΧΗ");
    }

    @Test
    public void test_measurement_data_with_holidays_2() throws Exception {
        CityScraper testCity = getScraperTestData("Trikala_weather_with_holidays_1st_measur_21:00.html");

        Assert.assertEquals("First measurement of first day should be at 21:00 ", testCity.city.days.get(0).measurements.get(0).eventTime, LocalTime.parse("21:00"));
        Assert.assertEquals("Second measurement of second day should be at 06:00", testCity.city.days.get(1).measurements.get(1).eventTime, LocalTime.parse("06:00"));
        Assert.assertEquals("Humidity at 03:00 on second day should be 85", testCity.city.days.get(1).measurements.get(0).humidity, Integer.valueOf(85));
        Assert.assertEquals("Wind at 15:00 on second day should be \'3 Μπφ Α\' ", testCity.city.days.get(1).measurements.get(4).wind, "3 Μπφ Α");
        Assert.assertEquals("Phenomeno on the third day at 06:00 should be \'ΚΑΘΑΡΟΣ\'", testCity.city.days.get(2).measurements.get(1).phenomeno, "ΚΑΘΑΡΟΣ");
        Assert.assertEquals("Phenomeno at 03:00 on the last day should be \'ΑΡΑΙΗ ΣΥΝΝΕΦΙΑ\'", testCity.city.days.get(6).measurements.get(0).phenomeno, "ΑΡΑΙΗ ΣΥΝΝΕΦΙΑ");
    }

    @Test
    public void test_measuement_data_without_Holidays() throws IOException {
        File file = new File(Objects.requireNonNull(classLoader.getResource("Trikala_no_holiday_1st_measurement_21:00.html")).getFile());
        HtmlPage city = getWebClient().getPage("file:" + file);

        List<HtmlElement> eventDates = (List<HtmlElement>)city.getByXPath("//span[@class='dayNumbercf']");
        List<HtmlElement> months = (List<HtmlElement>) city.getByXPath("//span[@class='monthNumbercf']");

        List<String> daysToList = cityScraper.daysToList(eventDates);
        List<String> monthsToList = cityScraper.monthsToList(months);

        List<LocalDate> datesList = cityScraper.zipDaymonthToLocaDate(daysToList, monthsToList );

        List<HtmlElement> holidayEventDate = (List<HtmlElement>) city.getByXPath("//span[@class='monthNumbercf']");
        List<LocalDate> holidaysList = cityScraper.holidaysToList(holidayEventDate);

        List<HtmlElement> eventTime = (List<HtmlElement>)city.getByXPath("//td[@class='innerTableCell fulltime']");
        List<LocalTime> timeList  = cityScraper.timeToList(eventTime);

        List<HtmlElement> eventTemp = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell temperature tempwidth']");
        List<Integer> tempList = cityScraper.tempToList(eventTemp, 0);

        List<HtmlElement> eventHumidity = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell temperature tempwidth']");
        List<Integer> humidityList = cityScraper.tempToList(eventHumidity, 1);

        List<HtmlElement> eventWind = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell anemosfull']");
        List<String> windList =  cityScraper.windToList(eventWind);

        List<HtmlElement> eventPhenomeno = (List<HtmlElement>) city.getByXPath("//td[@class='phenomeno-name ']");
        List<String> phenomenoList = cityScraper.phenomenoToList(eventPhenomeno);

        if(holidaysList.size() != 0){
            datesList.addAll(holidaysList);
            Collections.sort(datesList);
        }

        cityScraper.setCityDays(datesList);
        cityScraper.setDailyMeasurements(timeList, tempList, humidityList, windList, phenomenoList);
        Assert.assertEquals("First measurement of first day should be at 21:00 ", cityScraper.city.days.get(0).measurements.get(0).eventTime, LocalTime.parse("21:00"));
        Assert.assertEquals("Second measurement of second day should be at 06:00", cityScraper.city.days.get(1).measurements.get(1).eventTime, LocalTime.parse("06:00"));
        Assert.assertEquals("Humidity at 03:00 on second day should be 65", cityScraper.city.days.get(1).measurements.get(0).humidity, Integer.valueOf(65));
        Assert.assertEquals("Wind at 15:00 on second day should be \'1 Μπφ ΝΑ\' ", cityScraper.city.days.get(1).measurements.get(4).wind, "1 Μπφ NA");
        Assert.assertEquals("Phenomeno on the third day at 06:00 should be \'ΚΑΘΑΡΟΣ\'", cityScraper.city.days.get(2).measurements.get(1).phenomeno, "ΚΑΘΑΡΟΣ");
        Assert.assertEquals("Phenomeno at 03:00 on the last day should be \'ΑΡΑΙΗ ΣΥΝΝΕΦΙΑ\'", cityScraper.city.days.get(6).measurements.get(0).phenomeno, "ΑΡΑΙΗ ΣΥΝΝΕΦΙΑ");
    }

    @Test
    public void test_days_size() throws IOException {
        File file = new File(Objects.requireNonNull(classLoader.getResource("Trikala_weather_no_holidays_1st_measure_18:00.html")).getFile());
        HtmlPage city = getWebClient().getPage("file:" + file);
        List<HtmlElement> eventDates = (List<HtmlElement>)city.getByXPath("//span[@class='dayNumbercf']");

        List<HtmlElement> months = (List<HtmlElement>) city.getByXPath("//span[@class='monthNumbercf']");

        List<String> daysToList = cityScraper.daysToList(eventDates);
        List<String> monthsToList = cityScraper.monthsToList(months);

        List<LocalDate> datesList = cityScraper.zipDaymonthToLocaDate(daysToList, monthsToList );
        cityScraper.setCityDays(datesList);

        Assert.assertEquals("A city must have measurements for 7 days.", cityScraper.city.days.size() , 7);
    }

    @Test
    public void test_holidays_size() throws IOException {
        File file = new File(Objects.requireNonNull(classLoader.getResource("Trikala_weather_with_holidays_1st_measur_21:00.html")).getFile());
        HtmlPage city = getWebClient().getPage("file:" + file);
        List<HtmlElement> eventHolidays = (List<HtmlElement>)city.getByXPath("//span[@class='monthNumbercf']");
        List<LocalDate> holidaysList = cityScraper.holidaysToList(eventHolidays);

        Assert.assertEquals("This forecast should include five holidays.", holidaysList.size(), 5);
    }

    @Test
    public void test_holidays_size_negative() throws IOException {
        File file = new File(Objects.requireNonNull(classLoader.getResource("Trikala_weather_no_holidays_1st_measure_18:00.html")).getFile());
        HtmlPage city = getWebClient().getPage("file:" + file);
        List<HtmlElement> eventHolidays = (List<HtmlElement>)city.getByXPath("//span[@class='monthNumbercf']");
        List<LocalDate> holidaysList = cityScraper.holidaysToList(eventHolidays);

        Assert.assertEquals("It should return zero days.", holidaysList.size(), 0);
    }

    @Test
    public void test_timeList_size() throws  IOException {
        File file = new File(Objects.requireNonNull(classLoader.getResource("Trikala_weather_no_holidays_1st_measure_18:00.html")).getFile());
        HtmlPage city = getWebClient().getPage("file:" + file);
        List<HtmlElement> eventTime = (List<HtmlElement>)city.getByXPath("//td[@class='innerTableCell fulltime']");
        List<LocalTime> timeList  = cityScraper.timeToList(eventTime);

        Assert.assertEquals("It should contain 44 values.",  timeList.size(), 44);
    }

    @Test
    public void test_temperatureList_size() throws IOException {
        File file = new File(Objects.requireNonNull(classLoader.getResource("Trikala_weather_no_holidays_1st_measure_18:00.html")).getFile());
        HtmlPage city = getWebClient().getPage("file:"+file);
        List<HtmlElement> eventTemp = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell temperature tempwidth']");
        List<Integer> tempList = cityScraper.tempToList(eventTemp, 0);

        Assert.assertEquals("It should contain 44 values.", tempList.size(), 44);
    }

    @Test
    public void test_humidityList_size() throws IOException{
        File file = new File(Objects.requireNonNull(classLoader.getResource("Trikala_weather_no_holidays_1st_measure_18:00.html")).getFile());
        HtmlPage city = getWebClient().getPage("file:"+file);
        List<HtmlElement> eventHumidity = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell temperature tempwidth']");
        List<Integer> humidityList = cityScraper.tempToList(eventHumidity, 1);

        Assert.assertEquals("It should contain 44 values", humidityList.size(), 44);
    }

    @Test
    public void test_windList_size() throws IOException{
        File file = new File(Objects.requireNonNull(classLoader.getResource("Trikala_weather_no_holidays_1st_measure_18:00.html")).getFile());
        HtmlPage city = getWebClient().getPage("file:"+file);
        List<HtmlElement> eventWind = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell anemosfull']");

        Assert.assertEquals("Wind values should be 44.", cityScraper.windToList(eventWind).size(), 44);
    }

    @Test
    public void test_phenomenon_size() throws IOException {
        File file = new File(Objects.requireNonNull(classLoader.getResource("Trikala_weather_no_holidays_1st_measure_18:00.html")).getFile());
        HtmlPage city = getWebClient().getPage("file:"+file);
        List<HtmlElement> eventPhenomeno = (List<HtmlElement>) city.getByXPath("//td[@class='phenomeno-name ']");
        List<String> phenomenoList = cityScraper.phenomenoToList(eventPhenomeno);

        Assert.assertEquals("Phenomenon values should be 44.", phenomenoList.size(), 44);
    }

    private WebClient getWebClient(){
            WebClient webClient = new WebClient();
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            return webClient;
    }

    private CityScraper getScraperTestData(String testData) throws Exception{
        File file = new File(Objects.requireNonNull(classLoader.getResource(testData)).getFile());
        HtmlPage city = getWebClient().getPage("file:" + file);

        List<HtmlElement> eventDates = (List<HtmlElement>)city.getByXPath("//span[@class='dayNumbercf']");

        List<HtmlElement> months = (List<HtmlElement>) city.getByXPath("//span[@class='monthNumbercf']");

        List<String> daysToList = cityScraper.daysToList(eventDates);
        List<String> monthsToList = cityScraper.monthsToList(months);
        List<LocalDate> datesList = cityScraper.zipDaymonthToLocaDate(daysToList, monthsToList );

        List<HtmlElement> holidayEventDate = (List<HtmlElement>) city.getByXPath("//span[@class='monthNumbercf']");
        List<LocalDate> holidaysList = cityScraper.holidaysToList(holidayEventDate);

        List<HtmlElement> eventTime = (List<HtmlElement>)city.getByXPath("//td[@class='innerTableCell fulltime']");
        List<LocalTime> timeList  = cityScraper.timeToList(eventTime);

        List<HtmlElement> eventTemp = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell temperature tempwidth']");
        List<Integer> tempList = cityScraper.tempToList(eventTemp, 0);

        List<HtmlElement> eventHumidity = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell temperature tempwidth']");
        List<Integer> humidityList = cityScraper.tempToList(eventHumidity, 1);

        List<HtmlElement> eventWind = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell anemosfull']");
        List<String> windList =  cityScraper.windToList(eventWind);

        List<HtmlElement> eventPhenomeno = (List<HtmlElement>) city.getByXPath("//td[@class='phenomeno-name ']");
        List<String> phenomenoList = cityScraper.phenomenoToList(eventPhenomeno);

        if(holidaysList.size() != 0){
            datesList.addAll(holidaysList);
            Collections.sort(datesList);
        }

        cityScraper.setCityDays(datesList);
        cityScraper.setDailyMeasurements(timeList, tempList, humidityList, windList, phenomenoList);

        return cityScraper;
    }

    /** Disable HtmlUnit logging for less output while building/testing. */
    private void disableHtmlUnitLogging() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }
}