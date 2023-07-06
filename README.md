[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]


## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)
* [Acknowledgements](#acknowledgements) 

## About The Project

This project was developed for fun :laughing: . It is an implementation of scraping 
with the use of HtmlUnit library. Quartz Scheduler is used for scheduling the tasks at a specific time 
of the day. As soon as the scheduler starts it scrapes [Meteo](https://www.meteo.gr) website and fetches all 
prediction measurements (time, temperature, humidity, wind speed, phenomenon) for every day of every city in Greece. 
The values scraped are saved in a relational database. Endpoints are then exposed so that the end user can view the 
meteorological predictions of the upcoming days for every individual city and day available. 
Thus this application does not provide actual meteorological measurements but predictions scraped 
from [Meteo](https://www.meteo.gr). 
 
### Built With

* [Java 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
* [Maven](https://maven.apache.org/)
* [HtmlUnit](https://htmlunit.sourceforge.io/)
* [Hibernate 5.4](https://hibernate.org/orm/releases/5.4/)
* [Quarzt Scheduler](http://www.quartz-scheduler.org/)
* [Grizzly HttpServer](https://javaee.github.io/grizzly/)
* [Jersey 2](https://eclipse-ee4j.github.io/jersey/)
* [Apache Lucene](https://lucene.apache.org/)
* [Docker](https://www.docker.com/)
* [Docker compose](https://docs.docker.com/compose/)


## Getting Started

A Cron job has been set up so that the application runs at 23:55 every night. You can change that in ScrapeScheduler class and make
it run at any time desired, even multiple times a day. 

### Prerequisites
This application utilizes docker and docker compose for deployment. Install [Docker](https://docs.docker.com/get-docker/) 
and [Docker Compose](https://docs.docker.com/compose/install/) on your machine.
You can also use [Postman](https://www.postman.com/downloads/) for the requests. Make sure you download 
Postman version 7.x.x. After installation open Settings section and enable "Use next generation URL processing" option.
This way encoding for Greek characters is enabled in URL path.


### Installation

1. Clone the repo
```sh
git clone https://github.com/StefanosAnastasiou/meteo-scraper-api.git
```
2. cd into the directory

3. Adapt where necessary the username and password for the database in docker-compose.yml file 
 
4. Install the application 
```sh
mvn clean install
```

5. Build the weather application image
```sh
docker build . -t meteo-scraper-api:latest
```

6. Run docker compose to start the containers
```sh 
docker-compose -up
``` 

## Usage

Three endpoints are exposed in this application. The first one is fetching all predictions for a specified city from 
the time the request is made and onwards. One is fetching all predictions for a city for a specific day
and the other is fetching predictions for a city, for a specific time a day. You can use Postman for the request, 
or even a browser. 

Example request that fetches all predictions available for a specified city:
```sh
http://your_domain/predictions/ΘΕΣΣΑΛΟΝΙΚΗ
```

Example request that fetches predictions for a city for a specific day:
```sh
http://your_domain/predictions/ΘΕΣΣΑΛΟΝΙΚΗ/2020-10-03
```

Example request that fetches predictions for a city, for a given time of a day: 
```sh
http://your_domain/predictions/ΘΕΣΣΑΛΟΝΙΚΗ/2020-10-03/21:00:00
```

## Contributing

Feel like you wanna have some fun? Or suggest improvements of any kind?

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


## License

Distributed under the MIT License. See `LICENSE` for more information.


## Contact

Stefanos Anastasiou - emperor_stef@yahoo.gr

Project Link: [https://github.com/stefanosAnastasiou/meteo-scraper-api](https://github.com/stefanosAnastasiou/meteo-scraper-api)

## Acknowledgements
* [GitHub Emoji Cheat Sheet](https://www.webpagefx.com/tools/emoji-cheat-sheet)
* [Img Shields](https://shields.io)
* [Choose an Open Source License](https://choosealicense.com)

[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=flat-square
[license-url]: https://choosealicense.com/licenses/mit/
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/stefanosanastasiou/
