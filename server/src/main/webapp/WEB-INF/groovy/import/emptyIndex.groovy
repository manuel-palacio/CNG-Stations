import com.google.appengine.api.search.*


def Index getIndex() {
    IndexSpec indexSpec = IndexSpec.newBuilder().setName("cngindex").build();
    return SearchServiceFactory.getSearchService().getIndex(indexSpec);
}

def emptyIndex() {
    def index = getIndex()
    while (true) {
            List<String> docIds = new ArrayList<String>();
            // Return a set of document IDs.
            GetRequest request = GetRequest.newBuilder().setReturningIdsOnly(true).build();
            GetResponse<Document> response = getIndex().getRange(request);
            if (response.getResults().isEmpty()) {
                break;
            }
            for (Document doc : response) {
                docIds.add(doc.getId());
            }
            index.delete(docIds);
        }
}

emptyIndex()
