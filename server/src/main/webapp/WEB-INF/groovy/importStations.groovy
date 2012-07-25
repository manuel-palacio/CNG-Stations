import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.Query
import net.palacesoft.cngstation.server.scraper.StationScraperNOR
import net.palacesoft.cngstation.server.scraper.StationScraperSWE

def urlScraper = [:]

urlScraper.put("SWE", new StationScraperSWE())
urlScraper.put("NOR", new StationScraperNOR())

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
        query.addFilter("street", Query.FilterOperator.EQUAL, station.street)
        query.addFilter("city", Query.FilterOperator.EQUAL, station.city)

        PreparedQuery preparedQuery = datastore.prepare(query)

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
    }
}




