package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import forms.{RegisterForm, RegistrationForm}
import models._
import models.daos._
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Registrations @Inject()(mail: Mail,
                              registrationDAO: RegistrationDAO,
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
      Ok(views.html.registrations(registrations.sortBy(_.person.name), request.identity)))
  }

  def registration(id: String) = SecuredAction.async { implicit request =>
    try {
      val uuid = UUID.fromString(id)
      for {
        categories <- categoryDAO.all
        registration <- registrationDAO.get(uuid)
      } yield {
        registration match {
          case Some(registration) => Ok(views.html.registration(
            registration,
            RegistrationForm.form,
            categoriesTupled(categories),
            request.identity))
          case None => NotFound(views.html.notFound(id, Some(request.identity)))
        }
      }
    } catch {
      case _: IllegalArgumentException =>
        Future(BadRequest(views.html.badRequest(Messages("uuid.invalid"), Some(request.identity))))
    }
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
          val group_id = UUID.fromString(data.group.split('#')(1))
          val category_id = UUID.fromString(data.category)
          groupDAO.get(group_id).flatMap {
            case Some(group) => categoryDAO.get(category_id).flatMap {
              case Some(category) => {
                val name = fullName(data.firstName, data.prefix, data.surName)
                val person = Person(UUID.randomUUID, name, data.email.toLowerCase, data.age, group)
                personDAO.save(person).flatMap {
                  person => {
                    val registration = Registration(UUID.randomUUID, person,
                      data.friday, data.saturday, data.sorting, category, false)
                    registrationDAO.save(registration).flatMap(registration => {
                      Future.successful(mail.sendConfirmation(registration, Messages("confirmation.subject")))
                      val flash = ("message", Messages("registered"))
                      Future.successful(Redirect(routes.Application.index).flashing(flash))
                    })
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

  def update(id: String) = SecuredAction.async { implicit request =>
    try {
      val uuid = UUID.fromString(id)
      RegistrationForm.form.bindFromRequest.fold(
        form => {
          for {
            registration <- registrationDAO.get(uuid)
            categories <- categoryDAO.all
          } yield {
            registration match {
              case Some(registration) =>
                BadRequest(views.html.registration(
                  registration,
                  form,
                  models.categoriesTupled(categories),
                  request.identity))
              case None =>
                val error = Messages("object.not.found") + ": " + id
                BadRequest(views.html.badRequest(error, Some(request.identity)))
            }
          }
        },
        data => {
          val category_id = UUID.fromString(data.category)
          registrationDAO.get(uuid).flatMap {
            case Some(registration) => categoryDAO.get(category_id).flatMap {
              case Some(category) => {
                val updatedRegistration = registration.copy(
                  friday = data.friday,
                  saturday = data.saturday,
                  sorting = data.sorting,
                  category = category,
                  teamLeader = data.teamLeader)
                registrationDAO.save(updatedRegistration).flatMap(registration => {
                  Future.successful(Redirect(routes.Registrations.registrations))
                })
              }
              case None =>
                val error = Messages("object.not.found") + ": " + category_id
                Future.successful(BadRequest(views.html.badRequest(error, Some(request.identity))))
            }
            case None =>
              val error = Messages("object.not.found") + ": " + id
              Future.successful(BadRequest(views.html.badRequest(error, Some(request.identity))))
          }
        }
      )
    } catch {
      case _: IllegalArgumentException =>
        Future(BadRequest(views.html.badRequest(Messages("uuid.invalid"), Some(request.identity))))
    }
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