import net.sf.json.JSONArray
import javax.servlet.http.HttpServletResponse
import net.palacesoft.cngstation.server.persistence.CngDao


if ('countries' in memcache) {
    String json = memcache['countries']
    outputData(json)
} else {

    def countries = CngDao.findCountries()

    if (!countries.empty) {

        JSONArray json = new JSONArray()

        countries.each {
            json.add(["countryName": it.countryName])
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
//    response.setHeader("Cache-Control", "public, max-age=" + 604800)
    print json
}

