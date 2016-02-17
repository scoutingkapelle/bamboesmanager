package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import forms.GroupForm
import models.daos.{GroupDAO, OrganisationDAO}
import models.{Group, Organisation, User}
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.{Json, Writes}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Groups @Inject()(groupDAO: GroupDAO,
                       organisationDAO: OrganisationDAO,
                       val messagesApi: MessagesApi,
                       val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  implicit val organisationWrites: Writes[Organisation] = Json.writes[Organisation]
  implicit val groupWrites = Json.writes[Group]

  def groups = SecuredAction.async { implicit request =>
    groupDAO.all.map(groups =>
      Ok(views.html.groups(groups, request.identity)))
  }

  def group(id: String) = SecuredAction.async { implicit request =>
    try {
      val uuid = UUID.fromString(id)
      for {
        persons <- groupDAO.persons(uuid)
        group <- groupDAO.get(uuid)
      } yield {
        group match {
          case Some(group) => Ok(views.html.group(group, persons, request.identity))
          case None => NotFound(Messages("group.not_found"))
        }
      }
    } catch {
      case _: IllegalArgumentException => Future(BadRequest("uuid.invalid"))
    }
  }

  def add = SecuredAction.async { implicit request =>
    organisationDAO.all.map(organisations =>
      Ok(views.html.groupAdd(GroupForm.form, organisations.map(org => (org.id.toString, org.name)), request.identity)))
  }

  def save = SecuredAction.async { implicit request =>
    GroupForm.form.bindFromRequest.fold(
      form => {
        for {
          organisations <- organisationDAO.all
        } yield {
          BadRequest(views.html.groupAdd(form, organisations.map(org => (org.id.toString, org.name)), request.identity))
        }
      },
      data => {
        for {
          organisation <- organisationDAO.get(UUID.fromString(data.organisation_id))
          group = Group(UUID.randomUUID, data.name, organisation.get)
          _ <- groupDAO.save(group)
        } yield {
          Ok(views.html.group(group, Nil, request.identity))
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