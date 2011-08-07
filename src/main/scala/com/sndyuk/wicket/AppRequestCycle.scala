package com.sndyuk.wicket

import org.apache.wicket.{ Request, Response }
import org.apache.wicket.protocol.http.{ WebRequest, WebResponse }
import org.apache.wicket.Page
import org.apache.wicket.protocol.http.WebRequestCycle
import org.apache.wicket.protocol.http.WebApplication

class AppRequestCycle(application: WebApplication, request: WebRequest, response: Response)
	extends WebRequestCycle(application, request, response) {

	override def onRuntimeException(cause: Page, e: RuntimeException) = {
		new ErrorPage
	}
}
