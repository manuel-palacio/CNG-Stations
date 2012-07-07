import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.Query
import net.palacesoft.cngstation.server.scraper.StationScraperSE

def urlScraper = [:]

urlScraper.put("SE", new StationScraperSE())

urlScraper.each {
    def stations = it.value.scrape()
    stations.each {

        def query = new Query("Station")

        query.addFilter("longitude", Query.FilterOperator.EQUAL, it.longitude)
        query.addFilter("latitude", Query.FilterOperator.EQUAL, it.latitude)
        query.addFilter("city", Query.FilterOperator.EQUAL, it.city)

        PreparedQuery preparedQuery = datastore.prepare(query)

        Entity found = preparedQuery.asSingleEntity()

        if (!found) {
            def entity = it as Entity
            entity.save()
        } else {
            found.street = it.street
            found.openingHours = it.openingHours
            found.save()
        }

    }
}




