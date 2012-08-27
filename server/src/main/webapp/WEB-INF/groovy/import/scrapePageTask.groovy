import com.google.appengine.api.datastore.Entity
import net.palacesoft.cngstation.server.scraper.ScraperFactory

def gasStations = []
try {
    gasStations = ScraperFactory.newInstance(params).scrapePage(params.pageUrl)
} catch (Exception exception) {
    mail.send from: "emanuel.palacio@gmail.com",
            to: "emanuel.palacio@gmail.com",
            subject: "Problem scraping with fuelstationservice " + params.pageUrl,
            textBody: exception.message
}

def entitiesToSave = new HashSet()

gasStations.each { station ->

    def entity = station as Entity
    entitiesToSave << entity
}
if (!entitiesToSave.empty) {
    datastore.put entitiesToSave
}
