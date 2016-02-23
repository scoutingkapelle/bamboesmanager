package models

import javax.inject.Inject

import play.api.libs.mailer.{Email, MailerClient}
import views.html.mail.conformationMail

class Mail @Inject()(mailer: MailerClient) {

  def sendConformation(registration: Registration): String = {
    val email = Email(
      "Bevestiging van aanmelding voor Actie Bamboes 2016",
      "Actie Bamboes FROM <actiebamboes@scoutingkapelle.nl>",
      Seq(registration.person.name + " TO <" + registration.person.email + ">"),
      bodyHtml = Some(conformationMail(registration).body)
    )
    mailer.send(email)
  }
}