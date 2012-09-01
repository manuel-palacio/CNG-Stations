import net.palacesoft.cngstation.server.dao.CngDao
import com.google.appengine.api.search.Consistency
import com.google.appengine.api.search.GeoPoint
import com.google.appengine.api.search.Document
import com.google.appengine.api.search.Field

def index = search.index("myindex", Consistency.PER_DOCUMENT)


def stations = CngDao.findStationsByCountryName(params.countryName)


stations.each {

    GeoPoint geoPoint = new GeoPoint(Double.valueOf(it.getProperty("latitude").toString()),
            Double.valueOf(it.getProperty("longitude").toString()))

    Document document = Document.newBuilder().setId(it.key.id.toString()).addField(Field.newBuilder().setName("location")
            .setGeoPoint(geoPoint)).build()

    index.add(document)

}