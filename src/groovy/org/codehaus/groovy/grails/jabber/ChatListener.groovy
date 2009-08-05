package org.codehaus.groovy.grails.jabber

import java.io.StringReader;
import java.util.regex.Pattern;

import groovy.xml.MarkupBuilder

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.PacketListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.filter.PacketFilter
import org.jivesoftware.smack.filter.PacketTypeFilter
import org.jivesoftware.smack.filter.FromMatchesFilter
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.provider.PacketExtensionProvider
import org.jivesoftware.smack.provider.ProviderManager
import org.jivesoftware.smack.packet.PacketExtension

import org.jivesoftware.smackx.muc.DiscussionHistory
import org.jivesoftware.smackx.muc.MultiUserChat

import org.xmlpull.v1.XmlPullParser

import org.apache.log4j.Logger

/**
 * A simple chat bot service using Jabber.  Based on http://memo.feedlr.com/?p=11 and
 * http://blogs.bytecode.com.au/glen/2008/01/03/gravl--google-talk-notifier.html
 *
 * @author Glen Smith
 *
 */
class ChatListener {

    private static final Logger log = Logger.getLogger(ChatListener.class)

    XMPPConnection connection

    def host
    def port = 5222
    def serviceName = "XMPP"
    def userName
    def password

    // if we're going to listen to a MUC too
    def chatRoom
    
    // and we're needing an extension handler
    def elementName
    def namespace
    
    def listenerMethod
    def targetService

    def connect = {
        try{
            ProviderManager.getInstance().addExtensionProvider(elementName, 
                            namespace, 
                            new PluginPacketProvider(elementName, namespace))
                
            ConnectionConfiguration cc = new ConnectionConfiguration(host,
                port, serviceName)

            connection = new XMPPConnection(cc)

            log.debug "Connecting to Jabber server and ${chatRoom} room"
            connection.connect()
            connection.login(userName, password, userName + Long.toHexString(System.currentTimeMillis()))
            log.debug "Connected to Jabber server: ${connection.isConnected()}"
        
            // obviously only join MUC if chatRoom is defined
            if (chatRoom) {
                DiscussionHistory dh = new DiscussionHistory()
                dh.setMaxStansas(0)
                MultiUserChat muc = new MultiUserChat(connection, chatRoom, dh, 10000)
                muc.join(userName)
            }
            
             
                
        } catch(Exception e) {
            // throwing a prop not found on the e...
            log.error "Jabber Connection failed: $e.message", e
        }

    }
    
    def listen = { 
        
        def msgFilter
        
        if (!connection)
            connect()
        
        if (chatRoom) {
            msgFilter = new FromMatchesFilter(chatRoom)
        } else {
            msgFilter = new PacketTypeFilter(Message.class)
        }

        def myListener = [processPacket: { packet ->

                log.debug "Received message from ${packet.from}, subject: ${packet.subject}, body: ${packet.body}, extensions: ${packet.extensionsXML}"
                // callback(packet)

                targetService[listenerMethod].call(packet)

        }] as PacketListener

        log.debug "Adding Jabber listnener..."
        connection.addPacketListener(myListener, msgFilter)

    }

    def disconnect() {

        if (connection && connection.isConnected())
        connection.disconnect()

    }

    def sendJabberMessage(String to, String msg) {
        if (!connection)
           connect()

        Chat chat = connection.chatManager.createChat(to, null)
        def msgObj = new Message(to, Message.Type.chat)
        msgObj.setBody(msg)

        log.debug "Sending Jabber message to ${to} with content ${msg}"
        chat.sendMessage(msgObj)
    }

}