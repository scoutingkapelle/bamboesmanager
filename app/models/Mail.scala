package models

import javax.inject.Inject

import play.api.libs.mailer.{Email, MailerClient}
import views.html.mail.{confirmationMail, distributionMail, listMail, messageMail}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Mail @Inject()(mailer: MailerClient) {

  protected val from = play.Play.application.configuration.getString("email.from")
  protected val replyTo = play.Play.application.configuration.getString("email.replyTo")

  def sendConfirmation(registration: Registration, subject: String) = Future {
    mailer.send(confirmation(registration, subject))
  }

  protected def confirmation(registration: Registration, subject: String) =
    Email(
      subject = subject,
      from = from,
      to = Seq(toAddress(registration.person.name, registration.person.email)),
      bodyHtml = Some(confirmationMail(registration).body),
      bcc = Seq(replyTo),
      replyTo = Some(replyTo)
    )

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

  def sendMessage(registrations: Seq[Registration], subject: String, content: String) = Future {
    registrations.map(registration => mailer.send(message(registration, subject, content)))
  }

  protected def message(registration: Registration, subject: String, content: String) =
    Email(
      subject = subject,
      from = from,
      to = Seq(toAddress(registration.person.name, registration.person.email)),
      bodyHtml = Some(messageMail(registration.person.name, content).body),
      bcc = Seq(replyTo),
      replyTo = Some(replyTo)
    )

  protected def toAddress(name: String, email: String) = name + " <" + email + ">"

  def sendList(email: String, subject: String, persons: Seq[Person]) = Future {
    mailer.send(list(email, subject, persons))
  }

  def list(email: String, subject: String, persons: Seq[Person]) =
    Email(
      subject = subject,
      from = from,
      to = Seq(email),
      bodyHtml = Some(listMail(persons).body),
      bcc = Seq(replyTo),
      replyTo = Some(replyTo)
    )
}