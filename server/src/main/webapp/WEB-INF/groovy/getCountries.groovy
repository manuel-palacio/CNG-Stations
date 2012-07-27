import net.sf.json.JSONArray
import javax.servlet.http.HttpServletResponse

def results = datastore.iterate {
    select countryCode:String from Station
}

def uniqueResults = new HashSet<String>()

results.each {
    uniqueResults << it.countryCode
}

if (!uniqueResults.empty) {

    JSONArray json = new JSONArray()

    uniqueResults.sort().each {
        json.add(["countryCode":it])
    }

    response.contentType = "application/json"
    response.setHeader("Cache-Control", "max-age=" + 60 * 60 * 24 * 2)
    json.write(out)
} else {
    response.status = HttpServletResponse.SC_NOT_FOUND
}

