/*
 * Urulu Lamp Control
 * Alpha release!
*/

preferences {
	input("devname", "text", title: "Device Name", description: "The devices name",required:true)
	input("destIp", "text", title: "IP", description: "The device IP",required:true)
    input("destPort", "text", title: "Port", description: "The device Port",required:true)
    input("gatewayIP", "text", title: "gatewayIP", description: "The gateway IP",required:true)
    input("gatewayPort", "text", title: "gatewayPort", description: "The gateway Port",required:true)    

		input("color", "enum", title: "Default Color", required: false, multiple:false, value: "Previous", options: [
                    ["Previous":"Previous"],
					["Soft White":"Soft White - Default"],
					["White":"White - Concentrate"],
					["Daylight":"Daylight - Energize"],
					["Warm White":"Warm White - Relax"],
					"Red","Green","Blue","Yellow","Orange","Purple","Pink","Cyan","Random","Custom"])
                 
	
        input("level", "enum", title: "Default Level", required: false, value: 100, options: [[0:"Previous"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]])


}

metadata {
	definition (name: "Urulu Lamp Control", namespace: "blahmartinblah", author: "blahmartinblah") {
		capability "Switch Level"
		capability "Color Control"
		capability "Color Temperature"
		capability "Switch"
        command "lighton"
        command "lightoff"
        command "setlightlevel"
        
      	}

	simulator {
		// TODO-: define status and reply messages here
	}

	tiles (scale: 2){      
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"lightoff", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"off"
				attributeState "off", label:'${name}', action:"lighton", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"on"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
			tileAttribute ("device.color", key: "COLOR_CONTROL") {
				attributeState "color", action:"setColor"
			}
 }
        standardTile("switchcandle", "device.switch", width: 2, height: 2, canChangeIcon: true) {
            state "off", label: '${currentValue}', action: "switch.on",
                  icon: "http://cdn.device-icons.smartthings.com/Seasonal Winter/seasonal-winter-018-icn@2x.png", backgroundColor: "#ffffff"
            state "on", label: '${currentValue}', action: "switch.off",
                  icon: "http://cdn.device-icons.smartthings.com/Seasonal Winter/seasonal-winter-018-icn@2x.png", backgroundColor: "#79b821"
        }
        standardTile("switchnight", "device.switch", width: 2, height: 2, canChangeIcon: true) {
            state "off", label: '${currentValue}', action: "switch.on",
                  icon: "st.Weather.weather4", backgroundColor: "#ffffff"
            state "on", label: '${currentValue}', action: "switch.off",
                  icon: "st.Weather.weather4", backgroundColor: "#79b821"
        }
    
// candle icon: http://cdn.device-icons.smartthings.com/Seasonal Winter/seasonal-winter-018-icn@2x.png
// moon icon: http://cdn.device-icons.smartthings.com/Weather/weather4-icn@2x.png
// source: http://scripts.3dgo.net/smartthings/icons/


	main(["switch"])
	details(["switch", "levelSliderControl",
             "switchcandle", "switchnight" ])
}
}


//def parse(String description) {
	//log.debug "Parsing '${description}'"
//}

def lighton() {
	log.debug "lightoff pushed"
    sendEvent(name: "light", value: 'lighton')
	request("/udp-gateway.php?udbport=6000&broadcastip=192.168.1.255&message={\"action\":\"light_control\",\"MAC\":\"" + devname + "\",\"type\":\"on\"}")
}

def lightoff() { 
	log.debug "lightoff pushed"
    sendEvent(name: "light", value: 'lightoff')
	request("/udp-gateway.php?udbport=6000&broadcastip=192.168.1.255&message={\"action\":\"light_control\",\"MAC\":\"" + devname + "\",\"type\":\"off\"}")
}

def setlightlevel(val) {
	if (val==0) {
    	request("/udp-gateway.php?udbport=6000&broadcastip=192.168.1.255&message={\"action\":\"light_control\",\"MAC\":\"" + devname + "\",\"type\":\"off\"}")
    } else {    
		request("/udp-gateway.php?udbport=6000&broadcastip=192.168.1.255&message={\"action\":\"light_control\",\"MAC\":\"" + devname + "\",\"type\":\"on\"}")
    }
    sendEvent(name: "lightlevel", value: val)    
	request("/udp-gateway.php?udbport=6000&broadcastip=192.168.1.255&message={\"action\":\"set_light\",\"MAC\":\"7cc709806aeb\",\"r\":4,\"g\":" + val + ",\"b\":12,\"x\":0}")
}

