package com.sndyuk.wicket

import org.apache.wicket.spring.injection.annot.SpringComponentInjector
import javax.servlet.http.HttpServletRequest

import org.apache.wicket.javascript.DefaultJavascriptCompressor
import org.apache.wicket.util.crypt.{ ClassCryptFactory, TrivialCrypt }

import org.apache.wicket.settings.ISecuritySettings

import org.apache.wicket.protocol.http.request.CryptedUrlWebRequestCodingStrategy
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy
import org.apache.wicket.request.IRequestCodingStrategy
import org.apache.wicket.protocol.https.HttpsRequestCycleProcessor
import org.apache.wicket.request.IRequestCycleProcessor

import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.protocol.http.servlet.ServletWebRequest

import org.apache.wicket.util.lang.Bytes
import org.apache.wicket.settings.IExceptionSettings
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy

import org.apache.wicket.{ Request, Response }
import org.apache.wicket.protocol.http.{ WebRequest, WebResponse }
import org.apache.wicket.Page
import org.apache.wicket.util.upload.FileUploadException
import org.apache.wicket.WicketRuntimeException

class App extends WebApplication {
  override def getHomePage = classOf[SamplePage]

  override def init = {
    super.init
    addComponentInstantiationListener(new SpringComponentInjector(this))

    getMarkupSettings.setDefaultMarkupEncoding("UTF-8")
    getRequestCycleSettings.setResponseRequestEncoding("UTF-8")

    getMarkupSettings.setStripComments(true)
    getMarkupSettings.setStripWicketTags(true)
    getMarkupSettings.setCompressWhitespace(true)
    getSecuritySettings.setEnforceMounts(true)
    getSecuritySettings.setCryptFactory(
      new ClassCryptFactory(classOf[TrivialCrypt],
        ISecuritySettings.DEFAULT_ENCRYPTION_KEY))
    getResourceSettings.setJavascriptCompressor(new DefaultJavascriptCompressor)
    
    /** For GAEJ
    getResourceSettings.setResourceWatcher(new ModificationWatcher)
    getResourceSettings.setResourcePollFrequency(null)
    */
    getPageSettings.setAutomaticMultiWindowSupport(true)
    getPageSettings.setVersionPagesByDefault(true)

    getApplicationSettings.setInternalErrorPage(classOf[ErrorPage])
    getApplicationSettings.setPageExpiredErrorPage(classOf[ExpiredPage])

    mount(new HybridUrlCodingStrategy("/error", classOf[ErrorPage]))
  }

  override def newRequestCycleProcessor = {

    new HttpsRequestCycleProcessor(null) {

      override def newRequestCodingStrategy = {
        new CryptedUrlWebRequestCodingStrategy(new WebRequestCodingStrategy)
      }
    }
  }

  override def newRequestCycle(request: Request, response: Response) = {
    new AppRequestCycle(this, request.asInstanceOf[WebRequest], response.asInstanceOf[WebResponse])
  }

  /** For GAEJ
  override protected def newWebRequest(servletRequest: HttpServletRequest) = {
    new ServletWebRequest(servletRequest) {
      override def newMultipartWebRequest(maxsize: Bytes) = {
        try {
          new MultipartWebRequest(getHttpServletRequest(), maxsize);
        } catch {
          case e: FileUploadException => throw new WicketRuntimeException(e)
        }
      }
    }
  }
  */
}
object App {
  def get = org.apache.wicket.Application.get.asInstanceOf[App]
}