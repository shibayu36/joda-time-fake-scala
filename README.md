# joda-time-fake [![Build Status](https://travis-ci.org/shibayu36/joda-time-fake-scala.svg?branch=master)](https://travis-ci.org/shibayu36/joda-time-fake-scala)

Provides utilities to fake current time gotten by [joda-time](http://www.joda.org/joda-time/).  This is useful for test.

## Getting started

Add dependency in your `build.sbt` as the following.

```scala
libraryDependencies ++= Seq(
  "com.github.shibayu36" %% "joda-time-fake" % "0.0.2"
)
```

The library is available on [Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.shibayu36/joda-time-fake_2.12).  Currently,
supported Scala versions are 2.11 and 2.12.

## Basic usage

```scala
import com.github.shibayu36.jodatimefake.FakeTimer
import org.joda.time.DateTime

// fake by millis
val result = FakeTimer.fake(1515974400000L) {
    println(DateTime.now.toString) // 2018-01-15T00:00:00.000Z
    "hoge"
}

// current time is restored after block
println(DateTime.now.toString) // current time is printed

// You can get the value returned from block
println(result) // hoge

// You can also pass DateTime instance
FakeTimer.fake(new DateTime(2018, 2, 13, 14, 59)) {

}

// You can also pass ISODateTimeFormat
FakeTimer.fake("2018-03-02T12:34:56+09:00") {

}

// If you use fakeWithTimer, a timer instance is passed to block.
// You can advance time using tick() method.
FakeTimer.fakeWithTimer(1515974400000L) { t =>
    println(DateTime.now.toString) // 2018-01-15T00:00:00.000Z
    t.tick(3000) // Advance time by 3000ms
    println(DateTime.now.toString) // 2018-01-15T00:00:03.000Z
}
```

## Caution

This library doesn't support a parallel test by thread, because org.joda.time.DateTimeUtils uses shared static variables.  Use fork if you want to test in parallel.
