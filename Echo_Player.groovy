/** 
 *  Echo Player v1.3.1
 *
 *  Author: SmartThings - Ulises Mujica (Ule)
 
 
 
 *  1.4.0
 *
 *  Added some speak commands in speak function , now you can ask  Weather,Traffic,FlashBriefing,GoodMorning,SingASong,TellStory in speak command
 *  Restore volume is not possible due a undefined lenght of phrase, but you can force a restore volume if you set a delay
 *  Example speak("weather") or speak("tellStory",50) or speak("tellStory",50, 20)
 *
 *  1.3.1
 *
 *  Speak Parametrs Added, you can use speak with volume change and custom delay, volume and delay are optional speak(Message required, Volume optional, Delay optional) 
 *  speak("The door is open") TTS
 *  speak("The door is open", 50) TTS If New Volumen != Current Volume it changes and after some time the volume is restored, the delay is message/17 
 *  speak("The door is open", 50, 4) TTS If New Volumen != Current Volume it changes and after custom delay the volume is restored 
 *  Fix Volume value, each Update Status the volume is updated even if no media is playng  
 *  Fix Status, Added "IDLE" and "Standby" status
 *
 *  1.2.0
 *
 *  createReminder  Added, you can create a new Reminder with command "createReminder", ["string","string"] 
 *  Format Date yyyy-MM-ddTHH:mm  -> 2018-12-25T00:01
 *  Example createReminder("2018-12-25T00:01" , "Christmas Time")
 *
 *  1.1.0
 *  Search TuneIn Genres added, Like DLNA speaker, you can search Christmas, Jazz, French, etc.  stations
 *
 *  playStation(station, genre) it allows to send increments in stations or genres based in your genres settings
 *  ex playStation(0,0) paly the actual station in current genre
 *  ex playStation(1,0) paly the next station in current genre
 *  ex playStation(0,1) paly the station in next genre
 *  ex playStation(-1,0) paly the previous station in current genre
 *  ex playStation(0,-1) paly the station in previous genre
 *

 
 */


