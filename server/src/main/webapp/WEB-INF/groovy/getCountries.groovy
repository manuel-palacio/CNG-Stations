import net.sf.json.JSONArray
import javax.servlet.http.HttpServletResponse


if ('countries' in memcache) {
    JSONArray json = memcache['countries']
    outputData(json)

} else {
    def results = datastore.iterate {
        select countryName: String from Station
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

        memcache["countries"] = json

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

