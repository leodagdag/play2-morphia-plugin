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
      publishTo := Some(githubRepository),
      scalacOptions ++= Seq("-Xlint","-deprecation", "-unchecked","-encoding", "utf8"),
      javacOptions ++= Seq("-encoding", "utf8", "-g"),
      resolvers ++= Seq(DefaultMavenRepository, Resolvers.typesafeRepository, Resolvers.morphiaRepository),
      checksums := Nil // To prevent proxyToys downloding fails https://github.com/leodagdag/play2-morphia-plugin/issues/11
    )
  ).settings()

  object Resolvers {
    val githubRepository =  Resolver.file("GitHub Repository", Path.userHome / "dev" / "leodagdag.github.com" / "repository" asFile)(Resolver.ivyStylePatterns)
    val dropboxRepository =  Resolver.file("Dropbox Repository", Path.userHome / "Dropbox" / "Public" / "repository" asFile)(Resolver.ivyStylePatterns)
    val typesafeRepository = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
    val morphiaRepository = "Morphia Repository" at "http://morphia.googlecode.com/svn/mavenrepo/"
  }

  object Dependencies {
      val runtime = Seq(
        "com.google.code.morphia"    % "morphia"               % "1.00-SNAPSHOT",
        "com.google.code.morphia"    % "morphia-logging-slf4j" % "0.99",
        "com.google.code.morphia"    % "morphia-validation"    % "0.99",
        "cglib"                      % "cglib-nodep"           % "[2.1_3,)",
        "com.thoughtworks.proxytoys" % "proxytoys"             % "1.0",
        "play"                       %% "play"                 % "2.0.4" % "compile" notTransitive(),
        ("org.springframework"       % "spring-core"           % "3.0.7.RELEASE" % "compile" notTransitive())
          .exclude("org.springframework", "spring-asm")
          .exclude("commons-logging", "commons-logging"),
        ("org.springframework"       % "spring-beans"          % "3.0.7.RELEASE" % "compile" notTransitive())
          .exclude("org.springframework", "spring-core")
      )
      val test = Seq(
        "play" %% "play-test" % "2.0.4" % "test"
      )
  }

  object BuildSettings {
    val buildOrganization = "leodagdag"
    val buildVersion      = "0.0.10"
    val buildScalaVersion = "2.9.1"
    val buildSbtVersion   = "0.11.3"
    val buildSettings = Defaults.defaultSettings ++ Seq (
      organization   := buildOrganization,
      version        := buildVersion,
      scalaVersion   := buildScalaVersion
    )
  }
}
