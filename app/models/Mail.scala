package models

import javax.inject.Inject

import play.api.libs.mailer.{Email, MailerClient}
import play.api.{Configuration, Environment}
import views.html.mail.{confirmationMail, distributionMail, listMail, messageMail}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Mail @Inject()(mailer: MailerClient, configuration: Configuration, env: Environment) {

  private val from = configuration.getString("email.from").getOrElse("info@example.com")
  private val replyTo = configuration.getString("email.replyTo").getOrElse("info@example.com")

  def sendConfirmation(registration: Registration, subject: String) = Future {
    mailer.send(confirmation(registration, subject))
  }

  private def confirmation(registration: Registration, subject: String) =
    email(
      subject = subject,
      to = Seq(toAddress(registration.person.name, registration.person.email)),
      bodyHtml = Some(confirmationMail(registration).body)
    )

  private def toAddress(name: String, email: String) = s"$name <$email>"

  private def email(subject: String, to: Seq[String], bodyHtml: Option[String]) =
    Email(
      subject = subject,
      from = from,
      to = to,
      bodyHtml = bodyHtml,
      bcc = Seq(replyTo),
      replyTo = Some(replyTo)
    )

  def sendDistribution(registrations: Seq[Registration], subject: String) = Future {
    registrations.map(registration => mailer.send(distribution(registration, subject)))
  }

  private def distribution(registration: Registration, subject: String) =
    email(
      subject = subject,
      to = Seq(toAddress(registration.person.name, registration.person.email)),
      bodyHtml = Some(distributionMail(registration).body)
    )

  def sendMessage(registrations: Seq[Registration], subject: String, content: String) = Future {
    registrations.map(registration => mailer.send(message(registration, subject, content)))
  }

  private def message(registration: Registration, subject: String, content: String) =
    email(
      subject = subject,
      to = Seq(toAddress(registration.person.name, registration.person.email)),
      bodyHtml = Some(messageMail(registration.person.name, content).body)
    )

  def sendList(email: String, subject: String, persons: Seq[Person]) = Future {
    mailer.send(list(email, subject, persons))
  }

  private def list(emailAdress: String, subject: String, persons: Seq[Person]) =
    email(
      subject = subject,
      to = Seq(emailAdress),
      bodyHtml = Some(listMail(persons).body)
    )
}