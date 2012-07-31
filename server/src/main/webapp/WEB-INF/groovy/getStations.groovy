import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.Query
import net.sf.json.JSONArray
import static com.google.appengine.api.datastore.FetchOptions.Builder.withDefaults
import javax.servlet.http.HttpServletResponse

def query = new Query("Station")

if (params.countryCode) {
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
        json.add(["city": it.city, "longitude": it.longitude, "latitude": it.latitude, "street": it.street, "phoneNo":it.phoneNo,
        "openingHours":it.openingHours,"price":it.price, "countryName": it.countryName])
    }

    response.contentType = "application/json"
    response.setHeader("Cache-Control","max-age=" + 60 * 60 * 24 * 2)
    json.write(out)
} else {
    response.status = HttpServletResponse.SC_NOT_FOUND
}

