def longPeriod = localMode ? 0 : 24.hours
def shortPeriod = localMode ? 0 : 1.hour

get "/importStations", forward: "/importStations.groovy"
get "/countries", forward: "/getCountries.groovy"
get "/cities/country/@country", forward: "/getCities.groovy?countryName=@country"
get "/stations/country/@country", forward: "/getStations.groovy?countryName=@country"
get "/stations/city/@city", forward: "/getStations.groovy?city=@city"
