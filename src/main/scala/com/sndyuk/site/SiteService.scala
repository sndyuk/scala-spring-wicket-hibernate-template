package com.sndyuk.site

import java.util.List

trait SiteService {
  def getAll: List[Site]
}