preferences {
		input(name: "customDelay", type: "enum", title: "Delay before msg (seconds)", options: ["0","1","2","3","4","5"])
		input(name: "actionsDelay", type: "enum", title: "Delay between actions (seconds)", options: ["0","1","2","3"])
        input "genres", "text", title: "Multiple Searches, comma separator", required: false, description:"Smooth Jazz,Christmas"
}
metadata {
	// Automatically generated. Make future change here.
	definition (name: "Echo", namespace: "mujica", author: "SmartThings-Ulises Mujica") {
		capability "Actuator"
		capability "Switch"
		capability "Refresh"
		capability "Sensor"
		capability "Music Player"
		capability "Polling"
        capability "Speech Synthesis"

		attribute "model", "string"
		attribute "trackUri", "string"
		attribute "transportUri", "string"
		attribute "trackNumber", "string"
		attribute "doNotDisturb", "string"
		


			command "subscribe"
		command "getVolume"
		command "getCurrentMedia"
		command "getCurrentStatus"
		command "seek"
		command "setLocalLevel", ["number"]
		command "tileSetLevel", ["number"]
		command "playTextAndResume", ["string","number"]
		command "playTextAndRestore", ["string","number"]
        	command "unsubscribe"
			command "playTrackAtVolume", ["string","number"]
			command "playTrackAndResume", ["string","number","number"]
			command "playTrackAndRestore", ["string","number","number"]
			command "playSoundAndTrack", ["string","number","json_object","number"]
			command "playTextAndResume", ["string","json_object","number"]
			command "setDoNotDisturb", ["string"]
		command "switchDoNotDisturb"
        command "switchBtnMode"
        command "speak", ["string"]
        command "searchTuneIn", ["string"]
        command "playStation", ["number","number"]
        command "previousStation"
        command "nextStation"
        command "previousGenre"
        command "nextGenre"
        command "playGenre", ["string"]
        command "createReminder", ["string","string"] // ex 2018-12-25T00:01 , Christmas Time
	}

	// Main
	standardTile("main", "device.status", width: 1, height: 1, canChangeIcon: true) {
		state "playing", label:'Playing', action:"music Player.stop", icon:"st.Electronics.electronics16", nextState:"paused", backgroundColor:"#79b821"
		state "stopped", label:'Stopped', action:"music Player.play", icon:"st.Electronics.electronics16", backgroundColor:"#ffffff"
		state "paused", label:'Paused', action:"music Player.play", icon:"st.Electronics.electronics16", nextState:"playing", backgroundColor:"#ffffff"
		state "no_media_present", label:'No Media', icon:"st.Electronics.electronics16", backgroundColor:"#ffffff"
        state "idle", label:'IDLE', icon:"st.Electronics.electronics16", backgroundColor:"#ffffff"
        state "standby", label:'Standby', icon:"st.Electronics.electronics16", backgroundColor:"#b6b6b4"
        state "no_device_present", label:'No Present', icon:"st.Electronics.electronics16", backgroundColor:"#b6b6b4"
		state "grouped", label:'Grouped', icon:"st.Electronics.electronics16", backgroundColor:"#ffffff"
	}

	// Row 1
	standardTile("nextTrack", "device.btnMode", width: 1, height: 1, decoration: "flat") {
        state "default", label:'', action:"nextTrack", icon:"st.sonos.next-btn", backgroundColor:"#ffffff",nextState:"default"
        state "station", label:'Next Station', action:"nextStation", icon:"http://urbansa.com/icons/next-btn@2x.png", backgroundColor:"#ffffff",nextState:"station"
        state "genre", label:'Next Genre', action:"nextGenre", icon:"http://urbansa.com/icons/next-btn@2x.png", backgroundColor:"#ffffff",nextState:"genre"
	}
	standardTile("play", "device.btnMode", width: 1, height: 1, decoration: "flat") {
		state "default", label:'', action:"play", icon:"st.sonos.play-btn", nextState:"default", backgroundColor:"#ffffff"
        state "station", label:'Play Station', action:"playStation", icon:"http://urbansa.com/icons/play-btn@2x.png", nextState:"station", backgroundColor:"#ffffff"
        state "genre", label:'Play Station', action:"playStation", icon:"http://urbansa.com/icons/play-btn@2x.png", nextState:"genre", backgroundColor:"#ffffff"
	}
	standardTile("previousTrack", "device.btnMode", width: 1, height: 1, decoration: "flat") {
		state "default", label:'', action:"previousTrack", icon:"st.sonos.previous-btn", backgroundColor:"#ffffff",nextState:"default"
        state "station", label:'Prev Station', action:"previousStation", icon:"http://urbansa.com/icons/previous-btn@2x.png", backgroundColor:"#ffffff",nextState:"station"
        state "genre", label:'Prev Genre', action:"previousGenre", icon:"http://urbansa.com/icons/previous-btn@2x.png", backgroundColor:"#ffffff",nextState:"genre"
	}

	// Row 2
    controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 1, inactiveLabel: false) {
		state "level", action:"tileSetLevel", backgroundColor:"#ffffff"
	}
	standardTile("stop", "device.status", width: 1, height: 1, decoration: "flat") {
		state "default", label:'', action:"music Player.pause", icon:"st.sonos.pause-btn", backgroundColor:"#ffffff"
		state "grouped", label:'', action:"music Player.pause", icon:"st.sonos.pause-btn", backgroundColor:"#ffffff"
	}
	standardTile("mute", "device.mute", inactiveLabel: false, decoration: "flat") {
		state "unmuted", label:"", action:"music Player.mute", icon:"st.custom.sonos.unmuted", backgroundColor:"#ffffff", nextState:"muted"
		state "muted", label:"", action:"music Player.unmute", icon:"st.custom.sonos.muted", backgroundColor:"#ffffff", nextState:"unmuted"
	}

	// Row 3
	
	// Row 4
	valueTile("currentSong", "device.trackDescription", inactiveLabel: true, height:1, width:3, decoration: "flat") {
		state "default", label:'${currentValue}', backgroundColor:"#ffffff"
	}

	
	// Row 5
	standardTile("status", "device.status", width: 1, height: 1, decoration: "flat", canChangeIcon: true) {
		state "playing", label:'Playing', action:"music Player.stop", icon:"st.Electronics.electronics16", nextState:"paused", backgroundColor:"#ffffff"
		state "stopped", label:'Stopped', action:"music Player.play", icon:"st.Electronics.electronics16", nextState:"playing", backgroundColor:"#ffffff"
		state "no_media_present", label:'No Media', icon:"st.Electronics.electronics16", backgroundColor:"#ffffff"
		state "no_device_present", label:'No Present', icon:"st.Electronics.electronics16", backgroundColor:"#ffffff"
		state "paused", label:'Paused', action:"music Player.play", icon:"st.Electronics.electronics16", nextState:"playing", backgroundColor:"#ffffff"
        state "idle", label:'IDLE', icon:"st.Electronics.electronics16", backgroundColor:"#ffffff"
        state "standby", label:'Standby', icon:"st.Electronics.electronics16", backgroundColor:"#ffffff"
	}
	standardTile("refresh", "device.status", inactiveLabel: false, decoration: "flat") {
		state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh", backgroundColor:"#ffffff"
	}
    standardTile("btnMode", "device.btnMode", width: 1, height: 1, decoration: "flat", canChangeIcon: true) {
		state "default", label:"Normal", action:"switchBtnMode", icon:"st.Electronics.electronics14",nextState:"station"
		state "station", label:"Station", action:"switchBtnMode", icon:"st.Entertainment.entertainment2",nextState:"genre"
        state "genre", label:"Genre", action:"switchBtnMode", icon:"st.Electronics.electronics1",nextState:"normal"
	}
	standardTile("doNotDisturb", "device.doNotDisturb", width: 1, height: 1, decoration: "flat", canChangeIcon: true) {
		state "off", label:"MSG Enabled", action:"switchDoNotDisturb", icon:"st.alarm.beep.beep",nextState:"on"
		state "on", label:"MSG Disabled", action:"switchDoNotDisturb", icon:"st.custom.sonos.muted",nextState:"on_playing"
        state "on_playing", label:"MSG on Stopped", action:"switchDoNotDisturb", icon:"st.alarm.beep.beep",nextState:"off_playing"
        state "off_playing", label:"MSG on Playing", action:"switchDoNotDisturb", icon:"st.alarm.beep.beep",nextState:"off"
	}

	standardTile("unsubscribe", "device.status", width: 1, height: 1, decoration: "flat") {
		state "previous", label:'Unsubscribe', action:"unsubscribe", backgroundColor:"#ffffff"
	}
	
    
    valueTile("currentStations", "device.stationsDescription", inactiveLabel: true, height:10, width:3, decoration: "flat") {
		state "default", label:'${currentValue}', backgroundColor:"#ffffff"
	}


	main "main"

	details([
		"previousTrack","play","nextTrack",
		"levelSliderControl","stop","mute",
		"currentSong",
		"status","btnMode","refresh", "doNotDisturb","currentStations"
		
		
		//,"unsubscribe"
	])
}


