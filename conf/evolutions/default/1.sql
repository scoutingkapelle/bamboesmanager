# --- !Ups
CREATE TABLE "organisations" (
  "id"   UUID PRIMARY KEY,
  "name" VARCHAR NOT NULL
);

CREATE TABLE "groups" (
  "id"              UUID PRIMARY KEY,
  "name"            VARCHAR NOT NULL,
  "organisation_id" UUID REFERENCES "organisations" ("id")
);

CREATE TABLE "persons" (
  "id"       UUID PRIMARY KEY,
  "name"     VARCHAR NOT NULL,
  "email"    VARCHAR NOT NULL,
  "age"      INT     NOT NULL,
  "group_id" UUID REFERENCES "groups" ("id")
);

CREATE TABLE "categories" (
  "id"   UUID PRIMARY KEY,
  "name" VARCHAR NOT NULL
);

CREATE TABLE "registrations" (
  "id"          UUID PRIMARY KEY,
  "person_id"   UUID REFERENCES "persons" ("id"),
  "friday"      BOOL NOT NULL,
  "saturday"    BOOL NOT NULL,
  "sorting"     BOOL NOT NULL,
  "category_id" UUID REFERENCES "categories" ("id"),
  "team_leader" BOOL,
  "bbq"         BOOL,
  "bbq_partner" BOOL
);

CREATE TABLE "users" (
  "id"    UUID PRIMARY KEY,
  "name"  VARCHAR NOT NULL,
  "email" VARCHAR NOT NULL UNIQUE
);

CREATE TABLE "passwords" (
  "hash"     VARCHAR NOT NULL,
  "password" VARCHAR NOT NULL,
  "salt"     VARCHAR,
  "email"    VARCHAR PRIMARY KEY REFERENCES "users" (email)
);