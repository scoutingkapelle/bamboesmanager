package controllers

import java.util.UUID
import javax.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import forms.GroupForm
import models._
import models.daos.{GroupDAO, OrganisationDAO, RegistrationDAO, StatisticsDAO}
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import utils.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Groups @Inject()(groupDAO: GroupDAO,
                       organisationDAO: OrganisationDAO,
                       registrationDAO: RegistrationDAO,
                       statisticsDAO: StatisticsDAO,
                       components: ControllerComponents,
                       silhouette: Silhouette[DefaultEnv],
                       groupsTemplate: views.html.groups,
                       groupTemplate: views.html.group,
                       groupAddTemplate: views.html.groupAdd,
                       notFoundTemplate: views.html.notFound,
                       badRequestTemplate: views.html.badRequest)
  extends AbstractController(components) with I18nSupport {

  def groups(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    for {
      groups <- groupDAO.all
      statistics <- statisticsDAO.group
    } yield {
      Ok(groupsTemplate(groups.sortBy(group => (group.organisation.name, group.name)), statistics, request.identity))
    }
  }

  def group(id: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    try {
      val uuid = UUID.fromString(id)
      for {
        persons <- registrationDAO.group(uuid)
        group <- groupDAO.get(uuid)
      } yield {
        group match {
          case Some(g) => Ok(groupTemplate(g, persons.sortBy(_.person.name), request.identity))
          case None => NotFound(notFoundTemplate(id, Some(request.identity)))
        }
      }
    } catch {
      case _: IllegalArgumentException =>
        Future.successful(BadRequest(badRequestTemplate(Messages("uuid.invalid"), Some(request.identity))))
    }
  }

  def add(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    organisationDAO.all.map(organisations =>
      Ok(groupAddTemplate(GroupForm.form, organisationsTupled(organisations), request.identity)))
  }

  def save(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    GroupForm.form.bindFromRequest().fold(
      form => {
        organisationDAO.all.map(organisations =>
          BadRequest(groupAddTemplate(form, organisationsTupled(organisations), request.identity)))
      },
      data => {
        organisationDAO.get(UUID.fromString(data.organisation_id)).flatMap {
          case Some(organisation) =>
            val group = Group(UUID.randomUUID, data.name, organisation)
            groupDAO.save(group).map(_ => Redirect(routes.Groups.group(group.id.toString)))
          case None => Future.successful(BadRequest(notFoundTemplate(data.organisation_id, Some(request.identity))))
        }
      }
    )
  }

  def all(): Action[AnyContent] = silhouette.SecuredAction.async {
    groupDAO.all.map(groups => Ok(Json.toJson(groups.sortBy(_.name))))
  }

  def get(id: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    try {
      val uuid = UUID.fromString(id)
      groupDAO.get(uuid).map {
        case Some(group) => Ok(Json.toJson(group))
        case None => NotFound(Json.toJson(Messages("group.not_found")))
      }
    } catch {
      case _: IllegalArgumentException => Future.successful(BadRequest(Json.toJson(Messages("uuid.invalid"))))
    }
  }
}
