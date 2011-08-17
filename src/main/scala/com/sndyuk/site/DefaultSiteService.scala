package com.sndyuk.site

import javax.persistence.{PersistenceContext, EntityManager}
import org.springframework.stereotype.{Service}
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired

@Service
@Transactional
class DefaultSiteService extends SiteService {
  @Autowired
  var sites: Sites = _

  def getAll = sites.getAll
}
