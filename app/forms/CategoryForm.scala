package forms

import play.api.data.Form
import play.api.data.Forms._

object CategoryForm {

  val form: Form[Data] = Form(
    mapping(
      "name" -> nonEmptyText,
      "secondChoice" -> boolean
    )(Data.apply)(Data.unapply)
  )

  case class Data(name: String, secondChoice: Boolean)

}
