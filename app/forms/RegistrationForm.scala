package forms

import play.api.data.Form
import play.api.data.Forms._

object RegistrationForm {

  val form = Form(
    mapping(
      "friday" -> boolean,
      "saturday" -> boolean,
      "sorting" -> boolean,
      "category" -> nonEmptyText,
      "teamLeader" -> boolean
    )(Data.apply)(Data.unapply)
  )

  case class Data(friday: Boolean, saturday: Boolean, sorting: Boolean, category: String, teamLeader: Boolean)

}