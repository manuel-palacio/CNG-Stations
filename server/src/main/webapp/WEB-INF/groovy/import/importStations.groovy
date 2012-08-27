import com.google.appengine.api.taskqueue.RetryOptions
import groovyx.gaelyk.logging.GroovyLogger
import net.palacesoft.cngstation.server.scraper.ScraperFactory

def log = new GroovyLogger("importStations")

def pagesToScrape = []

try {

    pagesToScrape = ScraperFactory.newInstance(params).getUrlsToScrape(params.baseUrl)
} catch (Exception exception) {
    mail.send from: "emanuel.palacio@gmail.com",
            to: "emanuel.palacio@gmail.com",
            subject: "Problem scraping with fuelstationservice " + params.baseUrl,
            textBody: exception.message

}

pagesToScrape.each {
    queues['optimized-queue'].add url: "/scrapePageTask",
            method: 'GET', params: [pageUrl: it, countryCode: params.countryCode, countryName: params.countryName]
}


if (!pagesToScrape.empty) {
    defaultQueue.add url: "/addCountryTask",
               method: 'GET', params: [countryName: params.countryName]
}









