import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.search.Consistency
import com.google.appengine.api.search.Results
import com.google.appengine.api.search.ScoredDocument
import net.palacesoft.cngstation.server.dao.CngDao
import net.sf.json.JSONArray

import javax.servlet.http.HttpServletResponse

if (params.countryName && 'stations_' + params.countryName in memcache) {
    String json = memcache['stations_' + params.countryName]
    outputData(json)

} else if (params.city && 'stations_' + params.city in memcache) {
    String json = memcache['stations_' + params.city]
    outputData(json)

} else {

    def stations = []

    if (params.city) {
        stations = CngDao.findStationsByCity(params.city)
    }

    if (params.countryName) {
        stations = CngDao.findStationsByCountryName(params.countryName)
    }

    if (!stations.empty) {
        outputData(cache(asJson(stations)))
    } else {
        stations = getNearbyStations()

        if (stations.empty) {
            response.status = HttpServletResponse.SC_NOT_FOUND
        } else {
            outputData(cache(asJson(stations)))
        }
    }

}

private def getNearbyStations() {
    def stations = []
    String latitude = params.latitude
    String longitude = params.longitude

    if (latitude && longitude) {
        def index = search.index("myindex", Consistency.PER_DOCUMENT)

        String queryStr = "distance(location, geopoint(${latitude}, ${longitude})) < 30000"

        Results<ScoredDocument> results = index.search(queryStr)
        results.each {
            Entity station = CngDao.findStationById(it.id)
            if (station) {
                stations << station
            }
        }

    }

    stations
}

private def cache(String jsonString) {
    if (params.countryName) {
        memcache["stations_" + params.countryName] = jsonString
    }

    if (params.city) {
        memcache["stations_" + params.city] = jsonString
    }

    jsonString
}


def asJson(def stations) {
    JSONArray json = new JSONArray()

    stations.each {
        json.add(["city": it.city, "longitude": it.longitude, "latitude": it.latitude, "street": it.street, "phoneNo": it.phoneNo,
                "openingHours": it.openingHours, "price": it.price, "filteredPrice": filterPrice(it.price), "countryName": it.countryName])
    }

    json.toString()
}

def filterPrice(String price) {
    price = price?.replace(",", ".")
    def regExp = price =~ /(\d+.\d+)(\W*kg)/
    if (regExp.size() == 0) return price
    regExp[0][1]
}

def outputData(def json) {
    response.contentType = "application/json"
    // response.setHeader("Cache-Control", "public, max-age=" + 604800)
    out << json
}

