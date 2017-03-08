package filters

import javax.inject._

import play.api.Environment
import play.api.http.HttpFilters
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.concurrent.Future

class TLSFilter @Inject()(env: Environment) extends Filter {
  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    if (requestHeader.headers.get("X-Forwarded-Proto").getOrElse("http") != "https" && env.mode == play.api.Mode.Prod)
      Future.successful(Results.MovedPermanently("https://" + requestHeader.host + requestHeader.uri))
    else
      nextFilter(requestHeader).map(_.withHeaders("Strict-Transport-Security" -> "max-age=31536000"))
  }
}

class Filters @Inject()(tls: TLSFilter) extends HttpFilters {
  val filters = Seq(tls)
}