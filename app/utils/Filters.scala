package utils

import javax.inject.Inject

import play.api.http.HttpFilters
import play.api.mvc._
import play.filters.csrf.CSRFFilter
import play.filters.headers.SecurityHeadersFilter

/**
  * Provides filters.
  */
class Filters @Inject()(csrfFilter: CSRFFilter, securityHeadersFilter: SecurityHeadersFilter, tlsFilter: TLSFilter) extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(csrfFilter, securityHeadersFilter, tlsFilter)
}