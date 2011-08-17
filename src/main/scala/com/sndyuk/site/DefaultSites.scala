package com.sndyuk.site

import javax.persistence.{ PersistenceContext, EntityManager }
import javax.persistence.criteria._
import org.springframework.stereotype.Repository
import java.util.List

@Repository
class DefaultSites extends Sites {
  @PersistenceContext
  var em: EntityManager = _

  def getAll = {

    val builder = em.getCriteriaBuilder
    val criteria = builder.createQuery(classOf[Site])
    val root = criteria.from(classOf[Site])
    criteria.select(root)
    em.createQuery(criteria).getResultList
  }
}