def parse(description) {
   
}

def installed() {
	sendEvent(name:"model",value:getDataValue("deviceType"),isStateChange:true)
	def result = [delayAction(5000)]
	result << refresh()
	result.flatten()
}

def on(){
	play()
}

def off(){
	stop()
}

def poll() {
	refresh()
}


def refresh() {
	log.trace "state.lastRefreshTime ${state.lastRefreshTime} state.lastStatusTime ${state.lastStatusTime}"
    if ((state.lastRefreshTime ?: 0) > (state.lastStatusTime ?:0)){
        sendEvent(name: "status", value: "no_device_present", data: "no_device_present", displayed: false)
    }
    state.lastRefreshTime = new Date().time
    updateStatus()
    
}
 
def updateStatus(){
	log.trace "updateStatus"
	def response = getStatus()
    if(response?.data && response?.data != []){
    	def data = response.data
	//	log.trace "response.data ${response.data}"
        if(data.playerInfo?.state){
        	def state = data.playerInfo?.state.toLowerCase()
        	sendEvent(name: "status", value: state, displayed: false)
			sendEvent(name: "switch", value: state=="playing" ? "on" : "off", displayed: false)
        }else{
        	sendEvent(name: "status", value: "standby", displayed: false)
			sendEvent(name: "switch", value: "off", displayed: false)
        }
        if(data.playerInfo?.infoText?.title){
            def trackDescription = (data.playerInfo?.providerName ? data.playerInfo?.providerName +" ": "") + data.playerInfo?.infoText?.title.replaceAll("_"," ")
        	sendEvent(name: "trackDescription",value: trackDescription,descriptionText: trackDescription	)
        }
        if(data.playerInfo?.volume?.volume){
        	sendEvent(name: "level", value: data.playerInfo?.volume?.volume, description: "$device.displayName Volume is ${data.playerInfo?.volume?.volume}",displayed: false)
            def muted = data.playerInfo?.volume?.muted == "true" ? "muted" : "unmuted"
			sendEvent(name: "mute", value: muted, descriptionText: "$device.displayName is $muted",displayed: false)
        }else{
         	def responseM = getMStatus()
        	 if(responseM?.data && responseM?.data != []){
             	def dataM = responseM.data
                if(dataM?.volume){
                    sendEvent(name: "level", value: dataM.volume, description: "$device.displayName Volume is ${dataM.volume}",displayed: false)
                    def muted = dataM.muted == "true" ? "muted" : "unmuted"
                    sendEvent(name: "mute", value: muted, descriptionText: "$device.displayName is $muted",displayed: false)
                }
             }
        }
        state.lastStatusTime = new Date().time
       //log.trace "updateStatus()  response $response.data "
    }

}

def setCommand(command){
	def params = [
        uri: parent.state.domain + "/api/np/command?deviceSerialNumber=" + getDataValue("serialNumber") + "&deviceType=" + getDataValue("deviceType") ,
        headers:[
            "Csrf": parent.state.csrf,
            "Cookie": parent.state.cookie,
        ],
        body:command
	]
    //log.trace "params $params"
    def response = apiPost(params)
}

def getSearchTuneIn(query) //transport info
{
    def params = [
        uri: parent.state.domain + "/api/tunein/search?query="+URLEncoder.encode(query, "UTF-8") +"&mediaOwnerCustomerId=" + getDataValue("deviceOwnerCustomerId") ,
        headers:[
            "Csrf": parent.state.csrf,
            "Cookie": parent.state.cookie,
        ]
	]
    apiGet(params)
}


