package forms

import play.api.data.Form
import play.api.data.Forms._

object RegisterForm {

  val form = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "prefix" -> text,
      "surName" -> nonEmptyText,
      "age" -> number(min = 11, max = 100),
      "email" -> email,
      "group" -> nonEmptyText,
      "friday" -> boolean,
      "saturday" -> boolean,
      "sorting" -> boolean,
      "category" -> optional(nonEmptyText),
      "bbq" -> boolean,
      "bbqPartner" -> boolean
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
                   category: Option[String],
                   bbq: Boolean,
                   bbqPartner: Boolean
                 )

}