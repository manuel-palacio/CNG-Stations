def longPeriod = localMode ? 0 : 24.hours
def shortPeriod = localMode ? 0 : 1.hour

get "/importStations", forward: "/importStations.groovy"
get "/stations/country/@country", forward: "/getStations.groovy?country=@country"
get "/stations/city/@city", forward: "/getStations.groovy?city=@city"