def playStation(incStatation = 0, incGenre = 0){
	log.trace "playStation(incStatation = $incStatation, incGenre = $incGenre)"
    def genre
    if (settings["genres"]){
        if (state.selectedGenres != settings["genres"]){
            state.selectedGenres = settings["genres"]
            state.genres = []
            log.trace "state.genres ${state.selectedGenres}"
            settings["genres"]?.tokenize(",").each{item ->
            		if (item) state.genres << item.trim().toLowerCase()
            }
			log.trace "Genres parsed ${state.genres}"
        }
        if (incGenre == 1 || incGenre == -1) Collections.rotate(state.genres, -incGenre)
        genre = state.genres[0]
    }else{
        genre = "smooth jazz"
    }

        log.trace "genre $genre"
	if (genre){
          def stations = getStationGenre(genre)
        if (stations){
            if (incStatation == 1 || incStatation == -1) Collections.rotate(state[genre], -incStatation)
            def stationsDescription = " [${genre.toUpperCase()}] \n\n"
            state[genre].each{it ->
                stationsDescription = stationsDescription + (it.values().n[0]).toString()  + "\n\n"
            }
            stationsDescription = stationsDescription + "_________\n\n"
            sendEvent(name: "stationsDescription",value: stationsDescription,descriptionText: stationsDescription,displayed: false	)
            
            
            def stationUri = stations[0].keySet()[0]
           // log.trace "genre: $genre / station: ${stations[0].values().n[0]} / link: $stationUri"
            
            def params = [
                uri: parent.state.domain + "/api/tunein/queue-and-play?deviceSerialNumber=" + getDataValue("serialNumber") + "&deviceType=" + getDataValue("deviceType") + "&guideId=" + stationUri + "&contentType=station&callSign=&mediaOwnerCustomerId=" + getDataValue("deviceOwnerCustomerId") ,
                headers:[
                    "Csrf": parent.state.csrf,
                    "Cookie": parent.state.cookie,
                ]
            ]
			log.trace ()
            def response = apiPost(params)
        }
    }
}


def getStationGenre(genre){
log.trace "getStationGenre($genre)"
    if (genre){
    	genre= genre.trim().toLowerCase()
//    log.trace "state[genre] ${state[genre]}"
    
        if(!state[genre] || state[genre]?.size() == 0  ){
  //      	log.trace "state[$genre] ${state[genre]}"
            state[genre] = []
            	def response = getSearchTuneIn(genre)
            
                try {
                
                //	log.trace "resp $resp ${response.data.any}"
                     response.data.browseList.any { element ->
                //    log.trace "element" + element
               // 		log.trace "bool  ${ !(state[genre].find{it.values().n[0] == element.name} as Boolean)}"
                        if (!element.name.contains("(Not Supported)") && element.available && !(state[genre].find{it?.values()?.n[0] == element.name} as Boolean) && element.id.startsWith("s")  ){
                            state[genre] << ["${element.id}":[n:"${element.name}",a:""]]
                         //   log.trace " new added-----------------------------state[$genre] ${element}"
                           
                            
                 /*          state[genre].any {it ->
                            log.trace "bb ${it.keySet()[0]}"
                            	log.trace "bb ${it.keySet()[0].startsWith("s")}"
                            }*/
                          //  log.trace "bbbb ${ state[genre].find { it.key == "[s249973:[n:Smooth Jazz, a:]]" }  }"
                           
						}
                        if (state[genre].size() >=20 ){
                            return true // break
                        }
                    
                }
            } catch (ex) {
                log.debug "something went wrong: $ex"
            }
        }
     //   log.trace " new -----------------------------state[$genre] ${state[genre]}"
        state[genre]
    }else{
     log.debug "vacio ${state[genre]} "
    	[]
    }
}

def playGenre(genre){
    def stations = getStationGenre(genre)

    log.trace "stations $stations"
    if (stations){
        def params = [
            uri: parent.state.domain + "/api/tunein/queue-and-play?deviceSerialNumber=" + getDataValue("serialNumber") + "&deviceType=" + getDataValue("deviceType") + "&guideId=" + stations[0].keySet()[0] + "&contentType=station&callSign=&mediaOwnerCustomerId=" + getDataValue("deviceOwnerCustomerId") ,
            headers:[
                "Csrf": parent.state.csrf,
                "Cookie": parent.state.cookie,
            ]
        ]
        log.trace ()
        def response = apiPost(params)
    }
    runIn(5, updateStatus)
}


