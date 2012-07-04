import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.Query
import net.sf.json.JSONArray
import static com.google.appengine.api.datastore.FetchOptions.Builder.withDefaults

def query = new Query("Station")

if (params.country) {
    query.addFilter("country", Query.FilterOperator.EQUAL, params.country)
}

if (params.city) {
    query.addFilter("city", Query.FilterOperator.EQUAL, params.city)
}

PreparedQuery preparedQuery = datastore.prepare(query)

def results = preparedQuery.asList(withDefaults())


response.contentType = "application/json"

JSONArray json = new JSONArray()

results.each {
    json.add(["city": it.city, "longitude": it.longitude, "latitude": it.latitude, "street": it.street, "phoneNo":it.phoneNo,
    "openingHours":it.openingHours,"price":it.price])
}


json.write(out)

