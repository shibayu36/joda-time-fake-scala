package org.shibayu36.jodatimefake

import org.joda.time.{DateTime, DateTimeUtils}

object FakeTimer {
  /** Fake DateTime.now by specified timeMillis.
   *
   * Time will be restored after block.
   */
  def fake[T](timeMillis: Long)(block: => T): T = {
    val timer = new FakeTimer(timeMillis)
    DateTimeUtils.setCurrentMillisProvider(new FakeTimerMillisProvider(timer))
    try {
      block
    } finally {
      DateTimeUtils.setCurrentMillisSystem()
    }
  }

  /** Fake DateTime.now by specified DateTime object. */
  def fake[T](t: DateTime)(block: => T): T =
    fake(t.getMillis)(block)

  /** Fake DateTime.now by specified ISODateTimeFormat string
   *
   * e.g. TimeFaker.fake("2018-03-01T12:34:56") { }
   */
  def fake[T](t: String)(block: => T): T =
    fake(DateTime.parse(t).getMillis)(block)

  /** Fake DateTime.now by specified timeMillis.
   *
   * This method passes FakeTimer instance to block,
   * so you can advance time by tick method.
   */
  def fakeWithTimer[T](timeMillis: Long)(block: FakeTimer => T): T = {
    val timer = new FakeTimer(timeMillis)
    DateTimeUtils.setCurrentMillisProvider(new FakeTimerMillisProvider(timer))
    try {
      block(timer)
    } finally {
      DateTimeUtils.setCurrentMillisSystem()
    }
  }

  /** DateTime object version of fakeWithTimer */
  def fakeWithTimer[T](t: DateTime)(block: FakeTimer => T): T =
    fakeWithTimer(t.getMillis)(block)

  /** ISODateTimeFormat string version of fakeWithTimer */
  def fakeWithTimer[T](t: String)(block: FakeTimer => T): T =
    fakeWithTimer(DateTime.parse(t).getMillis)(block)
}

class FakeTimer(_currentMillis: Long) {
  private[this] var currentMillis = _currentMillis

  /** Advance time by millis */
  def tick(millis: Long): Unit = {
    currentMillis = currentMillis + millis
  }

  private[jodatimefake] def getMillis(): Long = currentMillis
}

private[jodatimefake] class FakeTimerMillisProvider(timer: FakeTimer) extends DateTimeUtils.MillisProvider {
  def getMillis(): Long = timer.getMillis
}
