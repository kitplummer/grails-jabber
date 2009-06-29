/**
 * 
 */
package org.codehaus.groovy.grails.jabber

import java.io.StringReader
import java.util.regex.Pattern
import org.apache.log4j.Logger
import org.jivesoftware.smack.packet.PacketExtension
import org.jivesoftware.smack.provider.PacketExtensionProvider

class PluginPacketExtension implements PacketExtension
{
	String wrapper = null
	String elementName = null
	String namespace = null
	
	/**
	 * Just taking in a simple buffer
	 */
	PluginPacketExtension(String wrapper, String elementName, String namespace)
	{
		this.wrapper = wrapper
		this.elementName = elementName
		this.namespace = namespace
	}

	String getWrapper()
	{
		return wrapper
	}

	String getElementName()
	{
		return this.elementName
	}

	String getNamespace()
	{
		return this.namespace
	}

	String toXML()
	{
		return wrapper
	}
	
}