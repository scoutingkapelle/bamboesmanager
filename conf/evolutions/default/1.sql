# --- !Ups
CREATE TABLE "organisations" (
  "id"   UUID PRIMARY KEY,
  "name" VARCHAR
);

CREATE TABLE "groups" (
  "id"              UUID PRIMARY KEY,
  "name"            VARCHAR,
  "organisation_id" UUID REFERENCES "organisations" ("id")
);

CREATE TABLE "persons" (
  "id"       UUID PRIMARY KEY,
  "name"     VARCHAR,
  "email"    VARCHAR,
  "age"      INT,
  "group_id" UUID REFERENCES "groups" ("id")
);

CREATE TABLE "categories" (
  "id"   UUID PRIMARY KEY,
  "name" VARCHAR
);

CREATE TABLE "registrations" (
  "id"          UUID PRIMARY KEY,
  "person_id"   UUID REFERENCES "persons" ("id"),
  "friday"      BOOL,
  "saturday"    BOOL,
  "sorting"     BOOL,
  "category_id" UUID REFERENCES "categories" ("id")
);

CREATE TABLE "users" (
  "id"       UUID PRIMARY KEY,
  "username" VARCHAR,
  "name"     VARCHAR,
  "email"    VARCHAR
);

CREATE TABLE "passwords" (
  "user_id"  UUID PRIMARY KEY REFERENCES "users" (id),
  "password" VARCHAR
);