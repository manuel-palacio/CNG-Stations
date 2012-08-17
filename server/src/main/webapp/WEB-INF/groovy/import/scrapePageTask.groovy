import com.google.appengine.api.datastore.Entity
import net.palacesoft.cngstation.server.scraper.ScraperFactory

def gasStations = ScraperFactory.newInstance(params).scrapePage(params.pageUrl)

def entitiesToSave = new HashSet()

gasStations.each { station ->

    def entity = station as Entity
    entitiesToSave << entity
}
datastore.put entitiesToSave
