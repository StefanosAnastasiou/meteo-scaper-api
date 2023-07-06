package com.emperium.domain;

import java.time.LocalTime;

public class Measurement {

        private LocalTime eventTime;
        private Integer temperature;
        private Integer humidity;
        private String wind;
        private String phenomeno;

        public LocalTime getEventTime() {
                return eventTime;
        }

        public void setEventTime(LocalTime eventTime) {
                this.eventTime = eventTime;
        }

        public Integer getTemperature() {
                return temperature;
        }

        public void setTemperature(Integer temperature) {
                this.temperature = temperature;
        }

        public Integer getHumidity() {
                return humidity;
        }

        public void setHumidity(Integer humidity) {
                this.humidity = humidity;
        }

        public String getWind() {
                return wind;
        }

        public void setWind(String wind) {
                this.wind = wind;
        }

        public String getPhenomeno() {
                return phenomeno;
        }

        public void setPhenomeno(String phenomeno) {
                this.phenomeno = phenomeno;
        }
}
