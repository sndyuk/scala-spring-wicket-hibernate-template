package com.sndyuk.wicket

import org.apache.wicket.markup.html._
import basic.Label
import list.{ListItem, ListView}
import org.apache.wicket.spring.injection.annot.SpringBean
import org.apache.wicket.model.CompoundPropertyModel
import com.sndyuk.product._

class SamplePage extends AppWebPage {
  
  @SpringBean
  var ps: ProductService = _
  
  val productListView = new ListView[Product]("productListView", ps.getAll) {
    override def populateItem(item: ListItem[Product]) = {
      item.setModel(new CompoundPropertyModel[Product](item.getDefaultModelObject))
      item.add(new Label("name"))
      item.add(new Label("price"))
    }
  }
  add(productListView)
}
