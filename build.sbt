name := "bamboesmanager"

version := "0.6.0-SNAPSHOT"

lazy val `bamboesmanager` = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

scalaVersion := "2.13.12"

resolvers += Resolver.jcenterRepo

libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

libraryDependencies ++= Seq(
  ehcache, ws, specs2 % Test, evolutions, filters, guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test,
  "org.playframework" %% "play-slick" % "6.0.0",
  "org.playframework" %% "play-slick-evolutions" % "6.0.0",
  "org.playframework" %% "play-json" % "3.0.1",
  "org.playframework" %% "play-mailer" % "10.0.0",
  "org.playframework" %% "play-mailer-guice" % "10.0.0",
  "com.h2database" % "h2" % "2.2.224",
  "org.postgresql" % "postgresql" % "42.6.0",
  "org.webjars" %% "webjars-play" % "3.0.0",
  "org.webjars" % "bootstrap" % "3.4.1" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap-table" % "1.16.0" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap-switch" % "3.3.4" exclude("org.webjars", "jquery"),
  "org.webjars.bower" % "tableExport.jquery.plugin" % "1.9.3" exclude("org.webjars.bower", "jquery") exclude("org.webjars.bower", "jspdf") exclude("org.webjars.bower", "jspdf-autotable") exclude("org.webjars.bower", "file-saver.js") exclude("org.webjars.bower", "html2canvas"),
  "org.webjars" % "jquery" % "1.12.4",
  "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B3" exclude("org.webjars", "bootstrap") exclude("org.webjars", "jquery"),
  "io.github.honeycomb-cheesecake" %% "play-silhouette" % "8.0.2",
  "io.github.honeycomb-cheesecake" %% "play-silhouette-persistence" % "8.0.2",
  "io.github.honeycomb-cheesecake" %% "play-silhouette-crypto-jca" % "8.0.2",
  "io.github.honeycomb-cheesecake" %% "play-silhouette-password-bcrypt" % "8.0.2",
  "io.github.honeycomb-cheesecake" %% "play-silhouette-testkit" % "8.0.2" % "test",
  "net.codingwell" %% "scala-guice" % "7.0.0",
  "com.iheart" %% "ficus" % "1.5.2",
)

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)

Global / onChangedBuildSource := ReloadOnSourceChanges