def createReminder(dateTime,reminderLabel ) //createReminder("2018-11-22T02:57", "My remote remainder")
{
	log.trace "createReminder(${dateTime.take(16)},$reminderLabel )"
    def originalDateTime = Date.parse("yyyy-MM-dd'T'HH:mm",dateTime.take(16))
    
    def params = [
        uri: parent.state.domain + "/api/notifications/createReminder",
        headers:[
            "Csrf": parent.state.csrf,
            "Cookie": parent.state.cookie,
        ],
        body:"{\"type\":\"Reminder\",\"status\":\"ON\",\"alarmTime\":${timeToday(originalDateTime.format("yyyy-MM-dd'T'HH:mm:ss.SSSZ"), location.timeZone).getTime()},\"originalTime\":\"${originalDateTime.format("HH:mm:00.000")}\",\"originalDate\":\"${originalDateTime.format("yyyy-MM-dd")}\",\"timeZoneId\":null,\"reminderIndex\":null,\"skillInfo\":null,\"sound\":null,\"deviceSerialNumber\":\"${getDataValue("serialNumber")}\",\"deviceType\":\"${getDataValue("deviceType")}\",\"recurringPattern\":null,\"reminderLabel\":\"$reminderLabel\",\"isSaveInFlight\":true,\"id\":\"createReminder\",\"isRecurring\":false,\"createdDate\":${new Date().getTime()}}"
	]
    def response = apiPut(params)
    if (response) sendEvent(name: "createReminder",value: "Reminder Created > $reminderLabel ${dateTime.take(16)}",descriptionText: "Reminder Created > $reminderLabel ${dateTime.take(16)}")
}

def getMStatus() 
{
	def params = [
        uri: parent.state.domain + "/api/media/state?deviceSerialNumber=" + getDataValue("serialNumber") + "&deviceType=" + getDataValue("deviceType") ,//+ "&screenWidth=2560"
        headers:[
            "Csrf": parent.state.csrf,
            "Cookie": parent.state.cookie,
        ]
	]
    //log.trace "params $params"
    apiGet(params)
}

def getStatus() //transport info
{
	def params = [
        uri: parent.state.domain + "/api/np/player?deviceSerialNumber=" + getDataValue("serialNumber") + "&deviceType=" + getDataValue("deviceType") ,//+ "&screenWidth=2560"
        headers:[
            "Csrf": parent.state.csrf,
            "Cookie": parent.state.cookie,
        ]
	]
    //log.trace "params $params"
    apiGet(params)
}



def play() {
    setCommand('{"type":"PlayCommand"}')
    runIn(5, updateStatus)
}

def stop() {
	setCommand('{"type":"PauseCommand"}')
    runIn(2, updateStatus)
}

def pause() {
	setCommand('{"type":"PauseCommand"}')
    runIn(2, updateStatus)
}

def nextTrack() {
	setCommand('{"type":"NextCommand"}')
    runIn(5, updateStatus)
}

def previousTrack() {
	setCommand('{"type":"PreviousCommand"}')
    runIn(5, updateStatus)
}


def nextStation() {
	playStation(1,0)
    runIn(5, updateStatus)
}

def previousStation() {
	playStation(-1,0)
    runIn(5, updateStatus)
}

def nextGenre() {
    playStation(0,1)
    runIn(5, updateStatus)
}

def previousGenre() {
	playStation(0,-1)
    runIn(5, updateStatus)
}

def tileSetLevel(val)
{
	setLocalLevel(val)
}

def setLocalLevel(val) {
	setLevel(val)
}

def setLevel(val)
{
	def v = Math.max(Math.min(Math.round(val), 100), 0)
    setCommand('{"type":"VolumeLevelCommand","volumeLevel":'+v+',"contentFocusClientId":null}')
    runIn(2, updateStatus)
}


def mute()
{
	
}

def unmute()
{
	
}

def playTextAndResume(text, volume=null){
    speak(text,volume,null)
}

def playTextAndRestore(text, volume=null){
	speak(text,volume,null)
}


def playText(msg){
	log.trace "playText($msg)"
    def type =  ["Weather","Traffic","FlashBriefing","GoodMorning","SingASong","TellStory"].find{ it.toLowerCase() == msg.toLowerCase()}
    def tts = ""
    if (type){
    	type = "Alexa.${type}.Play"
    }else{
    	type = "Alexa.Speak"
        tts = ", \\\"textToSpeak\\\": \\\"" + msg + "\\\"" 
    }
    log.trace "type = $type tts $tts"
       
        
    def params = [
        uri: parent.state.domain + "/api/behaviors/preview",
        headers:[
            "Csrf": parent.state.csrf,
            "Cookie": parent.state.cookie,
                ],
         body:"{\"behaviorId\":\"PREVIEW\",\"sequenceJson\":\"{\\\"@type\\\":\\\"com.amazon.alexa.behaviors.model.Sequence\\\", \\\"startNode\\\":{\\\"@type\\\":\\\"com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode\\\",\\\"type\\\":\\\"${type}\\\",\\\"operationPayload\\\":{\\\"deviceType\\\":\\\"" +  getDataValue("deviceType") + "\\\",        \\\"deviceSerialNumber\\\":\\\"" +  getDataValue("serialNumber") + "\\\",\\\"locale\\\":\\\"es-US\\\", \\\"customerId\\\":\\\"" +  getDataValue("deviceOwnerCustomerId") + "\\\"${tts}}}}\", \"status\":\"ENABLED\"}"
        ]
        //log.trace params
        def response = apiPost(params)
}


