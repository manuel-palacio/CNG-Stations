import com.google.appengine.api.datastore.Entity


def results = datastore.execute {
    select keys from ReadableStation
    prefetchSize 400
    chunkSize 400
}

datastore.delete results


def now = new Date()
results = datastore.iterate {
    select all from Station
    where dateUpdated > now - 30
    prefetchSize 400
    chunkSize 400
}

def entities = new HashSet<String>()

results.each {
    Entity entity = new Entity("ReadableStation")
    entity.price = it.price
    entity.street = it.street
    entity.latitude = it.latitude
    entity.longitude = it.longitude
    entity.openingHours = it.openingHours
    entity.operatedBy = it.operatedBy
    entity.phoneNo = it.phoneNo
    entity.openingHours = it.openingHours
    entity.payment = it.payment
    entity.email = it.email
    entity.countryName = it.countryName
    entity.countryCode = it.countryCode
    entity.city = it.city
    entity.web = it.web
    entity.type = it.type
    entity.dateUpdated = it.dateUpdated
    entities << entity
}

datastore.put entities


//memcache.clearAll()
