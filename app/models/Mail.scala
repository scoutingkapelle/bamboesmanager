package models

import javax.inject.Inject

import play.api.libs.mailer.{Email, MailerClient}
import play.api.Configuration
import views.html.mail.{confirmationMail, distributionMail, listMail, messageMail}

class Mail @Inject()(mailer: MailerClient, configuration: Configuration) {

  private val from = configuration.underlying.getString("email.from")
  private val replyTo = configuration.underlying.getString("email.replyTo")

  def sendConfirmation(registration: Registration, subject: String): String =
    mailer.send(confirmation(registration, subject))

  private def confirmation(registration: Registration, subject: String): Email =
    email(
      subject = subject,
      to = Seq(toAddress(registration.person.name, registration.person.email)),
      bodyHtml = Some(confirmationMail(registration).body)
    )

  def sendDistribution(registrations: Seq[Registration], subject: String): Seq[String] =
    registrations.map(registration => mailer.send(distribution(registration, subject)))

  private def distribution(registration: Registration, subject: String): Email =
    email(
      subject = subject,
      to = Seq(toAddress(registration.person.name, registration.person.email)),
      bodyHtml = Some(distributionMail(registration).body)
    )

  private def toAddress(name: String, email: String): String = s"$name <$email>"

  def sendMessage(registrations: Seq[Registration], subject: String, content: String): Seq[String] =
    registrations.map(registration => mailer.send(message(registration, subject, content)))

  private def message(registration: Registration, subject: String, content: String): Email =
    email(
      subject = subject,
      to = Seq(toAddress(registration.person.name, registration.person.email)),
      bodyHtml = Some(messageMail(registration.person.name, content).body)
    )

  def sendList(email: String, subject: String, persons: Seq[Person]): String = mailer.send(list(email, subject, persons))

  private def list(emailAddress: String, subject: String, persons: Seq[Person]): Email =
    email(
      subject = subject,
      to = Seq(emailAddress),
      bodyHtml = Some(listMail(persons).body)
    )

  private def email(subject: String, to: Seq[String], bodyHtml: Option[String]): Email =
    Email(
      subject = subject,
      from = from,
      to = to,
      bodyHtml = bodyHtml,
      bcc = Seq(replyTo),
      replyTo = Seq(replyTo)
    )
}
