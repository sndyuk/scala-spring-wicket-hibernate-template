package com.sndyuk.wicket;

import javax.servlet._
import javax.servlet.{ServletRequest, ServletResponse}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class AppFilter extends Filter with  grizzled.slf4j.Logging {

	override def destroy = {}

	override def doFilter(req: ServletRequest, res: ServletResponse,
			chain: FilterChain) = {

		try {
			chain.doFilter(req, res)
		} catch {
		  case e: Exception => 
		    res.asInstanceOf[HttpServletResponse].sendRedirect("/error")
		    warn("Unknown exception", e)
		}
	}

	override def init(filterConfig: FilterConfig) = {}
}
