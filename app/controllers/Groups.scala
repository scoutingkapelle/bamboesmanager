package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import forms.GroupForm
import models._
import models.daos.{GroupDAO, OrganisationDAO, RegistrationDAO}
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Groups @Inject()(groupDAO: GroupDAO,
                       organisationDAO: OrganisationDAO,
                       registrationDAO: RegistrationDAO,
                       val messagesApi: MessagesApi,
                       val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  def groups = SecuredAction.async { implicit request =>
    groupDAO.all.map(groups =>
      Ok(views.html.groups(groups.sortBy(group => (group.organisation.name, group.name)), request.identity))
    )
  }

  def group(id: String) = SecuredAction.async { implicit request =>
    try {
      val uuid = UUID.fromString(id)
      for {
        persons <- registrationDAO.group(uuid)
        group <- groupDAO.get(uuid)
      } yield {
        group match {
          case Some(g) => Ok(views.html.group(g, persons.sortBy(_.person.name), request.identity))
          case None => NotFound(views.html.notFound(id, Some(request.identity)))
        }
      }
    } catch {
      case _: IllegalArgumentException =>
        Future(BadRequest(views.html.badRequest(Messages("uuid.invalid"), Some(request.identity))))
    }
  }

  def add = SecuredAction.async { implicit request =>
    organisationDAO.all.map(organisations =>
      Ok(views.html.groupAdd(GroupForm.form, organisationsTupled(organisations), request.identity)))
  }

  def save = SecuredAction.async { implicit request =>
    GroupForm.form.bindFromRequest.fold(
      form => {
        organisationDAO.all.map(organisations =>
          BadRequest(views.html.groupAdd(form, organisationsTupled(organisations), request.identity)))
      },
      data => {
        organisationDAO.get(UUID.fromString(data.organisation_id)).flatMap {
          case Some(organisation) =>
            val group = Group(UUID.randomUUID, data.name, organisation)
            groupDAO.save(group).map(_ => Redirect(routes.Groups.group(group.id.toString)))
          case None => Future(BadRequest(views.html.notFound(data.organisation_id, Some(request.identity))))
        }
      }
    )
  }

  def all = SecuredAction.async {
    groupDAO.all.map(groups => Ok(Json.toJson(groups)))
  }

  def get(id: String) = SecuredAction.async {
    try {
      val uuid = UUID.fromString(id)
      groupDAO.get(uuid).map {
        case Some(group) => Ok(Json.toJson(group))
        case None => NotFound(Json.toJson(Messages("group.not_found")))
      }
    } catch {
      case _: IllegalArgumentException => Future(BadRequest(Json.toJson(Messages("uuid.invalid"))))
    }
  }
}