/**
 *  Echo Service Manager v 1.0.0
 *
 *  Author: Ulises Mujica 
 */
 
definition(
	name: "Echo (Connect)",
	namespace: "mujica",
	author: "Ulises Mujica",
	description: "Allows you to control your Echo from the SmartThings app. Perform basic functions like play, pause, stop, tts from the Things screen.",
	category: "SmartThings Labs",
	singleInstance: true,
	iconUrl: "https://graph.api.smartthings.com/api/devices/icons/st.secondary.smartapps-tile?displaySize=2x",
    iconX2Url: "https://graph.api.smartthings.com/api/devices/icons/st.secondary.smartapps-tile?displaySize=2x"
){
    appSetting "domain"
    appSetting "csrf"
    appSetting "cookieP1"
    appSetting "cookieP2"
    appSetting "cookieP3"
}

preferences {
	page(name: "iniSettings", title: "Connect your Echo devices to SmartThings", content: "iniSettings")
	page(name: "chooseDevices", title: "Choose devices to Control With SmartThings", content: "echoDiscovery")
}

def iniSettings(){
	state.loadStatus = "Inactive"
    log.trace "state.loadStatus ${state.loadStatus}"
    return dynamicPage(name:"iniSettings", title:"Connect Your Echo devices to SmartThings", nextPage:"chooseDevices", install:false, uninstall: true) {
       section("Echo Remote Credentials") {
			paragraph "Get your Echo data from https://alexa.amazon.com\r\n\r\nThe cookie data is to long, you must to split it in 3 parts, go to APP settings in IDE to add the info\r\n\r\nTap 'Next' after you have entered the data.\r\n\r\nOnce your request is accepted, SmartThings will scan your Echo devices."
		}
    }
}

def echoDiscovery() {
	debugOut "echoDiscovery()"
	//getToken()
	state.token = "1234567890"
    
    if (state.loadStatus == "Inactive"){
    	state.count = 0
    	state.loadStatus = "Loading"
        log.trace "state.loadStatus ${state.loadStatus}"
    	deviceDiscovery()
    }
    log.trace "state.count ${state.count}"
    state.count = state.count + 1 
    log.trace "state.count ${state.count}"
    if(state.loadStatus == "Loaded" ){
        def options = devicesDiscovered() ?: []
		log.trace "state.loadStatus ${state.loadStatus}"
        return dynamicPage(name:"chooseDevices", title:"", nextPage:"", install:true, uninstall: true) {
            section("Tap Below to View Device List") {
                input "selectedEchos", "enum", required:false, title:"Select Echo", multiple:true, options:options
                paragraph """Tap 'Done' after you have selected the desired devices."""
            }
        }
    }else{
    	if (state.count)
    	log.trace "state.loadStatus ${state.loadStatus}"
        def msg = state.count >= 3 ? "The TCP Gateway is not responding, please verify the ip address" : "Please wait while we discover your devices. Discovery can take some minutes or more, so sit back and relax! Select your device below once discovered."
        return dynamicPage(name:"chooseDevices", title:"", nextPage:"", refreshInterval:5) {
            section(msg) {}
        }
    }
}


def installed() {
	debugOut "Installed with settings: ${settings}"

	unschedule()
	unsubscribe()

	setupEchos()
}

def updated() {
	debugOut "Updated with settings: ${settings}"
	unschedule()
	setupEchos()
}

def uninstalled()
{
	unschedule() //in case we have hanging runIn()'s
}

private removeChildDevices(delete)
{
	debugOut "deleting ${delete.size()} Echos"
	debugOut "deleting ${delete}"
	delete.each {
		deleteChildDevice(it.device.deviceNetworkId)
	}
}

def uninstallFromChildDevice(childDevice)
{
	def errorMsg = "uninstallFromChildDevice was called and "
	if (!settings.selectedEchos) {
		debugOut errorMsg += "had empty list passed in"
		return
	}

	def dni = childDevice.device.deviceNetworkId

	if ( !dni ) {
		debugOut errorMsg += "could not find dni of device"
		return
	}

	def newDeviceList = settings.selectedEchos - dni
	app.updateSetting("selectedEchos", newDeviceList)
	debugOut errorMsg += "completed succesfully"
}


def setupEchos() {
	debugOut "setupEchos()"
	def echos = state.devices
	def deviceFile = "TCP Echo"

	selectedEchos.each { did ->
		//see if this is a selected echo and install it if not already
		def d = getChildDevice(did)

		if(!d) {
			def newEcho = echos.find { (it.serialNumber) == did }
			d = addChildDevice("mujica", "Echo", did, null, [name: "${newEcho?.accountName}", label: "Echo ${newEcho?.accountName}", completedSetup: true,"data":newEcho])
		} else {
			infoOut "Avoid add existent device ${did}"
		}
	}
	def delete = getChildDevices().findAll { !selectedEchos?.contains(it.deviceNetworkId) }
	removeChildDevices(delete)
}


def deviceDiscovery() {
	
    def logText =""
    
    def data
    def devices = []
    
    def params = [
    uri: appSettings.domain + "/api/devices-v2/device?cached=true",
    headers:[
          //  "Csrf": appSettings.csrf,
            "Cookie": appSettings.cookieP1 + appSettings.cookieP2 + appSettings.cookieP3,
            ]
	]
    log.trace "params $params"
    try {
        httpGet(params) { resp ->
 //          resp.headers.each {
 //               log.debug "${it.name} : ${it.value}"
 //           }
 //           log.debug "response contentType: ${resp.contentType}"
            data = resp.data
            log.debug "response data: ${resp.data}"
        }
    } catch (e) {
        log.debug "something went wrong: $e"
    }
	
    if (data){
        state.devices = data["devices"]
    	state.loadStatus = "Loaded"

        data["devices"].each {
                logText +=" accountName:  ${ it["accountName"]} deviceAccountId:  ${ it["deviceAccountId"]} deviceFamily:  ${ it["deviceFamily"]} deviceOwnerCustomerId:  ${ it["deviceOwnerCustomerId"]} deviceType:  ${ it["deviceType"]} deviceTypeFriendlyName:  ${ it["deviceTypeFriendlyName"]} online:  ${ it["online"]} serialNumber:  ${ it["serialNumber"]} softwareVersion:  ${ it["softwareVersion"]} "
        	}
    	log.trace logText

    } else{
    	log.trace "No data"
    }
}

Map devicesDiscovered() {
	def devices =  state.devices
	def map = [:]

	devices.each {
    	if (it?.deviceFamily !="VOX"){
            def value = "${it?.accountName}"
            def key = it?.serialNumber
            map["${key}"] = value
        }
    }
	map
}

def getDevices()
{
	state.devices = state.devices ?: [:]
}


def debugOut(msg) {
	log.debug msg
}

def traceOut(msg) {
	log.trace msg
}

def infoOut(msg) {
	log.info msg
}
