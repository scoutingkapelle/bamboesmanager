package controllers

import java.util.UUID
import javax.inject.Inject

import models.daos.OrganisationDAO
import models.Organisation
import play.api.Play.current
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Organisations @Inject()(organisationDAO: OrganisationDAO) extends Controller {
  implicit val organisationWrites = Json.writes[Organisation]

  def all = Action.async {
    organisationDAO.all.map(organisations => Ok(Json.toJson(organisations)))
  }

  def get(id: String) = Action.async {
    try {
      val uuid = UUID.fromString(id)
      organisationDAO.get(uuid).map {
        case Some(organisation) => Ok(Json.toJson(organisation))
        case None => NotFound(Json.toJson(Messages("organisation.not_found")))
      }
    } catch {
      case _: IllegalArgumentException => Future(BadRequest(Json.toJson(Messages("uuid.invalid"))))
    }
  }
}