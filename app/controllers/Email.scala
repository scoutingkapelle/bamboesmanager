
package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import models.daos.RegistrationDAO
import models.{Mail, User}
import play.api.i18n.{Messages, MessagesApi}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Email @Inject()(mail: Mail,
                      registrationDAO: RegistrationDAO,
                      val messagesApi: MessagesApi,
                      val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  def email = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.email(request.identity)))
  }

  def sendDistribution = SecuredAction.async { implicit request =>
    registrationDAO.all.map(registrations => {
      Future.successful(mail.sendDistribution(registrations, Messages("division.subject")))
      val flash = ("message", Messages("distribution.succes"))
      Redirect(routes.Email.email).flashing(flash)
    })
  }
}

