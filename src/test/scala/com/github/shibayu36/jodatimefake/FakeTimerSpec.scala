package com.github.shibayu36.jodatimefake

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent._

class FakeTimerSpec extends AnyFunSpec with Matchers {
  val tz = DateTimeZone.forID("Asia/Tokyo")

  describe("FakeTimer.fake") {
    it("Fix time only in the block") {
      FakeTimer.fake(1515974400000L) {
        DateTime.now(tz).toString shouldBe "2018-01-15T09:00:00.000+09:00"
      }

      // Restore after block
      DateTime.now(tz).toString shouldNot be("2018-01-15T09:00:00.000+09:00")
    }

    it("Returns value from block") {
      val num = FakeTimer.fake(1515974400000L) {
        123
      }
      num shouldBe 123

      val str = FakeTimer.fake(1515974400000L) {
        "hoge"
      }
      str shouldBe "hoge"
    }

    it("DateTime object can be passed") {
      val dt = new DateTime(2018, 2, 13, 14, 59, tz)
      FakeTimer.fake(dt) {
        DateTime.now(tz).toString shouldBe "2018-02-13T14:59:00.000+09:00"
      }
    }


    it("ISODateTimeFormat string can be passed") {
      FakeTimer.fake("2018-03-02T12:34:56+09:00") {
        DateTime.now(tz).toString shouldBe "2018-03-02T12:34:56.000+09:00"
      }
    }
  }

  describe("FakeTimer.fakeWithTimer") {
    it("Fake time by millis with timer") {
      val result = FakeTimer.fakeWithTimer(1515974400000L) { t =>
        DateTime.now(tz).toString shouldBe "2018-01-15T09:00:00.000+09:00"
        t.tick(100)
        DateTime.now(tz).toString shouldBe "2018-01-15T09:00:00.100+09:00"
        t.tick(1000)
        DateTime.now(tz).toString shouldBe "2018-01-15T09:00:01.100+09:00"
        t.tick(3600 * 1000)
        DateTime.now(tz).toString shouldBe "2018-01-15T10:00:01.100+09:00"

        DateTime.now(tz).toString
      }

      // Last value of block is returned
      result shouldBe "2018-01-15T10:00:01.100+09:00"

      // Restore after block
      DateTime.now(tz).toString shouldNot be(result)
    }

    it("Fake time by DateTime with timer") {
      val dt = new DateTime(2018, 2, 13, 14, 59, tz)
      FakeTimer.fakeWithTimer(dt) { t =>
        DateTime.now(tz).toString shouldBe "2018-02-13T14:59:00.000+09:00"
        t.tick(2000)
        DateTime.now(tz).toString shouldBe "2018-02-13T14:59:02.000+09:00"
      }
    }

    it("Fake time by ISODateTimeFormat string with timer") {
      FakeTimer.fakeWithTimer("2018-03-02T12:34:56+09:00") { t =>
        DateTime.now(tz).toString shouldBe "2018-03-02T12:34:56.000+09:00"
        t.tick(60 * 1000)
        DateTime.now(tz).toString shouldBe "2018-03-02T12:35:56.000+09:00"
      }
    }
  }

  describe("Parallel execution") {
    import java.util.concurrent.Executors
    import scala.concurrent.ExecutionContext

    import Implicits._

    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(3)).withFakeTimer

    describe("FakeTimer.fake") {
      it("Can run in multi-threaded") {
        def runThread(msec: Int) = Future {
          FakeTimer.fake(1515974400000L) {
            Thread.sleep(msec)
            DateTime.now(tz).toString shouldBe "2018-01-15T09:00:00.000+09:00"
          }
        }

        Await.result(runThread(100) zip runThread(200), duration.Duration.Inf)
      }

      it("Can run in nested-threaded") {
        def runThread(msec: Int) = Future {
          FakeTimer.fake(1515974500000L) {
            Thread.sleep(msec)
            DateTime.now(tz).toString shouldBe "2018-01-15T09:01:40.000+09:00"
            val f = Future {
              DateTime.now(tz).toString shouldBe "2018-01-15T09:01:40.000+09:00"
            }
            Await.result(f, duration.Duration.Inf)
            DateTime.now(tz).toString shouldBe "2018-01-15T09:01:40.000+09:00"
          }
        }

        Await.result(runThread(100) zip runThread(200), duration.Duration.Inf)
        Await.result(runThread(100) zip runThread(200), duration.Duration.Inf)
      }
    }

    describe("FakeTimer.fakeWithTimer") {
      it("Can run in multi-threaded") {
        def runThread(msec: Int) = Future {
          Thread.sleep(msec)
          FakeTimer.fakeWithTimer(1515974400000L) { t =>
            DateTime.now(tz).toString shouldBe "2018-01-15T09:00:00.000+09:00"
            t.tick(2000)
            DateTime.now(tz).toString shouldBe "2018-01-15T09:00:02.000+09:00"
          }
        }

        Await.result(runThread(100) zip runThread(200), duration.Duration.Inf)
      }

      it("Can run in nested-threaded") {
        def runThread(msec: Int) = Future {
          Thread.sleep(msec)
          FakeTimer.fakeWithTimer(1515974500000L) { t =>
            DateTime.now(tz).toString shouldBe "2018-01-15T09:01:40.000+09:00"
            t.tick(2000)
            DateTime.now(tz).toString shouldBe "2018-01-15T09:01:42.000+09:00"
            val f = Future {
              DateTime.now(tz).toString shouldBe "2018-01-15T09:01:42.000+09:00"
            }
            Await.result(f, duration.Duration.Inf)
            DateTime.now(tz).toString shouldBe "2018-01-15T09:01:42.000+09:00"
          }
        }

        Await.result(runThread(100) zip runThread(200), duration.Duration.Inf)
        Await.result(runThread(100) zip runThread(200), duration.Duration.Inf)
      }
    }
  }
}
