libraryDependencies ++= Seq(
  "com.lucidchart" % "opentracing-thread-context" % "0.4",
  "io.opentracing" % "opentracing-api" % "0.20.7",
  "io.opentracing.contrib" % "opentracing-globaltracer" % "0.1.0",
  "org.apache.httpcomponents" % "httpclient" % "4.5.2"
)

moduleName := s"opentracing-${name.value}"
