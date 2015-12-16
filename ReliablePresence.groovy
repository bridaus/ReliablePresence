/**
 *  Reliable Presence
 *
 *  Copyright 2015 Brian Gudauskas
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
 * 12/12 Created version for reliable presence
 */
definition(
    name: "Reliable Presence",
    namespace: "bridaus",
    author: "Brian Gudauskas",
    description: "Will set a simulated presence sensor based on any arrival/any departure.",
    category: "Fun & Social",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Select Presence Sensor Group") {
		input "presenceSensors", "capability.presenceSensor", title: "Presence Sensors", multiple: true, required: true
        input "simulatedPresence", "device.simulatedPresenceSensor", title: "Simulated Presence Sensor", multiple: false, required: true
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	setPresence()
	subscribe(presenceSensors, "presence", "presenceHandler")
}

def presenceHandler(evt) {

    if (evt.value == "present") {
        if (simulatedPresence.currentValue("presence") != "present") {
            simulatedPresence.arrived()
            log.debug("Reliable Arrival")
            }
    } else if (evt.value == "not present") {
        if (simulatedPresence.currentValue("presence") != "not present") {
            simulatedPresence.departed()
            log.debug("Reliable Departure")
        }
    }
}

def setPresence(){
	def presentCounter = 0
    
    presenceSensors.each {
    	if (it.currentValue("presence") == "present") {
        	presentCounter++
        }
    }
    
    log.debug("presentCounter: ${presentCounter}, simulatedPresence: ${simulatedPresence.currentValue("presence")}")
    
    if (presentCounter > 0) {
    	if (simulatedPresence.currentValue("presence") != "present") {
    		simulatedPresence.arrived()
            log.debug("Reliable Arrival")
        }
    } else {
    	if (simulatedPresence.currentValue("presence") != "not present") {
    		simulatedPresence.departed()
            log.debug("Reliable Departure")
        }
    }
}
