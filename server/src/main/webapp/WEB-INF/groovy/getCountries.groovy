import net.sf.json.JSONArray
import javax.servlet.http.HttpServletResponse
import com.google.appengine.api.datastore.Query


if ('countries' in memcache) {
    String json = memcache['countries']
    outputData(json)

} else {


    def results = datastore.execute {
        select countryName: String from Station
        chunkSize 100
    }

    def uniqueResults = new HashSet<String>()

    results.each {
        uniqueResults << it.countryName
    }

    if (!uniqueResults.empty) {

        JSONArray json = new JSONArray()

        uniqueResults.sort().each {
            json.add(["countryName": it])
        }

        String jsonString = json.toString()
        memcache["countries"] = jsonString

       outputData(jsonString)
    } else {
        response.status = HttpServletResponse.SC_NOT_FOUND
    }

}

private def outputData(def json) {
    response.contentType = "application/json"
    response.setHeader("Cache-Control", "public, max-age=" + 604800)
    print json
}

