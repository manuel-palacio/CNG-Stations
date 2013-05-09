import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.search.Index
import com.google.appengine.api.search.IndexSpec
import com.google.appengine.api.search.Results
import com.google.appengine.api.search.ScoredDocument
import com.google.appengine.api.search.SearchServiceFactory
import net.palacesoft.cngstation.server.dao.CngDao
import net.sf.json.JSONArray


if ('stations_' + params.city + "_distance_" + params.distance in memcache) {
    String json = memcache['stations_' + params.city + "_distance_" + params.distance]
    outputData(json)

} else {

    def stations = getNearbyStations()

    if (stations.hasNext()) {
        outputData(cache(asJson(stations)))
    } else {
        forward '/getStations.groovy'
    }
}

def Index getIndex() {
    IndexSpec indexSpec = IndexSpec.newBuilder().setName("cngindex").build();
    return SearchServiceFactory.getSearchService().getIndex(indexSpec);
}

private def getNearbyStations() {
    def stations = []
    String latitude = params.latitude
    String longitude = params.longitude

    if (latitude && longitude) {
        latitude = convertToDegrees(latitude)
        longitude = convertToDegrees(longitude)


        String distance = "30"
        if(params.distance){
           distance = params.distance
        }

        String queryStr = "distance(geopoint(${latitude}, ${longitude}),location) < ${distance}000"
        Results<ScoredDocument> results = null
        try {
            results = getIndex().search(queryStr)
        } catch (Exception e) {
            //probable quota exceeded
        }
        results?.each {
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

