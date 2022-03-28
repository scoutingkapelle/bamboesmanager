package controllers

import com.mohiva.play.silhouette.api.Silhouette
import forms.OrganisationForm
import models._
import models.daos.{OrganisationDAO, StatisticsDAO}
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import utils.DefaultEnv

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class Organisations @Inject()(organisationDAO: OrganisationDAO,
                              statisticsDAO: StatisticsDAO,
                              components: ControllerComponents,
                              silhouette: Silhouette[DefaultEnv],
                              organisationsTemplate: views.html.organisations,
                              organisationTemplate: views.html.organisation,
                              organisationAddTemplate: views.html.organisationAdd,
                              notFoundTemplate: views.html.notFound,
                              badRequestTemplate: views.html.badRequest)
                             (implicit ec: ExecutionContext)
  extends AbstractController(components) with I18nSupport {

  def organisations(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    for {
      organisations <- organisationDAO.all
      statistics <- statisticsDAO.organisation
    } yield {
      Ok(organisationsTemplate(organisations.sortBy(_.name), statistics, request.identity))
    }
  }

  def organisation(id: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    try {
      val uuid = UUID.fromString(id)
      for {
        groups <- organisationDAO.members(uuid)
        organisation <- organisationDAO.get(uuid)
        stats <- statisticsDAO.organisation(uuid)
      } yield {
        organisation match {
          case Some(o) => Ok(organisationTemplate(o, groups.sortBy(_.name), stats, request.identity))
          case None => NotFound(notFoundTemplate(id, Some(request.identity)))
        }
      }
    } catch {
      case _: IllegalArgumentException =>
        Future.successful(BadRequest(badRequestTemplate(Messages("uuid.invalid"), Some(request.identity))))
    }
  }

  def add(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(organisationAddTemplate(OrganisationForm.form, request.identity)))
  }

  def save(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    OrganisationForm.form.bindFromRequest().fold(
      form => Future.successful(BadRequest(organisationAddTemplate(form, request.identity))),
      data => {
        val organisation = Organisation(UUID.randomUUID, data.name)
        for {
          _ <- organisationDAO.save(organisation)
          groups <- organisationDAO.members(organisation.id)
          stats <- statisticsDAO.organisation(organisation.id)
        } yield {
          Ok(organisationTemplate(organisation, groups, stats, request.identity))
        }
      }
    )
  }

  def all(): Action[AnyContent] = silhouette.SecuredAction.async {
    organisationDAO.all.map(organisations => Ok(Json.toJson(organisations.sortBy(_.name))))
  }

  def get(id: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    try {
      val uuid = UUID.fromString(id)
      organisationDAO.get(uuid).map {
        case Some(organisation) => Ok(Json.toJson(organisation))
        case None => NotFound(Json.toJson(Messages("organisation.not_found")))
      }
    } catch {
      case _: IllegalArgumentException => Future.successful(BadRequest(Json.toJson(Messages("uuid.invalid"))))
    }
  }
}
