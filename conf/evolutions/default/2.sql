# --- !Ups
ALTER TABLE "categories"
    ADD "second_choice" boolean;

ALTER TABLE "registrations"
    ADD "second_choice_id" UUID REFERENCES "categories" ("id");
