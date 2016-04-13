package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import models.daos._
import models.{Group, Organisation, Person, User}
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Persons @Inject()(personDAO: PersonDAO,
                        val messagesApi: MessagesApi,
                        val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  implicit val organisationWrites: Writes[Organisation] = Json.writes[Organisation]
  implicit val groupWrites: Writes[Group] = Json.writes[Group]
  implicit val personWrites: Writes[Person] = Json.writes[Person]

  def all = SecuredAction.async {
    personDAO.all.map(persons => Ok(Json.toJson(persons)))
  }

  def get(id: String) = SecuredAction.async {
    try {
      personDAO.get(UUID.fromString(id)).map {
        case Some(person) => Ok(Json.toJson(person))
        case None => NotFound(Json.toJson(Messages("person.not_found")))
      }
    } catch {
      case _: IllegalArgumentException => Future(BadRequest(Json.toJson(Messages("uuid.invalid"))))
    }
  }
}