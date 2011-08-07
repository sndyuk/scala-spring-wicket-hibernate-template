package com.sndyuk.wicket

import org.apache.wicket.markup.html.WebPage
import javax.servlet.http.HttpServletResponse

class ExpiredPage extends AppWebPage {

  override def isVersioned = false
  
  override protected def configureResponse = {
    super.configureResponse
    getWebRequestCycle.getWebResponse.getHttpServletResponse.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY)
  }
}
