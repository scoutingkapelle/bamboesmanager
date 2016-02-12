package controllers

import java.util.UUID
import javax.inject.Inject

import dao.GroupDAO
import models.{Group, Organisation}
import play.api.Play.current
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Groups @Inject()(groupDAO: GroupDAO) extends Controller {
  implicit val organisationWrites: Writes[Organisation] = Json.writes[Organisation]
  implicit val groupWrites = Json.writes[Group]

  def all = Action.async {
    groupDAO.all.map(groups => Ok(Json.toJson(groups)))
  }

  def get(id: String) = Action.async {
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