def speak(String msg, Integer volume = null, Integer delay=null){
	log.trace "speak($msg, $volume, $delay)"
    def currentVolume
    def response = playText(msg)

	delay = delay ? delay :  Math.max(Math.round(msg.length()/16),2)

    if (volume){
    	volume = Math.max(Math.min(Math.round(volume), 100), 0)
    	currentVolume = device.currentState("level")?.integerValue
        log.trace "volume $volume currentVolume $currentVolume"
    	if (currentVolume != volume && volume > 0){
            setCommand('{"type":"VolumeLevelCommand","volumeLevel":'+volume+',"contentFocusClientId":null}')
            if( ["Weather","Traffic","FlashBriefing","GoodMorning","SingASong","TellStory"].find{ it.toLowerCase() == msg.toLowerCase()} && delay <= 2 ) delay = -1
        }else{volume = null}
    }
    //log.trace "delay $delay volume $volume"
	if (volume && delay > 0) runIn(delay,  "executeFunction", [data: [function: "setCommand" , parametrs: '{"type":"VolumeLevelCommand","volumeLevel":'+currentVolume+',"contentFocusClientId":null}']])
}

def executeFunction(data){
    switch(data.function) {
        case "setCommand":
			setCommand(data.parametrs)
            break
	}
}

def apiPut(Map params){
	try{
		httpPut(params) {resp ->
            resp
        }
    } catch (e) {
    	log.trace "apiPut Error : $e" 
        null
    }
}


def apiGet(Map params){
	try{
		httpGet(params) {resp ->
            resp
        }
    } catch (e) {
    	log.trace "apiGet Error : $e" 
        null
    }
}


def apiPost(Map params){
    try {
        httpPost(params) { resp ->
            resp
        }
    } catch (e) {
        log.trace "apiPost Error : $e" 
		null
    }
}

def setBtnMode(val)
{
	sendEvent(name:"btnMode",value:val,isStateChange:true)
}

def getVolume()
{
	updateStatus()
}


def getPlayMode()
{
	updateStatus()
}
def getCurrentMedia()
{
	updateStatus()
}

def getCurrentStatus() //transport info
{
	updateStatus()
}

def seek(index) {
	playStation(index,0)
    runIn(5, updateStatus)
}

//--- fin modificado----------------------------------------------------------------




def setDoNotDisturb(val)
{
	sendEvent(name:"doNotDisturb",value:val,isStateChange:true)
}


// Always sets only this level

// Always sets only this level
def setVolume(val) {
	mediaRendererAction("SetVolume", "RenderingControl", getDataValue("rccurl") , [InstanceID:0, Channel:"Master", DesiredVolume:Math.max(Math.min(Math.round(val), 100), 0)])
}

private childLevel(previousMaster, newMaster, previousChild)
{
	if (previousMaster) {
		if (previousChild) {
			Math.round(previousChild * (newMaster / previousMaster))
		}
		else {
			newMaster
		}
	}
	else {
		newMaster
	}
}




def setPlayMode(mode)
{
	mediaRendererAction("SetPlayMode", [InstanceID:0, NewPlayMode:mode])
}
def switchDoNotDisturb(){
    switch(device.currentValue("doNotDisturb")) {
        case "off":
			setDoNotDisturb("on")
            break
        case "on":
			setDoNotDisturb("on_playing")
            break
		case "on_playing":
			setDoNotDisturb("off_playing")
            break
		default:
			setDoNotDisturb("off")
	}
}
def switchBtnMode(){
    switch(device.currentValue("btnMode")) {
        case "normal":
			setBtnMode("station")
            break
        case "station":
			setBtnMode("genre")
            break
		case "genre":
			setBtnMode("normal")
            break
		default:
			setBtnMode("station")
	}
}

