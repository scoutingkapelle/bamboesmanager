package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import models._
import models.daos._
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.Json

import scala.collection.immutable.ListMap
import scala.concurrent.ExecutionContext.Implicits._

class Statistics @Inject()(statisticsDAO: StatisticsDAO,
                           organisationDAO: OrganisationDAO,
                           val messagesApi: MessagesApi,
                           val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  def statistics = SecuredAction.async { implicit request =>
    for {
      friday <- statisticsDAO.friday
      saturday <- statisticsDAO.saturday
      sorting <- statisticsDAO.sorting
      selling <- statisticsDAO.selling
      bbq <- statisticsDAO.bbq
      bbqPartner <- statisticsDAO.bbqPartner
      organisations <- organisationDAO.all
    } yield {
      val statistics = ListMap(
        Messages("friday") -> fill(organisations, friday),
        Messages("saturday") -> fill(organisations, saturday),
        Messages("sorting") -> fill(organisations, sorting),
        Messages("selling") -> fill(organisations, selling),
        Messages("bbq") -> fill(organisations, bbq),
        Messages("bbq.partner") -> fill(organisations, bbqPartner)
      )
      Ok(views.html.statistics(statistics, request.identity))
    }
  }

  def fill(organisations: Seq[Organisation], statistic: Seq[(String, Int)]): Map[String, Int] =
    organisations.map(organisation => (organisation.name, statistic.toMap.getOrElse(organisation.name, 0))).toMap

  def friday = SecuredAction.async { implicit request =>
    statisticsDAO.friday.map(statistics => Ok(Json.toJson(statistics.toMap)))
  }

  def saturday = SecuredAction.async { implicit request =>
    statisticsDAO.saturday.map(statistics => Ok(Json.toJson(statistics.toMap)))
  }

  def sorting = SecuredAction.async { implicit request =>
    statisticsDAO.sorting.map(statistics => Ok(Json.toJson(statistics.toMap)))
  }

  def selling = SecuredAction.async { implicit request =>
    statisticsDAO.selling.map(statistics => Ok(Json.toJson(statistics.toMap)))
  }

  def bbq = SecuredAction.async { implicit request =>
    statisticsDAO.bbq.map(statistics => Ok(Json.toJson(statistics.toMap)))
  }

  def bbqPartner = SecuredAction.async { implicit request =>
    statisticsDAO.bbqPartner.map(statistics => Ok(Json.toJson(statistics.toMap)))
  }
}