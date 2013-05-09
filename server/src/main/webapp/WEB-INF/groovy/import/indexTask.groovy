import com.google.appengine.api.search.Index
import com.google.appengine.api.search.IndexSpec
import com.google.appengine.api.search.Results
import com.google.appengine.api.search.ScoredDocument
import com.google.appengine.api.search.SearchServiceFactory
import net.palacesoft.cngstation.server.dao.CngDao
import com.google.appengine.api.search.GeoPoint
import com.google.appengine.api.search.Document
import com.google.appengine.api.search.Field

public Index getIndex() {
    IndexSpec indexSpec = IndexSpec.newBuilder().setName("cngindex").build();
    return SearchServiceFactory.getSearchService().getIndex(indexSpec);
}

Index index = getIndex()

def stations = CngDao.findStationsByCountryName(params.countryName)


stations.each {

    def longitude = it.getProperty("longitude").toString()
    def latitude = it.getProperty("latitude").toString()

    def found = index.get(it.key.id.toString())

    if (!found) {
        GeoPoint geoPoint = new GeoPoint(Double.valueOf(latitude), Double.valueOf(longitude))

        Document document = Document.newBuilder().setId(it.key.id.toString()).addField(Field.newBuilder().setName("location")
                .setGeoPoint(geoPoint)).build()

        index.put(document)
    }
}