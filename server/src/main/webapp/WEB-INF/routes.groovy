def longPeriod = localMode ? 0 : 24.hours
def shortPeriod = localMode ? 0 : 1.hour

get "/addCountryTask", forward: "/import/addCountryTask.groovy"
get "/indexLocations", forward: "/indexLocations.groovy"
get "/importStations", forward: "/import/importStations.groovy"
get "/scrapePageTask", forward: "/import/scrapePageTask.groovy"
get "/exportImport", forward: "/import/exportImport.groovy"
get "/importLight", forward: "/import/importLight.groovy"
get "/countries", forward: "/getCountries.groovy"
get "/country", forward: "/getCountries.groovy"
get "/cities/country/@country", forward: "/getCities.groovy?countryName=@country"
get "/stations/country/@country", forward: "/getStations.groovy?countryName=@country"
get "/stations/city/@city", forward: "/getStations.groovy?city=@city"
