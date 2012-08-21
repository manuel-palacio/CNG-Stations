package net.palacesoft.cngstation

import net.palacesoft.cngstation.server.model.Country



def m = "pepöe 028-252000" =~ /([\w\W]*\s)(\d*[-][\d\s]*)/


println m[0][2]


def m2 = "13,3" =~ /(\d+,\d+)(\W*kg)/

println m2.size()
//println m2[0][1]


println Country.values().find {it.countryCode == "SE"}
