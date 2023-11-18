package utils

import org.apache.pekko.stream.Materializer

import javax.inject.Inject
import play.api.Environment
import play.api.mvc.{Filter, RequestHeader, Result, Results}

import scala.concurrent.{ExecutionContext, Future}

class TLSFilter @Inject()(env: Environment, val mat: Materializer)(implicit ec: ExecutionContext) extends Filter {
  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    if (requestHeader.headers.get("X-Forwarded-Proto").getOrElse("http") != "https" && env.mode == play.api.Mode.Prod)
      Future.successful(Results.MovedPermanently("https://" + requestHeader.host + requestHeader.uri))
    else
      nextFilter(requestHeader).map(_.withHeaders("Strict-Transport-Security" -> "max-age=31536000"))
  }
}
