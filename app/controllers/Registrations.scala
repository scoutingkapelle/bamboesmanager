package controllers

import java.util.UUID
import javax.inject.Inject

import models.daos._
import models._
import play.api.Play.current
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Registrations @Inject()(registrationDAO: RegistrationDAO) extends Controller {
  implicit val organisationWrites: Writes[Organisation] = Json.writes[Organisation]
  implicit val groupWrites: Writes[Group] = Json.writes[Group]
  implicit val personWrites: Writes[Person] = Json.writes[Person]
  implicit val categoryWrites: Writes[Category] = Json.writes[Category]
  implicit val registrationWrites: Writes[Registration] = Json.writes[Registration]

  def all = Action.async {
    registrationDAO.all.map(registrations => Ok(Json.toJson(registrations)))
  }

  def get(id: String) = Action.async {
    try {
      registrationDAO.get(UUID.fromString(id)).map {
        case Some(registration) => Ok(Json.toJson(registration))
        case None => NotFound(Messages("registration.not_found"))
      }
    } catch {
      case _: IllegalArgumentException => Future(BadRequest(Json.toJson(Messages("uuid.invalid"))))
    }
  }
}