package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import forms.RegisterForm
import models._
import models.daos._
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Registrations @Inject()(registrationDAO: RegistrationDAO,
                              organisationDAO: OrganisationDAO,
                              groupDAO: GroupDAO,
                              categoryDAO: CategoryDAO,
                              personDAO: PersonDAO,
                              val messagesApi: MessagesApi,
                              val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  implicit val organisationWrites: Writes[Organisation] = Json.writes[Organisation]
  implicit val groupWrites: Writes[Group] = Json.writes[Group]
  implicit val personWrites: Writes[Person] = Json.writes[Person]
  implicit val categoryWrites: Writes[Category] = Json.writes[Category]
  implicit val registrationWrites: Writes[Registration] = Json.writes[Registration]

  def registrations = SecuredAction.async { implicit request =>
    registrationDAO.all.map(registrations =>
      Ok(views.html.registrations(registrations, request.identity)))
  }

  def register = UserAwareAction.async { implicit request =>
    for {
      organisations <- organisationDAO.all
      groups <- groupDAO.all
      categories <- categoryDAO.all
    } yield {
      Ok(views.html.register(
        RegisterForm.form,
        models.organisationsTupled(organisations),
        models.groupsTupled(groups),
        models.categoriesTupled(categories),
        request.identity))
    }
  }

  def save = UserAwareAction.async { implicit request =>
    RegisterForm.form.bindFromRequest.fold(
      form => for {
        organisations <- organisationDAO.all
        groups <- groupDAO.all
        categories <- categoryDAO.all
      } yield {
        BadRequest(views.html.register(
          form,
          models.organisationsTupled(organisations),
          models.groupsTupled(groups),
          models.categoriesTupled(categories),
          request.identity))
      },
      data => {
        try {
          val group_id = UUID.fromString(data.group)
          val category_id = UUID.fromString(data.category)
          groupDAO.get(group_id).flatMap {
            case Some(group) => categoryDAO.get(category_id).flatMap {
              case Some(category) => {
                val name = fullName(data.firstName, data.prefix, data.surName)
                val person = Person(UUID.randomUUID, name, data.email, data.age, group)
                personDAO.save(person).flatMap {
                  person => {
                    val registration = Registration(UUID.randomUUID, person,
                      data.friday, data.saturday, data.sorting, category, false)
                    registrationDAO.save(registration).flatMap(_ => Future.successful(Ok("saved registration")))
                  }
                }
              }
              case None =>
                val error = Messages("object.not.found") + ": " + category_id
                Future.successful(BadRequest(views.html.badRequest(error, request.identity)))
            }
            case None =>
              val error = Messages("object.not.found") + ": " + group_id
              Future.successful(BadRequest(views.html.badRequest(error, request.identity)))
          }
        } catch {
          case _: IllegalArgumentException =>
            Future(BadRequest(views.html.badRequest(Messages("uuid.invalid"), request.identity)))
        }
      }
    )
  }

  def all = SecuredAction.async {
    registrationDAO.all.map(registrations => Ok(Json.toJson(registrations)))
  }

  def get(id: String) = SecuredAction.async {
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