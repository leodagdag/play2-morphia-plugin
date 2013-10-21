package leodagdag.play2morphia.test.models;

import org.mongodb.morphia.annotations.*;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.Date;

/**
 * User: leo
 * Date: 06/10/12
 * Time: 15:35
 */
@Embedded
public class Period {

    @Transient
    public LocalTime start;

    private Date _start;

    @Transient
    public LocalTime end;

    private Date _end;

    @PrePersist
    void prePersist() {
        _start = start.toDateTimeToday().toDate();
        _end = end.toDateTimeToday().toDate();
    }

    @PostLoad
    void postLoad() {
        start = new DateTime(_start.getTime()).toLocalTime();
    }

    @Reference(ignoreMissing = true)
    public Mission mission;


}
