package io.qameta.allure.xunitxml.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Optional;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;

/**
 * @author Nigel (Nigel Anderton).
 */
@Data
@Accessors(chain = true)
public class RunStamp implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(RunStamp.class);

    private Long start;
    private Long stop;

    public static RunStamp create(final String testDate, final String testTime) {
        if (testDate == null || testTime == null) {
            return new RunStamp();
        }

        final Optional<Long> startTime = RunStamp.parseTime(
            String.format(
                "%sT%s.0000000+00:00", 
                testDate, 
                testTime
            )
        );

        if (startTime.isPresent()) {
            return new RunStamp().setStart(startTime.get());
        }

        return new RunStamp();
    }

    public boolean hasValue() {
        return start != null;
    }

    public void setDuration(final Long duration) {
        final Instant stopInstant = Instant
                                .ofEpochMilli(start)
                                .plusMillis(duration);

        stop = stopInstant.toEpochMilli();
    }

    public void reset() {
        start = stop;
        stop = Long.MIN_VALUE;
    }

    private static Optional<Long> parseTime(final String time) {
        try {
            return Optional.ofNullable(time)
                    .map(ZonedDateTime::parse)
                    .map(ChronoZonedDateTime::toInstant)
                    .map(Instant::toEpochMilli);
        } catch (Exception e) {
            LOGGER.error("Could not parse time {}", time, e);
            return Optional.empty();
        }
    }

}
