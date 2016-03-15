package models

import java.util.UUID

import com.mohiva.play.silhouette.api.Identity

case class Person(id: UUID, name: String, email: String, age: Int, group: Group)

case class Organisation(id: UUID, name: String)

case class Group(id: UUID, name: String, organisation: Organisation)

case class Category(id: UUID, name: String)

case class Registration(id: UUID, person: Person, friday: Boolean, saturday: Boolean, sorting: Boolean,
                        category: Option[Category], teamLeader: Boolean, bbq: Boolean, bbqPartner: Boolean)

case class User(id: UUID, name: String, email: String) extends Identity