import sbt._
import Keys._

object Play2MorphiaPluginBuild extends Build {

  import Resolvers._
  import Dependencies._
  import BuildSettings._

  lazy val Play2MorphiaPlugin = Project(
    "play2-morphia-plugin",
    file("."),
    settings = buildSettings ++ Seq(
      libraryDependencies := runtime ++ test,
      publishMavenStyle := true,
      publishTo := Some(dropboxRepository),
      scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked", "-encoding", "utf8"),
      javacOptions ++= Seq("-source", "1.6", "-encoding", "utf8"),
      resolvers ++= Seq(DefaultMavenRepository, Resolvers.typesafeRepository), //, Resolvers.morphiaRepository),
      checksums := Nil // To prevent proxyToys downloding fails https://github.com/leodagdag/play2-morphia-plugin/issues/11
    )
  ).settings()

  object Resolvers {
    val githubRepository = Resolver.file("GitHub Repository", Path.userHome / "dev" / "leodagdag.github.com" / "repository" asFile)(Resolver.ivyStylePatterns)
    val dropboxRepository = Resolver.file("Dropbox Repository", Path.userHome / "Dropbox" / "Public" / "repository" asFile)(Resolver.ivyStylePatterns)
    val typesafeRepository = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
    //val morphiaRepository = "Morphia Repository" at "http://morphia.googlecode.com/svn/mavenrepo/"
  }

  object Dependencies {
    val runtime = Seq(
      "com.github.jmkgreen.morphia" % "morphia" % "1.2.2",
      ("com.github.jmkgreen.morphia" % "morphia-logging-slf4j" % "1.2.2" % "compile" notTransitive())
        .exclude("org.slf4j", "slf4j-simple")
        .exclude("org.slf4j", "slf4j-jdk14"),
      ("com.github.jmkgreen.morphia" % "morphia-validation" % "1.2.2" % "compile" notTransitive())
        .exclude("org.slf4j", "slf4j-simple")
        .exclude("org.slf4j", "slf4j-jdk14"),

      "play" %% "play-java" % "2.1.3" % "compile"


      /*,
      ("org.springframework"       % "spring-core"           % "3.0.7.RELEASE" % "compile" notTransitive())
        .exclude("org.springframework", "spring-asm")
        .exclude("commons-logging", "commons-logging"),
      ("org.springframework"       % "spring-beans"          % "3.0.7.RELEASE" % "compile" notTransitive())
        .exclude("org.springframework", "spring-core")*/
    )
    val test = Seq(
      "play" %% "play-test" % "2.1.3" % "test"
    )
  }

  object BuildSettings {
    val buildOrganization = "leodagdag"
    val buildVersion = "0.0.15"
    val buildScalaVersion = "2.10.0"
    val buildSbtVersion = "0.12.2"
    val buildSettings = Defaults.defaultSettings ++ Seq(
      organization := buildOrganization,
      version := buildVersion,
      scalaVersion := buildScalaVersion
    )
  }

}
