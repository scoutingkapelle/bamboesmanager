package forms

import play.api.data.Form
import play.api.data.Forms._

object RegisterForm {

  val form = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "prefix" -> text,
      "surName" -> nonEmptyText,
      "age" -> number(min = 11),
      "email" -> email,
      "group" -> nonEmptyText,
      "friday" -> boolean,
      "saturday" -> boolean,
      "sorting" -> boolean,
      "category" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(
                   firstName: String,
                   prefix: String,
                   surName: String,
                   age: Int,
                   email: String,
                   group: String,
                   friday: Boolean,
                   saturday: Boolean,
                   sorting: Boolean,
                   category: String
                 )

}