def playByMode(uri, duration, volume,newTrack,mode) {

	def playTrack = false
	def eventTime = new Date().time
	def track = device.currentState("trackData")?.jsonValue
	def currentVolume = device.currentState("level")?.integerValue
	def currentStatus = device.currentValue("status")
    def currentPlayMode = device.currentValue("playMode")
    def currentDoNotDisturb = device.currentValue("doNotDisturb")
	def level = volume as Integer
	def actionsDelayTime =  actionsDelay ? (actionsDelay as Integer) * 1000 :0
	def result = []
	duration = duration ? (duration as Integer) : 0
	switch(mode) {
        case 1:
			playTrack = currentStatus == "playing" ? true : false
            break
		case 3:
			track = newTrack
			playTrack = !track?.uri?.startsWith("http://127.0.0.1") ? true : false
            break
	}
	if( !(currentDoNotDisturb  == "on_playing" && currentStatus == "playing" ) && !(currentDoNotDisturb  == "off_playing" && currentStatus != "playing" ) && currentDoNotDisturb != "on"  && eventTime > state.secureEventTime ?:0){
		if (uri){
			uri = uri.replace("https:","http:")
            uri = uri +  ( uri.contains("?") ? "&":"?") + "ts=$eventTime"

            result << mediaRendererAction("Stop")
            result << delayAction(1000 + actionsDelayTime)
            

            if (level) {
                //if(actionsDelayTime > 0){result << delayAction(actionsDelayTime)}
                result << setVolume(level)
				result << delayAction(2200 + actionsDelayTime)
			}
            if (currentPlayMode != "NORMAL") {
                result << setPlayMode("NORMAL")
                result << delayAction(2000 + actionsDelayTime)
			}
			result << setTrack(uri)
			result << delayAction(2000 + actionsDelayTime)

			result << mediaRendererAction("Play")
			if (duration < 2){
				def matcher = uri =~ /[^\/]+.mp3/
				if (matcher){duration =  Math.max(Math.round(matcher[0].length()/8),2)}
			}
			def delayTime = (duration * 1000) + 3000
			delayTime = customDelay ? ((customDelay as Integer) * 1000) + delayTime : delayTime
			state.secureEventTime = eventTime + delayTime + 7000
			result << delayAction(delayTime)
		}
		if (track ) {

            result << mediaRendererAction("Stop")
            result << delayAction(1000 + actionsDelayTime)

            if (level) {
                result << setVolume(currentVolume)
				result << delayAction(2200 + actionsDelayTime)
			}
            if (currentPlayMode != "NORMAL") {
                result << setPlayMode(currentPlayMode)
                result << delayAction(2000 + actionsDelayTime)
			}
			if (!track.uri.startsWith("http://127.0.0.1")){
				result << setTrack(track)
				result << delayAction(2000 + actionsDelayTime)
			}
			if (playTrack) {
				if (!track.uri.startsWith("http://127.0.0.1")){
					result << mediaRendererAction("Play")
				}else{
                    result << mediaRendererAction("Next")
				}
			}else{
				result << mediaRendererAction("Stop")
			}
		}
		result = result.flatten()
	}
	else{
		log.trace "previous notification in progress or Do Not Disturb Activated"
	}
	result
}



def playTextAndTrack(text, trackData, volume=null){
	def sound = textToSpeech(text)
	playByMode(sound.uri, Math.max((sound.duration as Integer),1), volume, trackData, 3)
}
def playTrackAndResume(uri, duration, volume=null) {
    playByMode(uri, duration, volume, null, 1)
}
def playTrackAndRestore(uri, duration, volume=null) {
	playByMode(uri, duration, volume, null, 2)
}
def playSoundAndTrack(uri, duration, trackData, volume=null) {
    playByMode(uri, duration, volume, trackData, 3)
}

def playTrackAtVolume(String uri, volume) {
	playByMode(uri, 0, volume, null, 3)
}

def playTrack(String uri, metaData="") {
	def result = setTrack(uri, metaData)
	result << mediaRendererAction("Play")
	result.flatten()
}

def playTrack(Map trackData) {
	def result = setTrack(trackData)
	result << mediaRendererAction("Play")
	result.flatten()
}

def setTrack(Map trackData) {
	setTrack(trackData.uri, trackData?.metaData)
}

def setTrack(String uri, metaData="")
{
	metaData = metaData?:"<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\"><item id=\"1\" parentID=\"1\" restricted=\"1\"><upnp:class>object.item.audioItem.musicTrack</upnp:class><upnp:album>SmartThings Catalog</upnp:album><upnp:artist>SmartThings</upnp:artist><upnp:albumArtURI>https://graph.api.smartthings.com/api/devices/icons/st.Entertainment.entertainment2-icn?displaySize=2x</upnp:albumArtURI><dc:title>SmartThings Message</dc:title><res protocolInfo=\"http-get:*:audio/mpeg:DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01500000000000000000000000000000\" >$uri</res></item> </DIDL-Lite>"
	mediaRendererAction("SetAVTransportURI", [InstanceID:0, CurrentURI:uri, CurrentURIMetaData:metaData])
}

def resumeTrack(Map trackData = null) {

	def result = restoreTrack(trackData)
	result << mediaRendererAction("Play")
	result
}

def restoreTrack(Map trackData = null) {

	def result = []
	def data = trackData
	if (!data) {
		data = device.currentState("trackData")?.jsonValue
	}
	if (data) {
		result << mediaRendererAction("SetAVTransportURI", [InstanceID:0, CurrentURI:data.uri, CurrentURIMetaData:data.metaData])
	}
	else {
		log.warn "Previous track data not found"
	}
	result
}


def setText(String msg) {
	def sound = textToSpeech(msg)
	setTrack(sound.uri)
}



// Custom commands

