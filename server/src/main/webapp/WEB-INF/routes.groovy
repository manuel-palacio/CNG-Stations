def longPeriod = localMode ? 0 : 24.hours
def shortPeriod = localMode ? 0 : 1.hour

get "/import/addCountryTask", forward: "/import/addCountryTask.groovy"
get "/import/deleteStationsTask", forward: "/import/deleteStationsTask.groovy"
get "/import/copyStationsTask", forward: "/import/copyStationsTask.groovy"
get "/import/indexLocations", forward: "/import/indexLocations.groovy"
get "/import/indexTask", forward: "/import/indexTask.groovy"
get "/import/importStations", forward: "/import/importStations.groovy"
get "/import/scrapePageTask", forward: "/import/scrapePageTask.groovy"
get "/import/exportImport", forward: "/import/exportImport.groovy"
get "/import/importLight", forward: "/import/importLight.groovy"
get "/countries", forward: "/getCountries.groovy"
get "/country", forward: "/getCountries.groovy"
get "/cities/country/@country", forward: "/getCities.groovy?countryName=@country"
get "/stations/country/@country", forward: "/getStations.groovy?countryName=@country"
get "/stations/city/@city", forward: "/getStations.groovy?city=@city"
get "/stations2/city/@city", forward: "/getNearByStations.groovy?city=@city"
get "/stations/near/@city", forward: "/getNearByStations.groovy?city=@city"
