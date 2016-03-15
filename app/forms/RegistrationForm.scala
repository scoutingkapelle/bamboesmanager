package forms

import play.api.data.Form
import play.api.data.Forms._

object RegistrationForm {

  val form = Form(
    mapping(
      "friday" -> boolean,
      "saturday" -> boolean,
      "sorting" -> boolean,
      "category" -> optional(nonEmptyText),
      "teamLeader" -> boolean,
      "bbq" -> boolean,
      "bbqPartner" -> boolean
    )(Data.apply)(Data.unapply)
  )

  case class Data(friday: Boolean, saturday: Boolean, sorting: Boolean, category: Option[String],
                  teamLeader: Boolean, bbq: Boolean, bbqPartner: Boolean)

}