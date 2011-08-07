package com.sndyuk.wicket

import javax.servlet.http.HttpServletResponse

class ErrorPage extends AppWebPage {

  override def isVersioned = false
  override def isErrorPage = true

  override protected def configureResponse = {
    super.configureResponse
    getWebRequestCycle.getWebResponse.getHttpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
  }
}
