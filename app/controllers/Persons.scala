package controllers

import java.util.UUID
import javax.inject.Inject

import models.daos._
import models.{Group, Organisation, Person}
import play.api.Play.current
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Persons @Inject()(personDAO: PersonDAO) extends Controller {
  implicit val organisationWrites: Writes[Organisation] = Json.writes[Organisation]
  implicit val groupWrites: Writes[Group] = Json.writes[Group]
  implicit val personWrites: Writes[Person] = Json.writes[Person]

  def all = Action.async {
    personDAO.all.map(persons => Ok(Json.toJson(persons)))
  }

  def get(id: String) = Action.async {
    try {
      personDAO.get(UUID.fromString(id)).map {
        case Some(person) => Ok(Json.toJson(person))
        case None => NotFound(Messages("person.not_found"))
      }
    } catch {
      case _: IllegalArgumentException => Future(BadRequest(Json.toJson(Messages("uuid.invalid"))))
    }
  }
}