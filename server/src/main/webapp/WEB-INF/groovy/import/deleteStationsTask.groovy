def results = datastore.execute {
    select keys from ReadableStation
    prefetchSize 400
    chunkSize 400
}

datastore.delete results
