package com.sndyuk.product

import javax.persistence.{GeneratedValue, Id, Entity}

@Entity
@serializable
class Product {
  
  @Id
  @GeneratedValue
  var id: Long = _
  var name: String = _
  var price: Double = _
}