def subscribe() {

	log.trace "subscribe()"
    log.trace "getDataValue(avteurl) ${getDataValue("avteurl")}"
    log.trace "getDataValue(rceurl) ${getDataValue("rceurl")}"
    log.trace "getDataValue(avtcurl) ${getDataValue("avtcurl")}"
    log.trace " getDataValue(rccurl) ${ getDataValue("rccurl")}"
    
   
	def result = []
	result << subscribeAction(getDataValue("avteurl"))
	result << delayAction(2500)
	result << subscribeAction(getDataValue("rceurl"))
	result << delayAction(2500)
	result
}
def unsubscribe() {
	log.trace "unsubscribe()"
    def result = [
		unsubscribeAction(getDataValue("avteurl"), device.getDataValue('subscriptionId')),
		unsubscribeAction(getDataValue("rceurl"), device.getDataValue('subscriptionId')),
		unsubscribeAction(getDataValue("avteurl"), device.getDataValue('subscriptionId1')),
		unsubscribeAction(getDataValue("rceurl"), device.getDataValue('subscriptionId1')),
	]
	updateDataValue("subscriptionId", "")
	updateDataValue("subscriptionId1", "")
	result
}





def getSystemString()
{
	mediaRendererAction("GetString", "SystemProperties", "/SystemProperties/Control", [VariableName:"UMTracking"])
}

private messageFilename(String msg) {
	msg.toLowerCase().replaceAll(/[^a-zA-Z0-9]+/,'_')
}

private getCallBackAddress()
{
	device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

private mediaRendererAction(String action) {
	def result
	if(action=="Play"){
		result = mediaRendererAction(action, "AVTransport", getDataValue("avtcurl"), [InstanceID:0, Speed:1])
    }
	else if (action=="Mute"){
		result = mediaRendererAction("SetMute", "RenderingControl", getDataValue("rccurl"), [InstanceID: 0, Channel:"Master", DesiredMute:1])
	}
	else if (action=="UnMute"){
		result = mediaRendererAction("SetMute", "RenderingControl", getDataValue("rccurl"), [InstanceID: 0, Channel:"Master", DesiredMute:0])
	}
	else{
		result = mediaRendererAction(action, "AVTransport", getDataValue("avtcurl"), [InstanceID:0])
    }
	result
}

private mediaRendererAction(String action, Map body) {
	mediaRendererAction(action, "AVTransport", getDataValue("avtcurl"), body)
}

private mediaRendererAction(String action, String service, String path, Map body = [InstanceID:0, Speed:1]) {
    def result = new physicalgraph.device.HubSoapAction(
		path:    path ?: "/MediaRenderer/$service/Control",
		urn:     "urn:schemas-upnp-org:service:$service:1",
		action:  action,
		body:    body,
		headers: [Host:getHostAddress(), CONNECTION: "close"]
	)
	result
}

private subscribeAction(path, callbackPath="") {
	def address = getCallBackAddress()
	def ip = getHostAddress()
	def result = new physicalgraph.device.HubAction(
		method: "SUBSCRIBE",
		path: path,
		headers: [
			HOST: ip,
			CALLBACK: "<http://${address}/notify$callbackPath>",
			NT: "upnp:event",
			TIMEOUT: "Second-1200"])
	result
}

private unsubscribeAction(path, sid) {
	def ip = getHostAddress()
	def result = new physicalgraph.device.HubAction(
		method: "UNSUBSCRIBE",
		path: path,
		headers: [
			HOST: ip,
			SID: "uuid:${sid}"])
	result
}

private delayAction(long time) {
	new physicalgraph.device.HubAction("delay $time")
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}



private getHostAddress() {
	def parts = getDataValue("dni")?.split(":")
	def ip = convertHexToIP(parts[0])
	def port = convertHexToInt(parts[1])
	return ip + ":" + port
}

private statusText(s) {
	switch(s) {
		case "PLAYING":
			return "playing"
		case "PAUSED_PLAYBACK":
			return "paused"
		case "STOPPED":
			return "stopped"
		case "NO_MEDIA_PRESENT":
			return "no_media_present"
        case "NO_DEVICE_PRESENT":
        	retun "no_device_present"
		default:
			return s
	}
}

private updateSid(sid) {
	if (sid) {
		def sid0 = device.getDataValue('subscriptionId')
		def sid1 = device.getDataValue('subscriptionId1')
		def sidNumber = device.getDataValue('sidNumber') ?: "0"
		if (sidNumber == "0") {
			if (sid != sid1) {
				updateDataValue("subscriptionId", sid)
				updateDataValue("sidNumber", "1")
			}
		}
		else {
			if (sid != sid0) {
				updateDataValue("subscriptionId1", sid)
				updateDataValue("sidNumber", "0")
			}
		}
	}
}

private dniFromUri(uri) {
	def segs = uri.replaceAll(/http:\/\/([0-9]+\.[0-9]+\.[0-9]+\.[0-9]+:[0-9]+)\/.+/,'$1').split(":")
	def nums = segs[0].split("\\.")
	(nums.collect{hex(it.toInteger())}.join('') + ':' + hex(segs[-1].toInteger(),4)).toUpperCase()
}

private hex(value, width=2) {
	def s = new BigInteger(Math.round(value).toString()).toString(16)
	while (s.size() < width) {
		s = "0" + s
	}
	s
}
