import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.search.Consistency
import com.google.appengine.api.search.Results
import com.google.appengine.api.search.ScoredDocument
import net.palacesoft.cngstation.server.dao.CngDao
import net.sf.json.JSONArray

import javax.servlet.http.HttpServletResponse

if ('stations_' + params.city + "_distance_" + params.distance in memcache) {
    String json = memcache['stations_' + params.city + "_distance_" + params.distance]
    outputData(json)

} else {

    def stations = getNearbyStations()

    if (stations.hasNext()) {
        outputData(cache(asJson(stations)))
    } else {
        response.status = HttpServletResponse.SC_NOT_FOUND
    }

}

private def getNearbyStations() {
    def stations = []
    String latitude = params.latitude
    String longitude = params.longitude

    if (latitude && longitude) {
        latitude = convertToDegrees(latitude)
        longitude = convertToDegrees(longitude)
        def index = search.index("myindex", Consistency.PER_DOCUMENT)

        String distance = "30"
        if(params.distance){
           distance = params.distance
        }

        String queryStr = "distance(location, geopoint(${latitude}, ${longitude})) < ${distance}000"

        Results<ScoredDocument> results = index.search(queryStr)
        results.each {
            Entity station = CngDao.findStationById(it.id)
            if (station) {
                stations << station
            }
        }

    }

    stations.iterator()
}

private def convertToDegrees(String value) {
    if (!value.contains(".")) {
        return Double.valueOf(value) / 1E6
    }

    value
}

private def cache(String jsonString) {

    if (params.city) {
        memcache["stations_" + params.city + "_distance_" + params.distance] = jsonString
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

