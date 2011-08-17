package com.sndyuk.wicket

import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletResponse
import org.apache.wicket.model.LoadableDetachableModel
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.spring.injection.annot.SpringBean
import com.sndyuk.site._
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.Page

class SitemapPage extends Page {

  override def getMarkupType = "xml"
  @SpringBean
  var siteService: SiteService = _
    
	def createListView = {
    val urlListModel = new LoadableDetachableModel[java.util.List[Site]]() {
        override protected def load = siteService.getAll
    }
    
    val listView = new ListView("urlList", urlListModel) {
        override protected def populateItem(item: ListItem[Site]) {
            val entry = item.getModelObject.asInstanceOf[Site]
            item.add(new Label("locNode", entry.url))
            item.add( new Label( "lastmodNode", new SimpleDateFormat( "yyyy-MM-dd").format(entry.updatedDate)))
            item.add(new Label( "changefreqNode", entry.changeFrequency))
            item.add(new Label( "priorityNode", String.valueOf(entry.priority)))
        }
    }
    add(listView)
  }
  
  createListView
}
