import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.Query
import net.palacesoft.cngstation.server.scraper.StationScraperNO
import net.palacesoft.cngstation.server.scraper.StationScraperSE
import groovyx.gaelyk.logging.GroovyLogger


def log = new GroovyLogger("import")

def urlScraper = [:]

urlScraper.put("SE", new StationScraperSE())
urlScraper.put("NO", new StationScraperNO())

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

        def query = new Query("Station")

        query.addFilter("longitude", Query.FilterOperator.EQUAL, station.longitude)
        query.addFilter("latitude", Query.FilterOperator.EQUAL, station.latitude)

        PreparedQuery preparedQuery = datastore.prepare(query)

        try {
            Entity found = preparedQuery.asSingleEntity()

            if (!found) {
                def entity = station as Entity
                entity.save()
            } else {
                found.street = station.street
                found.price = station.price
                found.openingHours = station.openingHours
                found.save()
            }
        } catch (Exception e) {
           log.severe("duplicate station " + station.toString() +  e.getMessage())
        }
    }
}




