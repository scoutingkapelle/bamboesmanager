package models

import java.util.UUID

case class Person(id: UUID, name: String, email: String, age: Int, group: Group)

case class User(id: UUID, username: String, name: String, email: String)

case class Organisation(id: UUID, name: String)

case class Group(id: UUID, name: String, organisation: Organisation)

case class Category(id: UUID, name: String)

case class Registration(id: UUID, person: Person, friday: Boolean, saturday: Boolean, sorting: Boolean, category: Category)