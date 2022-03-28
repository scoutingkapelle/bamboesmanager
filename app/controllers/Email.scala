
package controllers

import com.mohiva.play.silhouette.api.Silhouette
import forms.{ListForm, MessageForm}
import models._
import models.daos.{GroupDAO, RegistrationDAO}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import utils.DefaultEnv

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class Email @Inject()(mail: Mail,
                      registrationDAO: RegistrationDAO,
                      groupDAO: GroupDAO,
                      components: ControllerComponents,
                      silhouette: Silhouette[DefaultEnv],
                      confirmationTemplate: views.html.mail.confirmation,
                      distributionTemplate: views.html.mail.distribution,
                      listTemplate: views.html.mail.list,
                      messageTemplate: views.html.mail.message,
                      badRequestTemplate: views.html.badRequest)
                     (implicit ec: ExecutionContext)
  extends AbstractController(components) with I18nSupport {

  def confirmation(): Action[AnyContent] = silhouette.SecuredAction { implicit request =>
    val organisation = Organisation(UUID.randomUUID, Messages("organisation"))
    val group = Group(UUID.randomUUID, Messages("group"), organisation)
    val person = Person(UUID.randomUUID, Messages("name"), Messages("email"), 21, group)
    val category = Category(UUID.randomUUID, Messages("category"))
    val registration = Registration(
      id = UUID.randomUUID,
      person = person,
      friday = true,
      saturday = true,
      sorting = true,
      category = Some(category),
      teamLeader = true
    )
    Ok(confirmationTemplate(registration, request.identity))
  }

  def distribution(): Action[AnyContent] = silhouette.SecuredAction { implicit request =>
    val organisation = Organisation(UUID.randomUUID, Messages("organisation"))
    val group = Group(UUID.randomUUID, Messages("group"), organisation)
    val person = Person(UUID.randomUUID, Messages("name"), Messages("email"), 21, group)
    val category = Category(UUID.randomUUID, Messages("category"))
    val registration = Registration(
      id = UUID.randomUUID,
      person = person,
      friday = true,
      saturday = true,
      sorting = true,
      category = Some(category),
      teamLeader = true
    )
    Ok(distributionTemplate(registration, request.identity))
  }

  def sendDistribution(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    registrationDAO.all.map(registrations => {
      mail.sendDistribution(registrations, Messages("distribution.subject"))
      val flash = ("message", Messages("distribution.success"))
      Redirect(routes.Email.distribution()).flashing(flash)
    })
  }

  def message(): Action[AnyContent] = silhouette.SecuredAction { implicit request =>
    Ok(messageTemplate(MessageForm.form, request.identity))
  }

  def sendMessage(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    MessageForm.form.bindFromRequest().fold(
      form => Future.successful(BadRequest(messageTemplate(form, request.identity))),
      data => {
        registrationDAO.all.map(registrations => {
          mail.sendMessage(registrations, data.subject, data.message)
          val flash = ("message", Messages("message.success"))
          Redirect(routes.Email.message()).flashing(flash)
        })
      })
  }

  def list(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val organisation = Organisation(UUID.randomUUID, Messages("organisation"))
    val group = Group(UUID.randomUUID, Messages("group"), organisation)
    val person = Person(UUID.randomUUID, Messages("name"), Messages("email"), 21, group)

    groupDAO.all.map(groups =>
      Ok(listTemplate(ListForm.form, groupsTupled(groups), Seq(person), request.identity)))
  }

  def sendList(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val organisation = Organisation(UUID.randomUUID, Messages("organisation"))
    val group = Group(UUID.randomUUID, Messages("group"), organisation)
    val person = Person(UUID.randomUUID, Messages("name"), Messages("email"), 21, group)

    ListForm.form.bindFromRequest().fold(
      form => groupDAO.all.map { groups =>
        BadRequest(listTemplate(form, groupsTupled(groups), Seq(person), request.identity))
      },
      data => {
        try {
          val uuid = UUID.fromString(data.group.split('#')(1))
          groupDAO.members(uuid).map { persons =>
            mail.sendList(data.email, Messages("list.subject"), persons.sortBy(_.name))
            val flash = ("message", Messages("list.success"))
            Redirect(routes.Email.list()).flashing(flash)
          }
        } catch {
          case _: IllegalArgumentException =>
            Future.successful(BadRequest(badRequestTemplate(Messages("uuid.invalid"), Some(request.identity))))
        }
      }
    )
  }
}
