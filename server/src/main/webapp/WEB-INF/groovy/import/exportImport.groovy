defaultQueue.add url: "/deleteStationsTask", method: 'GET'

defaultQueue.add countdownMillis: 15000, url: "/copyStationsTask", method: 'GET'

memcache.clearAll()
