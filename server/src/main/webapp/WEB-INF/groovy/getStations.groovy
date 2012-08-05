import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.Query
import net.sf.json.JSONArray
import static com.google.appengine.api.datastore.FetchOptions.Builder.withDefaults
import javax.servlet.http.HttpServletResponse



if (params.countryName && 'stations_' + params.countryName in memcache) {
    JSONArray json = memcache['stations_' + params.countryName]
    outputData(json)

} else if (params.city && 'stations_' + params.city in memcache) {
    JSONArray json = memcache['stations_' + params.city]
    outputData(json)

} else {
    def query = new Query("Station")

    if (params.countryName) {
        query.addFilter("countryName", Query.FilterOperator.EQUAL, params.countryName)
    }

    if (params.city) {
        query.addFilter("city", Query.FilterOperator.EQUAL, params.city)
    }

    PreparedQuery preparedQuery = datastore.prepare(query)

    def results = preparedQuery.asList(withDefaults())


    if (!results.empty) {

        JSONArray json = new JSONArray()

        results.each {
            json.add(["city": it.city, "longitude": it.longitude, "latitude": it.latitude, "street": it.street, "phoneNo": it.phoneNo,
                    "openingHours": it.openingHours, "price": it.price, "countryName": it.countryName])
        }

        if (params.countryName) {
            memcache["stations_" + params.countryName] = json
        }

        if (params.city) {
            memcache["stations_" + params.city] = json
        }

        outputData(json)
    } else {
        response.status = HttpServletResponse.SC_NOT_FOUND
    }

}

private def outputData(def json) {
    response.contentType = "application/json"
    response.setHeader("Cache-Control", "public, max-age=" + 604800)
    json.write(out)
}

