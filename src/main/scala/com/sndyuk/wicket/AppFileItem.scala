package com.sndyuk.wicket

import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.util.Map

import org.apache.wicket.util.io.ByteArrayOutputStream
import org.apache.wicket.util.upload.FileItem
import org.apache.wicket.util.upload.ParameterParser

class AppFileItem(
  var fieldName: String,
  val contentType: String,
  var formField: Boolean,
  val fileName: String)
  extends FileItem() {

  // ----------------------------------------------------- Manifest constants

  /**
   * Default content charset to be used when no explicit charset parameter is
   * provided by the sender. Media subtypes of the "text" type are defined to
   * have a default charset value of "ISO-8859-1" when received via HTTP.
   */
  val DEFAULT_CHARSET = "ISO-8859-1"

  // ----------------------------------------------------------- Data members

  /**
   * Cached contents of the file.
   */
  private var cachedContent: Array[Byte] = Array[Byte]()

  @transient
  private var memoryOutputStream: ByteArrayOutputStream = new ByteArrayOutputStream

  override def delete = {
    cachedContent = null
    if (memoryOutputStream != null) {
      try {
        memoryOutputStream.close();
      } catch {
        // don't care!
        case _ => None
      }
    }
  }

  override def get: Array[Byte] = {
    if (cachedContent == null) {
      cachedContent = memoryOutputStream.toByteArray
    }
    cachedContent
  }

  override def getContentType = contentType
  override def getFieldName = fieldName

  override def getInputStream = {
    if (cachedContent == null) {
      if (memoryOutputStream == null) {
        memoryOutputStream = new ByteArrayOutputStream
      }
      cachedContent = memoryOutputStream.toByteArray
    }
    new ByteArrayInputStream(cachedContent)
  }

  override def getName = fileName

  override def getOutputStream = {
    if (memoryOutputStream == null) {
      memoryOutputStream = new ByteArrayOutputStream
    }
    memoryOutputStream
  }

  override def getSize = {
    if (cachedContent != null) {
      cachedContent.length
    } else {
      if (memoryOutputStream == null) {
        memoryOutputStream = new ByteArrayOutputStream
      }
      cachedContent = memoryOutputStream.toByteArray
      memoryOutputStream.size
    }
  }

  override def getString = {
    val rawdata = get
    var charset = getCharSet
    if (charset == null) {
      charset = DEFAULT_CHARSET
    }
    try {
      new String(rawdata, charset)
    } catch {
      case _ => new String(rawdata)
    }
  }

  override def getString(encoding: String) = {
    new String(get, encoding)
  }

  override def isFormField = formField

  override def isInMemory = true

  override def setFieldName(name: String) = fieldName = name

  override def setFormField(state: Boolean) = formField = state

  override def write(file: File) = throw new UnsupportedOperationException

  def getCharSet = {
    val parser = new ParameterParser
    parser.setLowerCaseNames(true)
    // Parameter parser can handle null input
    val params = parser.parse(getContentType(), ';')
    params.get("charset").asInstanceOf[String]
  }

  override def toString = {
    "name=" + getName + ", size=" + getSize + "bytes, " +
      "isFormField=" + isFormField + ", FieldName=" + getFieldName
  }
}
