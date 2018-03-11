package org.shibayu36.jodatimefake

import org.joda.time.{ DateTime, DateTimeUtils }

object FakeTimer {
  /**
   * Fix DateTime.now by specified timeMillis.
   * Restored after block.
   */
  def fake[T](timeMillis: Long)(block: => T): T = {
    DateTimeUtils.setCurrentMillisFixed(timeMillis)
    try {
      block
    } finally {
      DateTimeUtils.setCurrentMillisSystem()
    }
  }

  /**
   * Fix DateTime.now by specified DateTime object.
   */
  def fake[T](t: DateTime)(block: => T): T =
    fake(t.getMillis)(block)

  /**
   * Fix DateTime.now by specified ISODateTimeFormat string
   * e.g. TimeFaker.fake("2018-03-01T12:34:56") { }
   */
  def fake[T](t: String)(block: => T): T =
    fake(DateTime.parse(t).getMillis)(block)
}
