libraryDependencies ++= Seq(
  "io.opentracing" % "opentracing-api" % "0.31.0",
  "org.apache.httpcomponents" % "httpcore" % "4.4.5"
)

moduleName := s"opentracing-${name.value}"
