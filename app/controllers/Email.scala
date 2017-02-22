
package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import forms.{ListForm, MessageForm}
import models._
import models.daos.{GroupDAO, RegistrationDAO}
import play.api.i18n.{Messages, MessagesApi}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Email @Inject()(mail: Mail,
                      registrationDAO: RegistrationDAO,
                      groupDAO: GroupDAO,
                      val messagesApi: MessagesApi,
                      val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  def confirmation = SecuredAction.async { implicit request =>
    val organisation = Organisation(UUID.randomUUID, Messages("organisation"))
    val group = Group(UUID.randomUUID, Messages("group"), organisation)
    val person = Person(UUID.randomUUID, Messages("name"), Messages("email"), 21, group)
    val category = Category(UUID.randomUUID, Messages("category"))
    val registration = Registration(UUID.randomUUID, person, true, true, true, Some(category), true)
    Future.successful(Ok(views.html.mail.confirmation(registration, request.identity)))
  }

  def distribution = SecuredAction.async { implicit request =>
    val organisation = Organisation(UUID.randomUUID, Messages("organisation"))
    val group = Group(UUID.randomUUID, Messages("group"), organisation)
    val person = Person(UUID.randomUUID, Messages("name"), Messages("email"), 21, group)
    val category = Category(UUID.randomUUID, Messages("category"))
    val registration = Registration(UUID.randomUUID, person, true, true, true, Some(category), true)
    Future.successful(Ok(views.html.mail.distribution(registration, request.identity)))
  }

  def sendDistribution = SecuredAction.async {
    registrationDAO.all.map(registrations => {
      Future.successful(mail.sendDistribution(registrations, Messages("distribution.subject")))
      val flash = ("message", Messages("distribution.success"))
      Redirect(routes.Email.distribution()).flashing(flash)
    })
  }

  def message = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.mail.message(MessageForm.form, request.identity)))
  }

  def sendMessage = SecuredAction.async { implicit request =>
    MessageForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.mail.message(form, request.identity))),
      data => {
        registrationDAO.all.map(registrations => {
          Future.successful(mail.sendMessage(registrations, data.subject, data.message))
          val flash = ("message", Messages("message.success"))
          Redirect(routes.Email.message()).flashing(flash)
        })
      })
  }

  def list = SecuredAction.async { implicit request =>
    val organisation = Organisation(UUID.randomUUID, Messages("organisation"))
    val group = Group(UUID.randomUUID, Messages("group"), organisation)
    val person = Person(UUID.randomUUID, Messages("name"), Messages("email"), 21, group)
    groupDAO.all.map(groups =>
      Ok(views.html.mail.list(ListForm.form, groupsTupled(groups), Seq(person), request.identity)))
  }


  def sendList = SecuredAction.async { implicit request =>
    ListForm.form.bindFromRequest.fold(
      form => {
        val organisation = Organisation(UUID.randomUUID, Messages("organisation"))
        val group = Group(UUID.randomUUID, Messages("group"), organisation)
        val person = Person(UUID.randomUUID, Messages("name"), Messages("email"), 21, group)
        groupDAO.all.map(groups =>
          BadRequest(views.html.mail.list(form, groupsTupled(groups), Seq(person), request.identity)))
      },
      data => {
        try {
          val uuid = UUID.fromString(data.group.split('#')(1))
          groupDAO.persons(uuid).map(persons => {
            mail.sendList(data.email, Messages("list.subject"), persons.sortBy(_.name))
            val flash = ("message", Messages("list.success"))
            Redirect(routes.Email.list()).flashing(flash)
          }
          )
        } catch {
          case _: IllegalArgumentException =>
            Future.successful(BadRequest(views.html.badRequest(Messages("uuid.invalid"), Some(request.identity))))
        }
      }
    )
  }
}

