package com.sndyuk.site

import javax.persistence.{GeneratedValue, Id, Entity}
import java.util.Date

@Entity
@serializable
class Site {
  
  @Id
  @GeneratedValue
  var id: Long = _
  var name: String = _
  var url: String = _
  var updatedDate: Date = _
  var changeFrequency: String = _
  var priority: Int = _
}
