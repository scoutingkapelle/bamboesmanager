package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import models._
import models.daos._
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.collection.immutable.ListMap
import scala.concurrent.ExecutionContext.Implicits._

class Statistics @Inject()(statisticsDAO: StatisticsDAO,
                           organisationDAO: OrganisationDAO,
                           val messagesApi: MessagesApi,
                           val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  def statistics: Action[AnyContent] = SecuredAction.async { implicit request =>
    for {
      friday <- statisticsDAO.friday
      saturday <- statisticsDAO.saturday
      sorting <- statisticsDAO.sorting
      selling <- statisticsDAO.selling
      organisations <- organisationDAO.all
    } yield {
      val statistics = ListMap(
        Messages("friday") -> fill(organisations, friday),
        Messages("saturday") -> fill(organisations, saturday),
        Messages("sorting") -> fill(organisations, sorting),
        Messages("selling") -> fill(organisations, selling)
      )
      Ok(views.html.statistics(statistics, request.identity))
    }
  }

  private def fill(organisations: Seq[Organisation], statistic: Seq[(String, Int)]): Map[String, Int] =
    organisations.map(organisation => (organisation.name, statistic.toMap.getOrElse(organisation.name, 0))).toMap

  def friday: Action[AnyContent] = SecuredAction.async { implicit request =>
    statisticsDAO.friday.map(statistics => Ok(Json.toJson(statistics.toMap)))
  }

  def saturday: Action[AnyContent] = SecuredAction.async { implicit request =>
    statisticsDAO.saturday.map(statistics => Ok(Json.toJson(statistics.toMap)))
  }

  def sorting: Action[AnyContent] = SecuredAction.async { implicit request =>
    statisticsDAO.sorting.map(statistics => Ok(Json.toJson(statistics.toMap)))
  }

  def selling: Action[AnyContent] = SecuredAction.async { implicit request =>
    statisticsDAO.selling.map(statistics => Ok(Json.toJson(statistics.toMap)))
  }
}