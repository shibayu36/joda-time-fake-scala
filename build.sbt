name := "joda-time-fake"

organization := "com.github.shibayu36"

scalaVersion := "2.13.3"
crossScalaVersions := Seq("2.11.12", "2.12.4", "2.13.3")

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.10.6",
  "org.scalatest" %% "scalatest" % "3.2.0" % "test"
)

// Compilation
scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xlint"
)

// Release
import ReleaseTransformations._

releaseCrossBuild := true // true if you cross-build the project for multiple Scala versions
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  // For non cross-build projects, use releaseStepCommand("publishSigned")
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
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
