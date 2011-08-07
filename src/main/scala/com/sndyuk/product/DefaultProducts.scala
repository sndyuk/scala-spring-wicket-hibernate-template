package com.sndyuk.product

import javax.persistence.{ PersistenceContext, EntityManager }
import javax.persistence.criteria._
import org.springframework.stereotype.Repository
import java.util.List

@Repository
class DefaultProducts extends Products {
  @PersistenceContext
  var em: EntityManager = _

  def getAll = {

    val builder = em.getCriteriaBuilder
    val criteria = builder.createQuery(classOf[Product])
    val root = criteria.from(classOf[Product])
    criteria.select(root)
    em.createQuery(criteria).getResultList
  }
}
