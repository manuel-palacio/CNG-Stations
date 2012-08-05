import net.sf.json.JSONArray
import javax.servlet.http.HttpServletResponse


if ('cities_' + params.countryName in memcache) {
    JSONArray json = memcache['cities_' + params.countryName]
    outputData(json)

} else {


    def results = datastore.iterate {
        select city: String from Station
        where countryName == params.countryName
    }

    def uniqueResults = new HashSet<String>()

    results.each {
        uniqueResults << it.city
    }

    if (!uniqueResults.empty) {

        JSONArray json = new JSONArray()

        uniqueResults.sort().each {
            json.add(["city": it])
        }

        memcache["cities_" + params.countryName] = json
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

