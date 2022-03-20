package forms

import play.api.data.Form
import play.api.data.Forms._

object SignInForm {

  val form: Form[Data] = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(email: String, password: String)

}
