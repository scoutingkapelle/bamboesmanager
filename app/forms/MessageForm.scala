package forms

import play.api.data.Form
import play.api.data.Forms._

object MessageForm {

  val form: Form[Data] = Form(
    mapping(
      "subject" -> nonEmptyText,
      "message" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(subject: String, message: String)

}
