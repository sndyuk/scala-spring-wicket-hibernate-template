package com.sndyuk.product

import javax.persistence.{PersistenceContext, EntityManager}
import org.springframework.stereotype.{Service}
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired

@Service
@Transactional
class DefaultProductService extends ProductService {
  @Autowired
  var products: Products = _

  def getAll = products.getAll
}
