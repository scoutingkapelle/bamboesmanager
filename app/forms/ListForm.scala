package forms

import play.api.data.Form
import play.api.data.Forms._

object ListForm {

  val form = Form(
    mapping(
      "email" -> nonEmptyText,
      "group" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(email: String, group: String)

}