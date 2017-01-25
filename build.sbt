lazy val httpcore = project

lazy val httpclient = project.dependsOn(httpcore)

lazy val httpasyncclient = project.dependsOn(httpclient)

inScope(Global)(Seq(
  autoScalaLibrary := false,
  crossPaths := false
))
