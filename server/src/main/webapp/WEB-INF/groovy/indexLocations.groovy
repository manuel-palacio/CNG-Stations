import com.google.appengine.api.search.*;

import net.palacesoft.cngstation.server.dao.CngDao

def countries = CngDao.findCountries()

def index = search.index("myindex", Consistency.PER_DOCUMENT)
countries.each {
    def stations = CngDao.findStationsByCountryName(it.getProperty("countryName").toString())


    stations.each {

        GeoPoint geoPoint = new GeoPoint(Double.valueOf(it.getProperty("latitude").toString()),
                Double.valueOf(it.getProperty("longitude").toString()))

        Document document = Document.newBuilder().setId(it.key.id.toString()).addField(Field.newBuilder().setName("location")
                .setGeoPoint(geoPoint)).build()

        index.add(document)

    }
}

/*def emptyIndex() {
    while (true) {
        List<String> docIds = new ArrayList<String>();
        // Return a set of document IDs.
        ListRequest request = ListRequest.newBuilder().setKeysOnly(true).build();
        ListResponse<Document> response = getIndex().listDocuments(request);
        if (response.getResults().isEmpty()) {
            break;
        }
        for (Document doc : response) {
            docIds.add(doc.getId());
        }
        getIndex().remove(docIds);
    }
}*/