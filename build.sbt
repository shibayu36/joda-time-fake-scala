name := "joda-time-fake"

organization := "com.github.shibayu36"

version := "0.0.1"

scalaVersion := "2.12.4"
crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.9.9",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

// Publishing
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
pomExtra := (
  <url>https://github.com/shibayu36/joda-time-fake-scala</url>
  <licenses>
    <license>
      <name>The Apache Software License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/shibayu36/joda-time-fake-scala</url>
    <connection>scm:git:https://github.com/shibayu36/joda-time-fake-scala.git</connection>
  </scm>
  <developers>
    <developer>
      <id>shibayu36</id>
      <name>Yuki Shibazaki</name>
      <url>https://github.com/shibayu36/</url>
    </developer>
  </developers>
)
