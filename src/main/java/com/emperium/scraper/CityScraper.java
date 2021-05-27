package com.emperium.scraper;

import com.emperium.domain.City;
import com.emperium.domain.Day;
import com.emperium.domain.Measurement;
import com.emperium.utils.Mappings;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CityScraper {

    private Logger logger = Logger.getLogger(CityScraper.class);

    private Calendar now;

    private static final String SERVICE_URL = "https://www.meteo.gr/cf.cfm?city_id=";

    public int cityKey;
    public City city;

    private List<HtmlElement> datesEvent;
    private List<HtmlElement> holidayEventDate;
    private List<HtmlElement> eventTime;
    private List<HtmlElement> eventTemp;
    private List<HtmlElement> eventHumidity;
    private List<HtmlElement> eventWind;
    private List<HtmlElement> phenomeno;
    private List<HtmlElement> months;


    public CityScraper(int cityKey) {
        this.cityKey = cityKey;
        this.now = Calendar.getInstance();
        this.city = new City(Mappings.cityMappings.get(cityKey));
        this.now.add(Calendar.MONTH, 1);
    }

    /**
     * Scrapes the city. This only works as long as {@link <a href="https://www.meteo.gr/">Meteo</a>} keeps these
     * specific names in the DOM elements !!!
     *
     * @throws Exception
     */
    public void scrapeCity() throws Exception {
        try{
            WebClient client = new WebClient();

            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);

            HtmlPage city = client.getPage(SERVICE_URL + this.cityKey);

            datesEvent       = (List<HtmlElement>) city.getByXPath("//span[@class='dayNumbercf']");
            months           = (List<HtmlElement>) city.getByXPath("//span[@class='monthNumbercf']");
            holidayEventDate = (List<HtmlElement>)city.getByXPath("//span[@class='monthNumbercf']");
            eventTime        = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell fulltime']");
            eventTemp        = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell temperature tempwidth']");
            eventHumidity    = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell temperature tempwidth']");
            eventWind        = (List<HtmlElement>) city.getByXPath("//td[@class='innerTableCell anemosfull']");
            phenomeno        = (List<HtmlElement>) city.getByXPath("//td[@class='phenomeno-nameXS ']");
            months           = (List<HtmlElement>) city.getByXPath("//span[@class='monthNumbercf']");


            List<String> datesList        = daysToList(datesEvent);
            List<String> monthsList       = monthsToList(months);
            List<LocalDate> holidaysList  = holidaysToList(holidayEventDate);
            List<LocalDate> dates         = zipDaymonthToLocaDate(datesList, monthsList);

            List<LocalTime> timeList      = timeToList(eventTime);

            List<Integer> temperatureList = tempToList(eventTemp, 0);
            List<Integer> humidityList    = humidityToList(eventHumidity, 1);
            List<String> windList         = windToList(eventWind);
            List<String> phenomenonList   = phenomenoToList(phenomeno);

            if (holidaysList.size() != 0) {
                dates.addAll(holidaysList);
                Collections.sort(dates);
            }
            setCityDays(dates);
            setDailyMeasurements(timeList, temperatureList, humidityList, windList, phenomenonList);

            logger.info("Scrape for city " + this.city.getName() + " finished successfully.");
        }catch ( Exception e){
            logger.error("Scrape for city " + this.city.getName() + " failed due to " + e.getMessage());
        }
    }

    public List<LocalDate> zipDaymonthToLocaDate(List<String> datesList, List<String> monthsList) {
        final String firstDate = monthsList.get(0);
        final int nextYear = now.get(Calendar.YEAR);

        return IntStream
                .range(0, Math.min(datesList.size(), monthsList.size()))
                .mapToObj(i ->  datesList.get(i) + "-" +  Mappings.monthMappings.get(monthsList.get(i)))
                .map(stringDate -> {
                    if(stringDate.substring(3,5).equals("01") && firstDate.equals("ΔΕΚΕΜΒΡΙΟΥ")) {
                       stringDate +=  "-" + nextYear;

                       return formatStringToLocalDate(stringDate);
                    }

                    int year = now.getInstance().get(Calendar.YEAR);
                    stringDate += "-" + year;

                    return formatStringToLocalDate(stringDate);
                }).collect(Collectors.toList());
    }

    private LocalDate formatStringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);

        return localDate;
    }

    public List<String> daysToList(List<HtmlElement> datesEvent) {
        return datesEvent
                .stream()
                .map(d -> d.asText().replaceAll("[^\\d]", ""))
                .map(dt -> {
                    /** Remove sunset and sunrise digits and keep only date **/
                    if (dt.length() == 10) {
                        return dt.substring(0, 2);
                    } else if (dt.length() == 2){
                        return dt;
                    }
                    return "0" + dt.substring(0, 1);
                }).collect(Collectors.toList());
    }

    public List<LocalDate> holidaysToList(List<HtmlElement> eventHoliday) {
        return eventHoliday
                .stream()
                .map(hd -> hd.asText().replaceAll("[Α-Ω]", ""))
                .map(h-> h.replace("Ϊ", ""))
                .filter(day -> !day.equals(""))
                .map(d -> {
                    try {
                        return stringToLocalDate(d.replace("/", "-"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return LocalDate.now();  //  FIXME:
                }).collect(Collectors.toList());
    }

    public List<String> monthsToList(List<HtmlElement> months) {
        return months.stream()
                .map(m -> formatMonth.apply(m.asText()))
                .collect(Collectors.toList());
    }

    private Function<String, String> formatMonth = (String month) -> {
        if (month.contains("/")) {
            return Mappings.monthMappings
                    .entrySet()
                    .stream()
                    .filter(m -> m.getValue().equals(month.substring(3,5)))
                    .findFirst()
                    .get()
                    .getKey();
        }
        return month;
    };

    public void setCityDays(List<LocalDate> days) {
        days.stream()
                .forEach(evtd -> city.days.add(new Day(evtd)));
    }

    public City getCity() {
        return city;
    }

    /**
     * Brings the index of the first value of {@link Mappings#summerTimeMappings} that is equal to
     * the first value of the foreCastTimeValues parameter. This way we can tell if the first
     * day that the scrape began, started before 3:00 am or 2:00 am (winter od summer). In this case we split the
     * measurements into sets of 8 measurements (since in each day, 8 measurements occur).
     * Otherwise we only keep the measurement at the index returned in this method until the
     * last index of the first day and then split the rest of the values into sets of 8 values.
     * (see {@link #setDailyMeasurements(List, List, List, List, List)})
     * This implementation gives the ability to the user to set the scraping scheduler
     * at any time desired.
     *
     * @param foreCastTimeValues the time values the scraping occurs
     * @return The index of the first forecast time value
     */
    private int getTimeMappingIndex(List<LocalTime> foreCastTimeValues) {
         Optional<Integer> summerMapping = Mappings.summerTimeMappings.stream()
                .filter(tm -> tm.compareTo(foreCastTimeValues.get(0)) == 0)
                .findFirst()
                .map(val -> Optional.of(Mappings.summerTimeMappings.indexOf(val)))
                .orElse(Optional.of(8));

         Optional<Integer> winterMapping = Mappings.winterTimeMappings.stream()
                 .filter(tm -> tm.compareTo(foreCastTimeValues.get(0)) == 0)
                 .findFirst()
                 .map(val -> Optional.of(Mappings.winterTimeMappings.indexOf(val)))
                 .orElse(Optional.of(8));

         return summerMapping.get() != 8 ? summerMapping.get() : winterMapping.get();
    }

    public void setDailyMeasurements(List<LocalTime> foreCastTimeValues, List<Integer> forecastTempValues,
                                     List<Integer> forecastHumidValues, List<String> forecastWindValues,
                                     List<String> forecastPhenomenonValues) {

        /** If scraping starts after at the first measurement of the day. **/
        if (getTimeMappingIndex(foreCastTimeValues) == 0) {
            int index = 0;
            for (int i = 0; i < city.days.size(); i++) {
                for (int k = 0; k < Mappings.summerTimeMappings.size(); k++) {
                    if (index == foreCastTimeValues.size()) {
                        break;
                    } else {
                        setDailyMeasurementData(i, k, index, foreCastTimeValues, forecastTempValues, forecastHumidValues,
                                forecastWindValues, forecastPhenomenonValues);
                        index++;
                    }
                }
            }
        /** If scraping starts after the first measurement of the day.  **/
        } else {
            int index = 0;
            for (int i = 0; i < Mappings.summerTimeMappings.size() - getTimeMappingIndex(foreCastTimeValues); i++) {
                setDailyMeasurementData(0, i, index, foreCastTimeValues, forecastTempValues,
                        forecastHumidValues, forecastWindValues, forecastPhenomenonValues);
                index++;
            }

            int nextDayMeasurementIndex = Mappings.summerTimeMappings.size() - getTimeMappingIndex(foreCastTimeValues);
            for (int i = 1; i < city.days.size(); i++) {
                for (int j = 0; j < 8; j++) {
                    if (nextDayMeasurementIndex == foreCastTimeValues.size()) {
                        break;
                    } else {
                        setDailyMeasurementData(i, j, nextDayMeasurementIndex, foreCastTimeValues, forecastTempValues,
                                forecastHumidValues, forecastWindValues, forecastPhenomenonValues);

                        nextDayMeasurementIndex++;
                    }
                }
            }
        }
    }

    public void setDailyMeasurementData(int cityIndex, int j, int nextDatMeasurementIndex, List<LocalTime> forecastTime,
                                         List<Integer> forecastTemperature, List<Integer> forecastHumid,
                                         List<String> forecastWind, List<String> forecastPhenomeno) {
        city.days.get(cityIndex).measurements.add(new Measurement());

        city.days.get(cityIndex).measurements.get(j).eventTime = forecastTime.get(nextDatMeasurementIndex);
        city.days.get(cityIndex).measurements.get(j).temperature = forecastTemperature.get(nextDatMeasurementIndex);
        city.days.get(cityIndex).measurements.get(j).humidity = forecastHumid.get(nextDatMeasurementIndex);
        city.days.get(cityIndex).measurements.get(j).wind = forecastWind.get(nextDatMeasurementIndex);
        city.days.get(cityIndex).measurements.get(j).phenomeno = forecastPhenomeno.get(nextDatMeasurementIndex);
    }

    public LocalDate stringToLocalDate(String day) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(day, formatter);

        return date;
    }

    public List<LocalTime> timeToList(List<HtmlElement> eventTime) {
        return eventTime
                .stream()
                .map(tl -> tl.asText().trim() + ":00")
                .map(t -> LocalTime.parse(t))
                .collect(Collectors.toList());
    }

    public List<Integer> tempToList(List<HtmlElement> temperature, int element) {
        return splitTempHumidity(temperature, element);
    }

    public List<Integer> humidityToList(List<HtmlElement> humidity, int element) {
        return splitTempHumidity(humidity, element);
    }

    public List<String> windToList(List<HtmlElement> wind) {
        return wind
                .stream()
                .map(w -> w.asText())
                .map(wd -> {
                    if (wd.matches("[a-zA-Z]+")) {
                        return wd;
                    }
                    String[] event = wd.split("\\R");
                    return event[0].trim();
                }).collect(Collectors.toList());
    }

    public List<String> phenomenoToList(List<HtmlElement> phenomeno) {
        return phenomeno
                .stream()
                .map(ph -> ph.asText().trim())
                .collect(Collectors.toList());
    }

    /**
     * Temperature element comes with a small hidden element underneath representing the humidity and sometimes
     * with a "feels like" extra temperature value. All these are scraped in two lines and when the "feels like"
     * temperature is present then they are scraped in three. We only keep the values for temperature and humidity.
     *
     * @param event
     * @return the event input parameter as a List
     */
    private List<Integer> splitTempHumidity(List<HtmlElement> event, int element) {
        return event
                .stream()
                .map(evt -> evt.asText())
                .map(t -> {
                    String[] items = t.split("\\R");
                    if (items.length == 3 && element == 1) {
                        return items[2].replaceAll("[^0-9]", "");
                    }
                    /** If temperature is minus degrees Celcius */
                    if(items[element].startsWith("-")) {
                       String temp = items[element].replaceAll("[^0-9]", "");
                       String temperature = "-" + temp;
                       return temperature;
                    }
                    return items[element].replaceAll("[^0-9]", "");
                })
                .map(ev -> Integer.parseInt(ev))
                .collect(Collectors.toList());
    }
}