package net.palacesoft.cngstation.server.scraper



def m = "pepöe 028-252000" =~ /([\w\W]*\s)(\d*[-][\d\s]*)/


print m[0][2]