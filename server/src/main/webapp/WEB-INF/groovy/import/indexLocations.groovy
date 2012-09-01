import net.palacesoft.cngstation.server.dao.CngDao

def countries = CngDao.findCountries()

countries.each {

    defaultQueue.add url: "/indexTask",
                   method: 'GET', params: [countryName: it.getProperty("countryName").toString()]

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