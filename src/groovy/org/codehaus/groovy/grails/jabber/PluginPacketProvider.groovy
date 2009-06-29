/**
 * 
 */
package org.codehaus.groovy.grails.jabber

import org.apache.log4j.Logger

import java.io.StringReader
import java.util.regex.Pattern

import org.jivesoftware.smack.packet.PacketExtension
import org.jivesoftware.smack.provider.PacketExtensionProvider
import org.xmlpull.v1.XmlPullParser

class PluginPacketProvider implements PacketExtensionProvider
{
    
    private static final Logger log = Logger.getLogger(CollaborationWrapperPacketProvider.class)
    
    String elementName = null
    String namespace = null
    
    PluginPacketProvider(String elementName, String namespace) {
        this.elementName = elementName
        this.namespace = namespace
    }
    
	PacketExtension parseExtension(XmlPullParser parser) throws Exception
	{
		// First parse the XML
		Pattern endTag = Pattern.compile("</.*${elementName}>");
		StringBuffer buffer = new StringBuffer();
		
		while (!endTag.matcher(parser.getText()).matches())
		{
			buffer.append(parser.getText());
			parser.next();
		}
		buffer.append(parser.getText());
		
				// Return a new extension
		log.debug("Returning a new extension");
		return new PluginPacketExtension(buffer.toString(), elementName, namespace);
	}
}