// handle commands
def request($action) {
    def port = "80"
    def host = "192.168.1.203" 
    def hosthex = convertIPtoHex(host)
    def porthex = convertPortToHex(port)
    device.deviceNetworkId = "$hosthex:$porthex" 
    
    log.debug "The device id configured is: $device.deviceNetworkId"
    
    def path = $action
    //def path = "/udp-gateway.php?udbport=6000&broadcastip=192.168.1.255&message={\"action\":\"set_light\",\"MAC\":\"7cc709806aeb\",\"r\":4,\"g\":121,\"b\":12,\"x\":0}" 
    log.debug "path is: $path"
    
    def headers = [:] 
    headers.put("HOST", "$host:$port")
    
    log.debug "The Header is $headers"
    
    def method = "GET"    
    
    log.debug "The method is $method"
    
    try {
    def hubAction = new physicalgraph.device.HubAction(
    	method: method,
    	path: path,
    	headers: headers
        )
        	
    hubAction.options = [outputMsgToS3:true]
    log.debug hubAction
    hubAction
    }
    catch (Exception e) {
    	log.debug "Hit Exception $e on $hubAction"
    }
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02X', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04X', port.toInteger() )
    return hexport
}

private toggleTiles(value) {
   def tiles = ["switch1", "switch2", "switch3", "switch4", "switch5", "switch6"]
   tiles.each {tile ->
      if (tile != value) sendEvent(name: tile, value: "off")
   }
}

private getScaledColor(color) {
   def rgb = color.findAll(/[0-9a-fA-F]{2}/).collect { Integer.parseInt(it, 16) }
   def maxNumber = 1
   for (int i = 0; i < 3; i++){
     if (rgb[i] > maxNumber) {
	    maxNumber = rgb[i]
     }
   }
   def scale = 255/maxNumber
   for (int i = 0; i < 3; i++){
     rgb[i] = rgb[i] * scale
   }
   def myred = rgb[0]
   def mygreen = rgb[1]
   def myblue = rgb[2]
   return rgbToHex([r:myred, g:mygreen, b:myblue])
}

def on() {
	log.debug "on()"
    //postAction("/on?transition=$transition")
}

def off() {
	log.debug "off()"
    //postAction("/off?transition=$transition")
}

def setLevel(level) {
	log.debug "setLevel() level = ${level}"
     if(level > 100) level = 100
     if (level == 0) { lightoff() }
	 else if (device.latestValue("switch") == "off") { lighton() }
	sendEvent(name: "level", value: level)
    sendEvent(name: "setLevel", value: level, displayed: false)
	setColor(aLevel: level)
}
def setSaturation(percent) {
	log.debug "setSaturation($percent)"
	setColor(saturation: percent)
}
def setHue(value) {
	log.debug "setHue($value)"
	setColor(hue: value)
}
def getWhite(value) {
	log.debug "getWhite($value)"
	def level = Math.min(value as Integer, 99)    
    level = 255 * level/99 as Integer
	log.debug "level: ${level}"
	return hex(level)
}
def setColor(value) {
    log.debug "setColor being called with ${value}"
	log.debug "setting color with hex"
       def rgb = value.hex.findAll(/[0-9a-fA-F]{2}/).collect { Integer.parseInt(it, 16) }
       def myred = rgb[0] < 40 ? 0 : rgb[0]
       def mygreen = rgb[1] < 40 ? 0 : rgb[1]
       def myblue = rgb[2] < 40 ? 0 : rgb[2]
       request("/udp-gateway.php?udbport=6000&broadcastip=192.168.1.255&message={\"action\":\"set_light\",\"MAC\":\"7cc709806aeb\",\"r\":" + myred + ",\"g\":" + mygreen + ",\"b\":" + myblue + ",\"x\":0}")
}

private getDimmedColor(color, level) {
   if(color.size() > 2){
      def rgb = color.findAll(/[0-9a-fA-F]{2}/).collect { Integer.parseInt(it, 16) }
      def myred = rgb[0]
      def mygreen = rgb[1]
      def myblue = rgb[2]
    
      color = rgbToHex([r:myred, g:mygreen, b:myblue])
      def c = hexToRgb(color)
    
      def r = hex(c.r * (level.toInteger()/100))
      def g = hex(c.g * (level.toInteger()/100))
      def b = hex(c.b * (level.toInteger()/100))

      return "${r + g + b}"
   }else{
      color = Integer.parseInt(color, 16)
      return hex(color * (level.toInteger()/100))
   }

}

