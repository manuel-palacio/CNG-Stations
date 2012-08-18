import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.Query
import net.sf.json.JSONArray
import static com.google.appengine.api.datastore.FetchOptions.Builder.withDefaults
import javax.servlet.http.HttpServletResponse



if (params.countryName && 'stations_' + params.countryName in memcache) {
    String json = memcache['stations_' + params.countryName]
    outputData(json)

} else if (params.city && 'stations_' + params.city in memcache) {
    String json = memcache['stations_' + params.city]
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
                    "openingHours": it.openingHours, "price": it.price, "filteredPrice": filterPrice(it.price), "countryName": it.countryName])
        }

        String jsonString = json.toString()
        if (params.countryName) {
            memcache["stations_" + params.countryName] = jsonString
        }

        if (params.city) {
            memcache["stations_" + params.city] = jsonString
        }

        outputData(jsonString)
    } else {
        response.status = HttpServletResponse.SC_NOT_FOUND
    }

}

def filterPrice(String price) {
    price = price.replace(",",".")
    def regExp = price =~ /(\d+.\d+)(\W*kg)/
    if(regExp.size() == 0) return price
    regExp[0][1]
}

def outputData(def json) {
    response.contentType = "application/json"
    // response.setHeader("Cache-Control", "public, max-age=" + 604800)
    out << json
}

