libraryDependencies ++= Seq(
  "com.lucidchart" % "opentracing-thread-context" % "0.5",
  "io.opentracing" % "opentracing-api" % "0.31.0",
  "io.opentracing" % "opentracing-util" % "0.31.0",
  "org.apache.httpcomponents" % "httpclient" % "4.5.2"
)

moduleName := s"opentracing-${name.value}"
