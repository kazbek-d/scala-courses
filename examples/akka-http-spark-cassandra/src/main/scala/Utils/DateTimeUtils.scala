package Utils

import java.time.ZonedDateTime
import com.github.nscala_time.time.Imports._

object DateTimeUtils {

  implicit class ExtentionDateTime(val dt: DateTime) {
    def toZonedDateTime = dt.toGregorianCalendar.toZonedDateTime
  }

  implicit class ExtentionZonedDateTime(val zdt: ZonedDateTime) {
    def toTimestamp = zdt.toLocalDateTime
  }

  def toSurrogate_pk_s(from: ZonedDateTime, to: ZonedDateTime) =
    (0 to java.time.Period.between(from.toLocalDate, to.toLocalDate).getDays)
      .map(from.plusDays(_))
      .map(zdt => s"${zdt.getYear}-${zdt.getMonth}-${zdt.getDayOfMonth}").toList
}
