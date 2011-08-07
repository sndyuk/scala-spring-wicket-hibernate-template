package com.sndyuk.product

import java.util.List

trait ProductService {
  def getAll: List[Product]
}
