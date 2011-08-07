package com.sndyuk.wicket

import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.util.HashMap
import java.util.Iterator
import java.util.{ ArrayList, List }
import java.util.Map
import javax.servlet.http.HttpServletRequest
import org.apache.wicket.WicketRuntimeException
import org.apache.wicket.protocol.http.IMultipartWebRequest
import org.apache.wicket.protocol.http.servlet.ServletWebRequest
import org.apache.wicket.util.lang.Bytes
import org.apache.wicket.util.upload.DiskFileItemFactory
import org.apache.wicket.util.upload.FileItem
import org.apache.wicket.util.upload.FileUploadException
import org.apache.wicket.util.upload.ServletFileUpload
import org.apache.wicket.util.upload.ServletRequestContext
import org.apache.wicket.util.value.ValueMap
import scala.collection.JavaConversions._

class MultipartWebRequest(request: HttpServletRequest, maxSize: Bytes)
  extends ServletWebRequest(request)
  with IMultipartWebRequest {

  /** Map of file items. */
  private val files = new HashMap[String, FileItem]

  /** Map of parameters. */
  private val parameters = new ValueMap

  /**
   * total bytes uploaded (downloaded from server's pov) so far. used for
   * upload notifications
   */
  private var bytesUploaded = 0

  /** content length cache, used for upload notifications */
  private var totalBytes = 0

  if (maxSize == null) {
    throw new IllegalArgumentException(
      "argument maxSize must be not null");
  }

  // Check that request is multipart
  val isMultipart = ServletFileUpload.isMultipartContent(request)
  if (!isMultipart) {
    throw new IllegalStateException(
      "ServletRequest does not contain multipart content");
  }

  val factory = new DiskFileItemFactory {
    override def createItem(
      fieldName: String,
      contentType: String,
      isFormField: Boolean,
      fileName: String) = new AppFileItem(fieldName, contentType, isFormField, fileName)
  }

  // Configure the factory here, if desired.
  val upload = new ServletFileUpload(factory)

  // The encoding that will be used to decode the string parameters
  // It should NOT be null at this point, but it may be
  // if the older Servlet API 2.2 is used
  val encoding = request.getCharacterEncoding

  // set encoding specifically when we found it
  if (encoding != null) {
    upload.setHeaderEncoding(encoding)
  }

  upload.setSizeMax(maxSize.bytes)

  var items: List[FileItem] = new ArrayList[FileItem]()

  if (wantUploadProgressUpdates) {
    val ctx = new ServletRequestContext(request) {
      override def getInputStream = {
        new CountingInputStream(super.getInputStream)
      }
    }
    totalBytes = request.getContentLength

    onUploadStarted(totalBytes)
    items = upload.parseRequest(ctx)
    onUploadCompleted

  } else {
    items = upload.parseRequest(request)
  }

  for (item <- items) {

    // If item is a form field
    if (item.isFormField) {

      // Set parameter value
      var value: String = null
      if (encoding != null) {
        try {
          value = item.getString(encoding)
        } catch {
          case e: UnsupportedEncodingException => throw new WicketRuntimeException(e)
        }
      } else {
        value = item.getString
      }

      addParameter(item.getFieldName, value)
    } else {
      // Add to file list
      files.put(item.getFieldName, item)
    }
  }

  /**
   * Adds a parameter to the parameters value map
   *
   * @param name
   *            parameter name
   * @param value
   *            parameter value
   */
  private def addParameter(name: String, value: String) = {
    val currVal = parameters.get(name).asInstanceOf[Array[String]]

    var newVal: Array[String] = Array[String]()

    if (currVal != null) {
      newVal = new Array[String](currVal.length + 1);
      System.arraycopy(currVal, 0, newVal, 0, currVal.length)
      newVal(currVal.length) = value
    } else {
      newVal = Array[String](value)
    }
    parameters.put(name, newVal)
  }

  /**
   * @return Returns the files.
   */
  def getFiles = files

  /**
   * Gets the file that was uploaded using the given field name.
   *
   * @param fieldName
   *            the field name that was used for the upload
   * @return the upload with the given field name
   */
  def getFile(fieldName: String) = files.get(fieldName)

  /**
   * @see org.apache.wicket.protocol.http.WebRequest#getParameter(java.lang.String)
   */
  override def getParameter(key: String) = {
    var v = parameters.get(key).asInstanceOf[Array[String]]
    if (v == null) null else v(0)
  }

  /**
   * @see org.apache.wicket.protocol.http.WebRequest#getParameterMap()
   */
  override def getParameterMap = parameters.asInstanceOf[Map[String, Array[String]]]

  /**
   * @see org.apache.wicket.protocol.http.WebRequest#getParameters(java.lang.String)
   */
  override def getParameters(key: String) = parameters.get(key).asInstanceOf[Array[String]]

  /**
   * Subclasses that want to receive upload notifications should return true
   *
   * @return true if upload status update event should be invoked
   */
  protected def wantUploadProgressUpdates = false

  /**
   * Upload start callback
   *
   * @param totalBytes
   */
  protected def onUploadStarted(totalBytes: Int) = {
  }

  /**
   * Upload status update callback
   *
   * @param bytesUploaded
   * @param total
   */
  protected def onUploadUpdate(bytesUploaded: Int, total: Int) = {
  }

  /**
   * Upload completed callback
   */
  protected def onUploadCompleted = {
  }

  /**
   * An {@link InputStream} that updates total number of bytes read
   *
   * @author Igor Vaynberg (ivaynberg)
   */
  private class CountingInputStream(val in: InputStream) extends InputStream {

    /**
     * @see java.io.InputStream#read()
     */
    override def read = {
      val read = in.read
      bytesUploaded += (if (read < 0) 0 else 1)
      onUploadUpdate(bytesUploaded, totalBytes)
      read
    }

    /**
     * @see java.io.InputStream#read(byte[])
     */
    override def read(b: Array[Byte]) = {
      val read = in.read(b)
      bytesUploaded += (if (read < 0) 0 else read)
      onUploadUpdate(bytesUploaded, totalBytes)
      read
    }

    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    override def read(b: Array[Byte], off: Int, len: Int) = {
      val read = in.read(b, off, len)
      bytesUploaded += (if (read < 0) 0 else read)
      onUploadUpdate(bytesUploaded, totalBytes)
      read
    }
  }
}