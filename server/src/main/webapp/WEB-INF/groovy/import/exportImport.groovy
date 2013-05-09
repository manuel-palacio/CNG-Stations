defaultQueue.add url: "/import/deleteStationsTask", method: 'GET'

defaultQueue.add countdownMillis: 15000, url: "/import/copyStationsTask", method: 'GET'

memcache.clearAll()
