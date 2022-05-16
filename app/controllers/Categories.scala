package controllers

import io.github.honeycombcheesecake.play.silhouette.api.Silhouette
import forms.CategoryForm
import models.Category
import models.daos.{CategoryDAO, RegistrationDAO, StatisticsDAO}
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import utils.DefaultEnv

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class Categories @Inject()(categoryDAO: CategoryDAO,
                           registrationDAO: RegistrationDAO,
                           statisticsDAO: StatisticsDAO,
                           components: ControllerComponents,
                           silhouette: Silhouette[DefaultEnv],
                           categoriesTemplate: views.html.categories,
                           categoryTemplate: views.html.category,
                           categoryAddTemplate: views.html.categoryAdd,
                           notFoundTemplate: views.html.notFound,
                           badRequestTemplate: views.html.badRequest)
                          (implicit ec: ExecutionContext)
  extends AbstractController(components) with I18nSupport {

  def categories(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    for {
      categories <- categoryDAO.all
      statistics <- statisticsDAO.category
      teamLeaders <- categoryDAO.teamLeaders
    } yield {
      Ok(categoriesTemplate(categories.sortBy(_.name), statistics, teamLeaders, request.identity))
    }
  }

  def category(id: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    try {
      val uuid = UUID.fromString(id)
      for {
        registrations <- registrationDAO.category(uuid)
        category <- categoryDAO.get(uuid)
      } yield {
        category match {
          case Some(c) => Ok(categoryTemplate(c, registrations.sortBy(_.person.name), request.identity))
          case None => NotFound(notFoundTemplate(id, Some(request.identity)))
        }
      }
    } catch {
      case _: IllegalArgumentException =>
        Future.successful(BadRequest(badRequestTemplate(Messages("uuid.invalid"), Some(request.identity))))
    }
  }

  def add(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(categoryAddTemplate(CategoryForm.form, request.identity)))
  }

  def save(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    CategoryForm.form.bindFromRequest().fold(
      form => Future.successful(BadRequest(categoryAddTemplate(form, request.identity))),
      data => {
        val category = Category(UUID.randomUUID, data.name)
        categoryDAO.save(category).map(_ => Redirect(routes.Categories.categories()))
      }
    )
  }

  def all(): Action[AnyContent] = silhouette.SecuredAction.async {
    categoryDAO.all.map(categories => Ok(Json.toJson(categories.sortBy(_.name))))
  }

  def get(id: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    try {
      val uuid = UUID.fromString(id)
      categoryDAO.get(uuid).map {
        case Some(category) => Ok(Json.toJson(category))
        case None => NotFound(Json.toJson(Messages("category.not_found")))
      }
    } catch {
      case _: IllegalArgumentException => Future.successful(BadRequest(Json.toJson(Messages("uuid.invalid"))))
    }
  }
}
