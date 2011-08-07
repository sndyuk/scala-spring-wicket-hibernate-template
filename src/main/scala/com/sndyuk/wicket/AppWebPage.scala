package com.sndyuk.wicket

import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.markup.html.link.Link

import java.util.{ Locale }

abstract class AppWebPage extends WebPage {
  add(new Link("link.top") {
    override def onClick = setResponsePage(getApplication().getHomePage())
  })

  override def getLocale = Locale.JAPAN
}