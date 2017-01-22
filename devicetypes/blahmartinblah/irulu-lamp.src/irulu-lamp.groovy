/**
 *  iRULU Lamp (Published on github)
 *
 *  Copyright 2017 Martin Petersen
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * Martin's preference settings:
 * color		Previous
 * DeviceIP		192.168.1.255 (IP Address of the lamp - static IP address recommended)
 * DevicePort	6000
 * devname		7cc709806aeb (Obtainable from the Ai Music App)
 * gatewayIP	192.168.1.203 (IP Address of the server the gateway script is ran on)
 * gatewayPort	80
 *
 */
preferences {
	input("devname", "text", title: "Device Name", description: "The devices name",required:true)
	input("deviceIP", "text", title: "IP", description: "The device IP",required:true)
    input("devicePort", "text", title: "Port", description: "The device Port",required:true)
    input("gatewayIP", "text", title: "gatewayIP", description: "The gateway IP",required:true)
    input("gatewayPort", "text", title: "gatewayPort", description: "The gateway Port",required:true)    
}

metadata {
	definition (name: "iRULU Lamp", namespace: "blahmartinblah", author: "Martin Petersen") {
		capability "Color Control"
		capability "Color Temperature"
		capability "Configuration"
		capability "Refresh"
		capability "Switch"
		capability "Switch Level"
        command "candleon"
        command "candleoff"
        command "nighton"
        command "nightoff"
attribute "atmosphere", "enum", ["candle", "night"]
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"off"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"on"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
			tileAttribute ("device.color", key: "COLOR_CONTROL") {
				attributeState "color", action:"setColor"
			}
	 	}
standardTile("tileName", "device.candle", width: 2, height: 2) {
    state "off", label: '${name}', icon: "st.Seasonal Winter.seasonal-winter-011", backgroundColor: "#ffffff", action: "candleon", nextState:"on"
    state "on", label: '${name}', icon: "st.Seasonal Winter.seasonal-winter-011", backgroundColor: "#79b821", action: "candleoff", nextState:"off"
}
standardTile("tileName", "device.night", width: 2, height: 2) {
    state "off", label: '${name}', icon: "st.Weather.weather4", backgroundColor: "#ffffff", action: "nighton", nextState:"on"
    state "on", label: '${name}', icon: "st.Weather.weather4", backgroundColor: "#79b821", action: "nightoff", nextState:"off"
}
    main("switch")
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'hue' attribute
	// TODO: handle 'saturation' attribute
	// TODO: handle 'color' attribute
	// TODO: handle 'colorTemperature' attribute
	// TODO: handle 'switch' attribute
	// TODO: handle 'level' attribute

}

// handle commands
def setHue() {
	log.debug "Executing 'setHue'"
	// TODO: handle 'setHue' command
}

def setSaturation() {
	log.debug "Executing 'setSaturation'"
	// TODO: handle 'setSaturation' command
}

def setColor() {
	log.debug "Executing 'setColor'"
	// TODO: handle 'setColor' command
}

def setColorTemperature() {
	log.debug "Executing 'setColorTemperature'"
	// TODO: handle 'setColorTemperature' command
}

def configure() {
	log.debug "Executing 'configure'"
	// TODO: handle 'configure' command
}

def refresh() {
	log.debug "Executing 'refresh'"
	// TODO: handle 'refresh' command
}

def on() {
	log.debug "Executing 'on'"
    sendEvent(name: "switch", value: 'on')
    request("/udp-gateway.php?udbport=6000&broadcastip=" + deviceIP + "&message={\"action\":\"light_control\",\"MAC\":\"" + devname + "\",\"type\":\"on\"}")    
}

def off() {
	log.debug "Executing 'off'"
    sendEvent(name: "switch", value: 'off')
request("/udp-gateway.php?udbport=6000&broadcastip=" + deviceIP + "&message={\"action\":\"light_control\",\"MAC\":\"" + devname + "\",\"type\":\"off\"}")
}

def candleon() {
// {"action":"atmosphere","MAC":"7cc709806aeb","type":"candle"}
	log.debug "Executing 'candleon'"
	request("/udp-gateway.php?udbport=6000&broadcastip=" + deviceIP + "&message={\"action\":\"atmosphere\",\"MAC\":\"" + devname + "\",\"type\":\"candle\"}")    
}

def candleoff() {
	log.debug "Executing 'candleoff'"
	request("/udp-gateway.php?udbport=6000&broadcastip=" + deviceIP + "&message={\"action\":\"light_control\",\"MAC\":\"" + devname + "\",\"type\":\"off\"}")
}

def nighton() {
	log.debug "Executing 'nighton'"
	request("/udp-gateway.php?udbport=6000&broadcastip=" + deviceIP + "&message={\"action\":\"atmosphere\",\"MAC\":\"" + devname + "\",\"type\":\"night\"}")    
}

def nightoff() {
	log.debug "Executing 'candleoff'"
    sendEvent(name: "light", value: 'lightoff')
	request("/udp-gateway.php?udbport=6000&broadcastip=" + deviceIP + "&message={\"action\":\"light_control\",\"MAC\":\"" + devname + "\",\"type\":\"off\"}")
}

def setLevel() {
	log.debug "Executing 'setLevel'"
	// TODO: handle 'setLevel' command
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
       on()
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