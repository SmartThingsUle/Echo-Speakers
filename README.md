# Echo-Speakers
Use Echo like a Speaker to send messages from SmartThings

Amazon Echo has some method to send messages directly without request, this has been used by many programmers, by alexa.amazon.com site, you can get access to several functions. 

Time before I have release Mediarenderer Player, to use a lot of DLNA speakers to get messages. but now Amazon Echo has filled my house along DLNA speakers. 

I have mod my   Mediarenderer  code to use it with Echo and still have some functions useless from MediaRenderer.

Some developments use a server to make request to echo and get a cookie session to control the echos like a user is in the site.

My  development needs to get the cookie manually to use it , but in reward, you no need a external server to use it, is still in first stage, but for now, you can use speak() to send messages to echos, play, pause , change volume and see the media you are playing. I still need to save tracks, search media and more, I don't have amazon music services, and Pandora services just to test functions, maybe you can help me with that.

The cookie could expire and you must to get another but I have many days and the cookie still works, I don't known if the cookie will expire soon, but is not so much problem to update , if it last a month I´m fine.

To get the cookie you need to go to alexa.amazon.com, if you are in other country the site will redirect you to your country site.

This is the site from I get the info https://www.gehrig.info/alexa/Alexa.html , is very easy tutorial to get the cookie, we just need the **Domain** : https://alexa.amazon.com or the domain you get in the site, it depends of your country, the  **CSRF** and the **cookie** 

If you have problems to find the beginning and end of the cookie, try to use the site https://incarnate.github.io/curl-to-php , it will parse the curl command into pieces. just be careful to remove the **Cookie:** . from the  beginning if you use the site.

In my Chrome Browser I use the option copy as Curl(bash) and pasted that info en the parser site.


You can add the parameters obtained from alexa site in the iniSettings() secction of the Echo Connect App Code, it have some example how data is , but you must fill with your real info

Ready, you can add your devices from the Echo Connect app, I have some Echos and Fabriq speakers, and works fine.

tell me if works with others alexa third party  devices.


https://github.com/SmartThingsUle/Echo-Speakers/tree/master


  

 
