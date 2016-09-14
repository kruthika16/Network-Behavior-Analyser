
__author__ = 'KRUTHIKA'

import re
import mysql.connector
import urllib2
import cookielib
import sys
string = open('unclean DNS.txt').read()
new_str = re.sub('Standard [aA-zZ0-9 ]* A', ' ', string)
new_str2=re.sub('"',' ',new_str)
new_str3=re.sub(' ','',new_str2)
open('clean DNS.txt', 'w').write(new_str3)


inputFile = open ('clean DNS.txt', 'r')
outputFile = open ('pattern.txt', 'w')
for line in inputFile:
      for word in line.split(','):
        if 'www.' in word:
            outputFile.write(word)


cnx = mysql.connector.connect(user='root', password='root',
                              host='127.0.0.1',
                              database='nba1')
cursor = cnx.cursor()

n=0
i=1
j=1
usage="0"
alert=0
inputFile=open ('pattern.txt', 'r')
outputFile=open ('test.txt', 'w')
for line in inputFile:
      cursor.execute("SELECT COUNT(website_name) FROM website where website_name = (%s)",(line,))
      n=cursor.fetchone()[0]

      cursor.execute("SELECT COUNT(ban_website_name) FROM forbidden where ban_website_name = (%s)",(line,))
      flag=cursor.fetchone()[0]


      if n==0 and flag==0:
         cursor.execute("insert into website values (%s,%s)",(i,line))
         usage=usage+','+str(i)
         i = i+1

      elif n==0 and flag==1:
          print "Alert Network Admin"
          alert+=1
          cursor.execute("SELECT websiteID FROM forbidden where ban_website_name = (%s)",(line,))
          n=cursor.fetchone()[0]
          usage = usage+','+str(n)
      else:
          cursor.execute("SELECT websiteID FROM website where website_name = (%s)",(line,))
          n=cursor.fetchone()[0]
          usage = usage+','+str(n)
print usage

usage_list = map(int, usage.split(','))
print usage_list
session=zip(*[iter(usage_list)]*2)

#update user details and session details in database
username="lavanya"
date="1/12/2015"
cursor.execute("SELECT COUNT(userID) FROM user where username = (%s)",(username,))
n=cursor.fetchone()[0]
if(n==0):
        cursor.execute("insert into user values (%s,%s,%s,%s)", (2,username, usage, date))
#cursor.fetchmany(500)
sessionID=2000
for j in range(len(session)):
    str1=' ' . join(str(e) for e in session[j])
    cursor.execute("SELECT COUNT(sessionID) FROM session where sessionString = (%s)",(str1,))
    n=cursor.fetchone()[0]
    if(n==0):
        cursor.execute("insert into session values (%s,%s,%s)", (2, sessionID, str1))
        sessionID += 1

#close database connection
cnx.commit()
cnx.close()

cnx1 = mysql.connector.connect(user='root', password='root',
                              host='127.0.0.1',
                              database='nba1')
cursor1 = cnx1.cursor()

if(alert>3):
    username = "8277390255"
    passwd = "nexus7222"
    cursor1.execute("SELECT userID FROM user where username = (%s)",("lavanya",))
    n=cursor1.fetchone()[0]
    message = "******\nALERT : User with USER-ID: "+str(n)+" deviated from his usual web access pattern. Suspicious user network behaviour \n *********"
    number = "7259236086"
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