private getDimmedColor(color) {
   if (device.latestValue("level")) {
      def newLevel = device.latestValue("level")
      def colorHex = getScaledColor(color)
      def rgb = colorHex.findAll(/[0-9a-fA-F]{2}/).collect { Integer.parseInt(it, 16) }
      def myred = rgb[0]
      def mygreen = rgb[1]
      def myblue = rgb[2]
    
      colorHex = rgbToHex([r:myred, g:mygreen, b:myblue])
      def c = hexToRgb(colorHex)
    
      def r = hex(c.r * (newLevel/100))
      def g = hex(c.g * (newLevel/100))
      def b = hex(c.b * (newLevel/100))

      return "#${r + g + b}"
   } else {
      return color
   }
}

def reset() {
	log.debug "reset()"
	setColor(white: "ff")
}
def refresh() {
	log.debug "refresh()"
    //postAction("/status")
}

def setWhiteLevel(value) {
	log.debug "setwhiteLevel: ${value}"
    def level = Math.min(value as Integer, 99)    
    level = 255 * level/99 as Integer
	log.debug "level: ${level}"
	if ( value > 0 ) {
    	if (device.latestValue("switch") == "off") { on() }
        sendEvent(name: "white", value: "on")
    } else {
    	sendEvent(name: "white", value: "off")
    }
	def whiteLevel = hex(level)
    setColor(white: whiteLevel)
}

def hexToRgb(colorHex) {
	def rrInt = Integer.parseInt(colorHex.substring(1,3),16)
    def ggInt = Integer.parseInt(colorHex.substring(3,5),16)
    def bbInt = Integer.parseInt(colorHex.substring(5,7),16)
    
    def colorData = [:]
    colorData = [r: rrInt, g: ggInt, b: bbInt]
    colorData
}

def huesatToRGB(float hue, float sat) {
	while(hue >= 100) hue -= 100
	int h = (int)(hue / 100 * 6)
	float f = hue / 100 * 6 - h
	int p = Math.round(255 * (1 - (sat / 100)))
	int q = Math.round(255 * (1 - (sat / 100) * f))
	int t = Math.round(255 * (1 - (sat / 100) * (1 - f)))
	switch (h) {
		case 0: return [255, t, p]
		case 1: return [q, 255, p]
		case 2: return [p, 255, t]
		case 3: return [p, q, 255]
		case 4: return [t, p, 255]
		case 5: return [255, p, q]
	}
}

private hex(value, width=2) {
	def s = new BigInteger(Math.round(value).toString()).toString(16)
	while (s.size() < width) {
		s = "0" + s
	}
	s
}
def rgbToHex(rgb) {
    def r = hex(rgb.r)
    def g = hex(rgb.g)
    def b = hex(rgb.b)
    def hexColor = "#${r}${g}${b}"
    
    hexColor
}

def sync(ip, port) {
    def existingIp = getDataValue("ip")
    def existingPort = getDataValue("port")
    if (ip && ip != existingIp) {
        updateDataValue("ip", ip)
    }
    if (port && port != existingPort) {
        updateDataValue("port", port)
    }
}


def parseDescriptionAsMap(description) {
	description.split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")
		map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
	}
}

private getHeader(userpass = null){
    def headers = [:]
    headers.put("Host", getHostAddress())
    headers.put("Content-Type", "application/x-www-form-urlencoded")
    if (userpass != null)
       headers.put("Authorization", userpass)
    return headers
}

def toAscii(s){
        StringBuilder sb = new StringBuilder();
        String ascString = null;
        long asciiInt;
                for (int i = 0; i < s.length(); i++){
                    sb.append((int)s.charAt(i));
                    sb.append("|");
                    char c = s.charAt(i);
                }
                ascString = sb.toString();
                asciiInt = Long.parseLong(ascString);
                return asciiInt;
}

def on1() { onOffCmd(1, 1) }
def on2() { onOffCmd(1, 2) }
def on3() { onOffCmd(1, 3) }
def on4() { onOffCmd(1, 4) }
def on5() { onOffCmd(1, 5) }
def on6() { onOffCmd(1, 6) }

def off1(p=null) { onOffCmd((p == null ? 0 : p), 1) }
def off2(p=null) { onOffCmd((p == null ? 0 : p), 2) }
def off3(p=null) { onOffCmd((p == null ? 0 : p), 3) }
def off4(p=null) { onOffCmd((p == null ? 0 : p), 4) }
def off5(p=null) { onOffCmd((p == null ? 0 : p), 5) }
def off6(p=null) { onOffCmd((p == null ? 0 : p), 6) }

