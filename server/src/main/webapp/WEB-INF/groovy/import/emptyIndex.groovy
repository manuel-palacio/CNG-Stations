import com.google.appengine.api.search.ListRequest
import com.google.appengine.api.search.Document
import com.google.appengine.api.search.ListResponse
import com.google.appengine.api.search.Consistency


def index = search.index("myindex", Consistency.PER_DOCUMENT)


emptyIndex()

def emptyIndex() {
    while (true) {
        List<String> docIds = new ArrayList<String>();
        // Return a set of document IDs.
        ListRequest request = ListRequest.newBuilder().setKeysOnly(true).build();
        ListResponse<Document> response = index.listDocuments(request);
        if (response.getResults().isEmpty()) {
            break;
        }
        for (Document doc : response) {
            docIds.add(doc.getId());
        }
        index.remove(docIds);
    }
}