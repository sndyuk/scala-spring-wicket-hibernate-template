package com.sndyuk.onomik

import org.mortbay.jetty.Connector
import org.mortbay.jetty.Server
import org.mortbay.jetty.bio.SocketConnector
import org.mortbay.jetty.webapp.WebAppContext

object ManualTest {

  def main(args: Array[String]) {

    val server = new Server()
    val connector = new SocketConnector()

    // Set some timeout options to make debugging easier.
    connector.setMaxIdleTime(1000 * 60 * 60)
    connector.setSoLingerTime(-1)
    connector.setPort(8080)
    server.setConnectors(Array[Connector](connector))

    val bb = new WebAppContext()
    bb.setServer(server)
    bb.setContextPath("/")
    bb.setResourceBase("./src/main/webapp")
    bb.setDescriptor("./src/main/webapp/WEB-INF/web.xml")
    bb.setParentLoaderPriority(true)

    server.addHandler(bb)

    try {
      System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP")
      server.start()
      System.in.read()
      System.out.println(">>> STOPPING EMBEDDED JETTY SERVER")

      server.stop()
      server.join()
    } catch {
      case e:Exception => e.printStackTrace()
    } finally {
      System.exit(100)
    }
  }
}
