import net.sf.json.JSONArray

def results = datastore.iterate {
    select city:String from Station
    where country == params.country
}

def uniqueResults = new HashSet<String>()

results.each{
    uniqueResults << it.city
}

response.contentType = "application/json"

JSONArray json = new JSONArray()

uniqueResults.each {
    json.add([it])
}


json.write(out)

