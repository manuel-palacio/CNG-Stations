import com.google.appengine.api.datastore.Entity

def country = datastore.execute {
    select single from Country
    where countryName == params.countryName
}

if (!country) {
    country = new Entity("Country")
    country.countryName = params.countryName
    datastore.put country
}
