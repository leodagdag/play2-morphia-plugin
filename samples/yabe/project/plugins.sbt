// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0")

// Use for intellij
resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

// Use for intellij
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")