def onOffCmd(value, program) {
    log.debug "onOffCmd($value, $program)"
    def uri
    if (value == 1){
       if(state."program${program}" != null) {
          uri = "/program?value=${state."program${program}"}&number=$program"
       }    
    } else if(value == 0){
       uri = "/stop"
    } else {
       uri = "/off"
    }
    if (uri != null) return //postAction(uri)
}

def setProgram(value, program){
   state."program${program}" = value
}

def hex2int(value){
   return Integer.parseInt(value, 10)
}

def redOn() {
	log.debug "redOn()"
    //postAction("/r?value=ff&channels=$channels&transition=$transition")
}
def redOff() {
	log.debug "redOff()"
    //postAction("/r?value=00&channels=$channels&transition=$transition")
}

def setRedLevel(value) {
	log.debug "setRedLevel: ${value}"
    def level = Math.min(value as Integer, 99)    
    level = 255 * level/99 as Integer
	log.debug "level: ${level}"
	level = hex(level)
    //postAction("/r?value=$level&channels=$channels&transition=$transition")
}
def greenOn() {
	log.debug "greenOn()"
    //postAction("/g?value=ff&channels=$channels&transition=$transition")
}
def greenOff() {
	log.debug "greenOff()"
    //postAction("/g?value=00&channels=$channels&transition=$transition")
}

def setGreenLevel(value) {
	log.debug "setGreenLevel: ${value}"
    def level = Math.min(value as Integer, 99)    
    level = 255 * level/99 as Integer
	log.debug "level: ${level}"
	level = hex(level)
    //postAction("/g?value=$level&channels=$channels&transition=$transition")
}
def blueOn() {
	log.debug "blueOn()"
    //postAction("/b?value=ff&channels=$channels&transition=$transition")
}
def blueOff() {
	log.debug "blueOff()"
    //postAction("/b?value=00&channels=$channels&transition=$transition")
}

def setBlueLevel(value) {
	log.debug "setBlueLevel: ${value}"
    def level = Math.min(value as Integer, 99)    
    level = 255 * level/99 as Integer
	log.debug "level: ${level}"
	level = hex(level)
    //postAction("/b?value=$level&channels=$channels&transition=$transition")
}
def white1On() {
	log.debug "white1On()"
    //postAction("/w1?value=ff&channels=$channels&transition=$transition")
}
def white1Off() {
	log.debug "white1Off()"
    //postAction("/w1?value=00&channels=$channels&transition=$transition")
}

def setWhite1Level(value) {
	log.debug "setwhite1Level: ${value}"
    def level = Math.min(value as Integer, 99)    
    level = 255 * level/99 as Integer
	log.debug "level: ${level}"
	def whiteLevel = hex(level)
    //postAction("/w1?value=$whiteLevel&channels=$channels&transition=$transition")
}
def white2On() {
	log.debug "white2On()"
    //postAction("/w2?value=ff&channels=$channels&transition=$transition")
}
def white2Off() {
	log.debug "white2Off()"
    //postAction("/w2?value=00&channels=$channels&transition=$transition")
}

def setWhite2Level(value) {
	log.debug "setwhite2Level: ${value}"
    def level = Math.min(value as Integer, 99)    
    level = 255 * level/99 as Integer
	log.debug "level: ${level}"
	def whiteLevel = hex(level)
    //postAction("/w2?value=$whiteLevel&channels=$channels&transition=$transition")
}

private getHexColor(value){
def color = ""
  switch(value){
    case "Previous":
    color = "Previous"
    break;
    case "White":
    color = "ffffff"
    break;
    case "Daylight":
    color = "ffffff"
    break;
    case "Soft White":
    color = "ff"
    break;
    case "Warm White":
    color = "ff"
    break;
    case "Blue":
    color = "0000ff"
    break;
    case "Green":
    color = "00ff00"
    break;
    case "Yellow":
    color = "ffff00"
    break;
    case "Orange":
    color = "ff5a00"
    break;
    case "Purple":
    color = "5a00ff"
    break;
    case "Pink":
    color = "ff00ff"
    break;
    case "Cyan":
    color = "00ffff"
    break;
    case "Red":
    color = "ff0000"
    break;
    case "Off":
    color = "000000"
    break;
    case "Random":
    color = "xxxxxx"
    break;
}
   return color
}