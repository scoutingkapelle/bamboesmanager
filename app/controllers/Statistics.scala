package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import models._
import models.daos._
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits._

class Statistics @Inject()(statisticsDAO: StatisticsDAO,
                           val messagesApi: MessagesApi,
                           val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  def friday = SecuredAction.async { implicit request =>
    statisticsDAO.friday.map(statistics => Ok(statistics.toString))
  }

  def saturday = SecuredAction.async { implicit request =>
    statisticsDAO.saturday.map(statistics => Ok(statistics.toString))
  }

  def sorting = SecuredAction.async { implicit request =>
    statisticsDAO.sorting.map(statistics => Ok(statistics.toString))
  }
}