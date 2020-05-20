package com.github.shibayu36.jodatimefake

import org.joda.time.{DateTime, DateTimeUtils}

/** Provides utility methods to fake current time of org.joda.time.DateTime.
 *
 * ==Provided Methods==
 * <ul>
 *   <li>fake</li>
 *   <li>fakeWithTimer</li>
 * </ul>
 * ==Basic Usage==
 * {{{
 * import com.github.shibayu36.jodatimefake.FakeTimer
 * import org.joda.time.DateTime
 *
 * // fake by millis
 * val result = FakeTimer.fake(1515974400000L) {
 *     println(DateTime.now.toString) // 2018-01-15T00:00:00.000Z
 *     "hoge"
 * }
 *
 * // current time is restored after block
 * println(DateTime.now.toString) // current time is printed
 *
 * // You can get the value returned from block
 * println(result) // hoge
 *
 * // You can also pass DateTime instance
 * FakeTimer.fake(new DateTime(2018, 2, 13, 14, 59)) {
 *
 * }
 *
 * // You can also pass ISODateTimeFormat
 * FakeTimer.fake("2018-03-02T12:34:56+09:00") {
 *
 * }
 *
 * // If you use fakeWithTimer, a timer instance is passed to block.
 * // You can advance time using tick() method.
 * FakeTimer.fakeWithTimer(1515974400000L) { t =>
 *     println(DateTime.now.toString) // 2018-01-15T00:00:00.000Z
 *     t.tick(3000) // Advance time by 3000ms
 *     println(DateTime.now.toString) // 2018-01-15T00:00:03.000Z
 * }
 * }}}
 */
object FakeTimer {
  /** Fakes DateTime.now only in passed block by specified timeMillis.
   *
   * Time will be restored after block.
   */
  def fake[T](timeMillis: Long)(block: => T): T = {
    val timer = new FakeTimer(timeMillis)
    ThreadLocalDateTimeUtils.setCurrentMillisProvider(new FakeTimerMillisProvider(timer))
    try {
      block
    } finally {
      ThreadLocalDateTimeUtils.setCurrentMillisSystem()
    }
  }

  /** Fakes DateTime.now by specified DateTime object. */
  def fake[T](t: DateTime)(block: => T): T =
    fake(t.getMillis)(block)

  /** Fakes DateTime.now by specified ISODateTimeFormat string
   *
   * e.g. TimeFaker.fake("2018-03-01T12:34:56") { }
   */
  def fake[T](t: String)(block: => T): T =
    fake(DateTime.parse(t).getMillis)(block)

  /** Fakes DateTime.now by specified timeMillis.
   *
   * This method passes FakeTimer instance to block,
   * so you can advance time by tick method.
   *
   * {{{
   * FakeTimer.fakeWithTimer(1515974400000L) { t =>
   *   t.tick(3000) // Advance time by 3000ms
   * }
   * }}}
   */
  def fakeWithTimer[T](timeMillis: Long)(block: FakeTimer => T): T = {
    val timer = new FakeTimer(timeMillis)
    ThreadLocalDateTimeUtils.setCurrentMillisProvider(new FakeTimerMillisProvider(timer))
    try {
      block(timer)
    } finally {
      ThreadLocalDateTimeUtils.setCurrentMillisSystem()
    }
  }

  /** DateTime object version of fakeWithTimer */
  def fakeWithTimer[T](t: DateTime)(block: FakeTimer => T): T =
    fakeWithTimer(t.getMillis)(block)

  /** ISODateTimeFormat string version of fakeWithTimer */
  def fakeWithTimer[T](t: String)(block: FakeTimer => T): T =
    fakeWithTimer(DateTime.parse(t).getMillis)(block)
}

/** FakeTimer class to advance time in fakeWithTimer */
class FakeTimer(private[this] var currentMillis: Long) {
  /** Advance time by millis */
  def tick(millis: Long): Unit = {
    currentMillis = currentMillis + millis
  }

  private[jodatimefake] def getMillis(): Long = currentMillis
}

private[jodatimefake] class FakeTimerMillisProvider(timer: FakeTimer) extends DateTimeUtils.MillisProvider {
  def getMillis(): Long = timer.getMillis()
}

private[jodatimefake] object ThreadLocalDateTimeUtils {
  private val SystemMillisProvider: DateTimeUtils.MillisProvider = new DateTimeUtils.MillisProvider {
      override def getMillis(): Long = System.currentTimeMillis
  }

  def setCurrentMillisSystem() = {
      install()
      ThreadLocalMillisProvider.local.set(SystemMillisProvider)
  }

  def setCurrentMillisProvider(millisProvider: DateTimeUtils.MillisProvider) = {
      install()
      ThreadLocalMillisProvider.local.set(millisProvider)
  }

  def current(): DateTimeUtils.MillisProvider = ThreadLocalMillisProvider.local.get

  def clear() = ThreadLocalMillisProvider.local.remove()

  private def install() = DateTimeUtils.setCurrentMillisProvider(ThreadLocalMillisProvider)

  private object ThreadLocalMillisProvider extends DateTimeUtils.MillisProvider {
    private[ThreadLocalDateTimeUtils] val local: ThreadLocal[DateTimeUtils.MillisProvider] = new ThreadLocal[DateTimeUtils.MillisProvider] {
        override def initialValue(): DateTimeUtils.MillisProvider = SystemMillisProvider
    }

    override def getMillis(): Long = local.get.getMillis
  }
}
