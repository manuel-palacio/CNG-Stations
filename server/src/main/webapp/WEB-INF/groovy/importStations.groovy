import com.google.appengine.api.datastore.Entity
import groovyx.gaelyk.logging.GroovyLogger
import net.palacesoft.cngstation.server.scraper.StationScraperNO
import net.palacesoft.cngstation.server.scraper.StationScraperSE

def log = new GroovyLogger("importStations")

def urlScraper = [:]

urlScraper.put("SE", new StationScraperSE())
urlScraper.put("NO", new StationScraperNO())

def entitiesToSave = []

urlScraper.each { scraper ->

    def stations = Collections.emptyList()
    try {
        stations = scraper.value.scrape()
    } catch (Exception exception) {
        mail.send from: "emanuel.palacio@gmail.com",
                to: "emanuel.palacio@gmail.com",
                subject: "Problem scraping with fuelstationservice ${scraper.toString()}",
                textBody: exception.message

    }
    stations.each { station ->

        def entity = station as Entity
        entitiesToSave << entity
    }

    datastore.put entitiesToSave
}




