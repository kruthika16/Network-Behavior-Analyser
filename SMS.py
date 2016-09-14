__author__ = 'KRUTHIKA'

import re
import mysql.connector
import urllib2
import cookielib
import sys

cnx1 = mysql.connector.connect(user='root', password='root',
                              host='127.0.0.1',
                              database='nba1')
cursor1 = cnx1.cursor()

#if(DB.alert>6):
username = "8277390255"
passwd = "nexus7222"
cursor1.execute("SELECT userID FROM user where username = (%s)",("lavanya",))
n=cursor1.fetchone()
print n
message = "******\nALERT : User with USER-ID:"+str(n)+" deviated from his usual web access pattern. Suspicious user network behaviour \n *********"
number = "8277390255"
message = "+".join(message.split(' '))

#Logging into the SMS Site
url = 'http://site24.way2sms.com/Login1.action?'
data = 'username='+username+'&password='+passwd+'&Submit=Sign+in'

#For Cookies:
cj = cookielib.CookieJar()
opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(cj))

# Adding Header detail:
opener.addheaders = [('User-Agent', 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36')]

try:
    usock = opener.open(url, data)
except IOError:
    print "Error while logging in."
    sys.exit(1)

jession_id = str(cj).split('~')[1].split(' ')[0]
send_sms_url = 'http://site24.way2sms.com/smstoss.action?'
send_sms_data = 'ssaction=ss&Token='+jession_id+'&mobile='+number+'&message='+message+'&msgLen=136'
opener.addheaders = [('Referer', 'http://site25.way2sms.com/sendSMS?Token='+jession_id)]

try:
    sms_sent_page = opener.open(send_sms_url, send_sms_data)
except IOError:
    print "Error while sending message"

cnx1.commit()
cnx1.close()