
package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import models.daos.RegistrationDAO
import models.{Mail, User}
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits._

class Mailer @Inject()(mail: Mail,
                       registrationDAO: RegistrationDAO,
                       val messagesApi: MessagesApi,
                       val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  def sendDistribution = SecuredAction.async { implicit request =>
    registrationDAO.all.map(registrations => {
      mail.sendDistribution(registrations)
      Ok("send distribution emails")
    })
  }
}

