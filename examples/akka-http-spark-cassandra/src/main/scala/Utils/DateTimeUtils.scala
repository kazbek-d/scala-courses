package Utils

import java.time.ZonedDateTime
import org.joda.time.{DateTime, DateTimeZone}

object DateTimeUtils {

  implicit class ExtentionDateTime(val dt: DateTime) {
    def toZonedDateTime = dt.toGregorianCalendar.toZonedDateTime
  }

  implicit class ExtentionZonedDateTime(val zdt: ZonedDateTime) {
    def toDateTime = {
      val zone = DateTimeZone.forID(zdt.getZone.getId)
      new DateTime(zdt.toInstant.toEpochMilli, zone)
    }
  }

  def toSurrogate_pk_s(from: ZonedDateTime, to: ZonedDateTime) =
    (0 to java.time.temporal.ChronoUnit.DAYS.between(from.toLocalDate, to.toLocalDate).toInt)
      .map(from.plusDays(_))
      .map(zdt => s"${zdt.getYear}-${zdt.getMonth.getValue}-${zdt.getDayOfMonth}").toList
}
