import net.sf.json.JSONArray
import javax.servlet.http.HttpServletResponse

def results = datastore.iterate {
    select city:String from Station
    where country == params.country
}

def uniqueResults = new HashSet<String>()

results.each{
    uniqueResults << it.city
}

if (!uniqueResults.empty) {
    response.contentType = "application/json"

    JSONArray json = new JSONArray()

    uniqueResults.each {
        json.add([it])
    }

    response.setHeader("Cache-Control","max-age=" + 60 * 60 * 24 * 2)
    json.write(out)
} else {
    response.status = HttpServletResponse.SC_NOT_FOUND
}

