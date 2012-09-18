package net.palacesoft.cngstation

import net.palacesoft.cngstation.server.model.Country



def m = "pepöe 028-252000" =~ /([\w\W]*\s)(\d*[-][\d\s]*)/


println m[0][2]


def m2 = "13,3 CHF/kg" =~ /(\d+,\d+)(.*kg)/

println m2.size()
println m2[0][1]


println Country.values().find {it.countryCode == "SE"}

class Car {
    Double mileage
}


println new Car(mileage: 3.5).mileage


println convertToDegrees("59.306073")


private def convertToDegrees(String value) {
    if (!value.contains(".")) {
        return Double.valueOf(value) / 1E6
    }

    value
}


println 1.8119442299999998E-5 * 1E6
