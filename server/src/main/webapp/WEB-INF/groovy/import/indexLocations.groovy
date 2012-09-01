import net.palacesoft.cngstation.server.dao.CngDao

def countries = CngDao.findCountries()

countries.each {

    defaultQueue.add url: "/import/indexTask",
                   method: 'GET', params: [countryName: it.getProperty("countryName").toString()]

}


