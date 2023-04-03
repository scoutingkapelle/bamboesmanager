package forms

import play.api.data.Form
import play.api.data.Forms._

object RegistrationForm {

  val form: Form[Data] = Form(
    mapping(
      "friday" -> boolean,
      "saturday" -> boolean,
      "sorting" -> boolean,
      "category" -> optional(nonEmptyText),
      "secondChoice" -> optional(nonEmptyText),
      "teamLeader" -> boolean
    )(Data.apply)(Data.unapply)
  )

  case class Data(friday: Boolean, saturday: Boolean, sorting: Boolean, category: Option[String], secondChoice: Option[String], teamLeader: Boolean)

}
