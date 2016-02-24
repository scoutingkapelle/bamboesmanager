package models

import javax.inject.Inject

import play.api.libs.mailer.{Email, MailerClient}
import views.html.mail.{conformationMail, distributionMail}

class Mail @Inject()(mailer: MailerClient) {

  def sendConformation(registration: Registration) {
    mailer.send(conformation(registration))
  }

  protected def conformation(registration: Registration) =
    Email(
      "Bevestiging van aanmelding voor Actie Bamboes 2016",
      "Actie Bamboes <info@actiebamboes.nl>",
      Seq(registration.person.name + "<" + registration.person.email + ">"),
      bodyHtml = Some(conformationMail(registration).body),
      replyTo = Some("Actie Bamboes <actiebamboes@scoutingkapelle.nl>")
    )

  def sendDistribution(registrations: Seq[Registration]) {
    registrations.map(registration => mailer.send(distribution(registration)))
  }

  protected def distribution(registration: Registration) =
    Email(
      "Indeling voor Actie Bamboes 2016",
      "Actie Bamboes <info@actiebamboes.nl>",
      Seq(registration.person.name + "<" + registration.person.email + ">"),
      bodyHtml = Some(distributionMail(registration).body),
      replyTo = Some("Actie Bamboes <actiebamboes@scoutingkapelle.nl>")
    )
}