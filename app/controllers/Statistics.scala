package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import models._
import models.daos._
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Statistics @Inject()(statisticsDAO: StatisticsDAO,
                           val messagesApi: MessagesApi,
                           val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  def statistics = SecuredAction.async { implicit request =>
    statisticsDAO.friday.flatMap(friday =>
      statisticsDAO.saturday.flatMap(saturday =>
        statisticsDAO.sorting.flatMap(sorting =>
          statisticsDAO.selling.flatMap(selling => {
            val statistics = Map(
              Messages("friday") -> friday.sortBy(_._1).toMap,
              Messages("saturday") -> saturday.sortBy(_._1).toMap,
              Messages("sorting") -> sorting.sortBy(_._1).toMap,
              Messages("selling") -> selling.sortBy(_._1).toMap
            )
            Future.successful(Ok(views.html.statistics(statistics, request.identity)))
          }
          )
        )
      )
    )
  }

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
}