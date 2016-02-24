package models

import javax.inject.Inject

import play.api.libs.mailer.{Email, MailerClient}
import views.html.mail.{conformationMail, distributionMail}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Mail @Inject()(mailer: MailerClient) {

  protected val from = play.Play.application.configuration.getString("email.from")
  protected val replyTo = play.Play.application.configuration.getString("email.replyTo")

  def sendConformation(registration: Registration, subject: String) = Future {
    mailer.send(conformation(registration, subject))
  }

  protected def conformation(registration: Registration, subject: String) =
    Email(
      subject = subject,
      from = from,
      to = Seq(toAddress(registration.person.name, registration.person.email)),
      bodyHtml = Some(conformationMail(registration).body),
      bcc = Seq(replyTo),
      replyTo = Some(replyTo)
    )

  protected def toAddress(name: String, email: String) = name + " <" + email + ">"

  def sendDistribution(registrations: Seq[Registration], subject: String) = Future {
    registrations.map(registration => mailer.send(distribution(registration, subject)))
  }

  protected def distribution(registration: Registration, subject: String) =
    Email(
      subject = subject,
      from = from,
      to = Seq(toAddress(registration.person.name, registration.person.email)),
      bodyHtml = Some(distributionMail(registration).body),
      bcc = Seq(replyTo),
      replyTo = Some(replyTo)
    )

}