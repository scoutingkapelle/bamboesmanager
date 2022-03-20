package forms

import play.api.data.Form
import play.api.data.Forms._

object GroupForm {

  val form: Form[Data] = Form(
    mapping(
      "name" -> nonEmptyText,
      "organisation_id" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(name: String, organisation_id: String)

}
