package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import forms.CategoryForm
import models.daos.{StatisticsDAO, CategoryDAO, RegistrationDAO}
import models.{Category, User}
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Categories @Inject()(categoryDAO: CategoryDAO,
                           registrationDAO: RegistrationDAO,
                           statisticsDAO: StatisticsDAO,
                           val messagesApi: MessagesApi,
                           val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  implicit val categoryWrites = Json.writes[Category]

  def categories = SecuredAction.async { implicit request =>
    for {
      categories <- categoryDAO.all
      stats <- statisticsDAO.category
      teamLeaders <- categoryDAO.teamLeaders
    } yield {
      val tls = teamLeaders.groupBy(_._1).map(cat => cat._1 -> cat._2.map(_._2).mkString(", "))
      Ok(views.html.categories(categories.sortBy(_.name), stats.toMap, tls, request.identity))
    }
  }

  def category(id: String) = SecuredAction.async { implicit request =>
    try {
      val uuid = UUID.fromString(id)
      for {
        registrations <- registrationDAO.category(uuid)
        category <- categoryDAO.get(uuid)
      } yield {
        category match {
          case Some(category) => Ok(views.html.category(category, registrations, request.identity))
          case None => NotFound(views.html.notFound(id, Some(request.identity)))
        }
      }
    } catch {
      case _: IllegalArgumentException => Future(BadRequest(Json.toJson(Messages("uuid.invalid"))))
    }
  }

  def add = SecuredAction.async { implicit request =>
    Future(Ok(views.html.categoryAdd(CategoryForm.form, request.identity)))
  }

  def save = SecuredAction.async { implicit request =>
    CategoryForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.categoryAdd(form, request.identity))),
      data => {
        val category = Category(UUID.randomUUID, data.name)
        categoryDAO.save(category).map(_ => Redirect(routes.Categories.categories))
      }
    )
  }

  def all = SecuredAction.async {
    categoryDAO.all.map(categories => Ok(Json.toJson(categories)))
  }

  def get(id: String) = SecuredAction.async {
    try {
      val uuid = UUID.fromString(id)
      categoryDAO.get(uuid).map {
        case Some(category) => Ok(Json.toJson(category))
        case None => NotFound(Json.toJson(Messages("category.not_found")))
      }
    } catch {
      case _: IllegalArgumentException => Future(BadRequest(Json.toJson(Messages("uuid.invalid"))))
    }
  }
}