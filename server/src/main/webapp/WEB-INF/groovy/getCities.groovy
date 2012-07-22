import net.sf.json.JSONArray
import javax.servlet.http.HttpServletResponse

def results = datastore.iterate {
    select city:String from Station
    where countryCode == params.countryCode
}

def uniqueResults = new HashSet<String>()

results.each{
    uniqueResults << it.city
}

if (!uniqueResults.empty) {

    JSONArray json = new JSONArray()

    uniqueResults.each {
        json.add([it])
    }

    response.contentType = "application/json"
    response.setHeader("Cache-Control","max-age=" + 60 * 60 * 24 * 2)
    json.write(out)
} else {
    response.status = HttpServletResponse.SC_NOT_FOUND
}

