name := "bamboesmanager"

version := "0.6.0-SNAPSHOT"

lazy val `bamboesmanager` = (project in file(".")).enablePlugins(PlayScala, RpmPlugin)

routesGenerator := InjectedRoutesGenerator

scalaVersion := "2.11.8"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Typesafe Ivy releases" at "https://repo.typesafe.com/typesafe/ivy-releases"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  ehcache, ws, specs2 % Test, evolutions, filters, guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "com.typesafe.play" %% "play-json" % "2.6.0",
  "com.h2database" % "h2" % "1.4.196",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "org.webjars" %% "webjars-play" % "2.6.1",
  "org.webjars" % "bootstrap" % "3.3.5" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap-table" % "1.9.1" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap-switch" % "3.3.2" exclude("org.webjars", "jquery"),
  "org.webjars.bower" % "tableExport.jquery.plugin" % "1.1.4" exclude("org.webjars.bower", "jquery") exclude("org.webjars.bower", "jspdf") exclude("org.webjars.bower", "jspdf-autotable") exclude("org.webjars.bower", "file-saver.js") exclude("org.webjars.bower", "html2canvas"),
  "org.webjars" % "jquery" % "1.9.1",
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3" exclude("org.webjars", "bootstrap") exclude("org.webjars", "jquery"),
  "com.mohiva" %% "play-silhouette" % "5.0.7",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.7",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.7",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.7",
  "com.mohiva" %% "play-silhouette-testkit" % "5.0.7" % "test",
  "net.codingwell" %% "scala-guice" % "4.0.1",
  "com.iheart" %% "ficus" % "1.4.0",
  "com.typesafe.play" %% "play-mailer" % "6.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "6.0.1"
)

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)
