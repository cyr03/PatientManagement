package org.one.patientmanagement.domain.models;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record Schedule(
        long id,
        DayOfWeek day,
        LocalTime start,
        LocalTime end,
        long doctorId
) {
    public Schedule {
        if (day == null) {
            throw new IllegalArgumentException("day is required");
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException("start/end required");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("end must be after start");
        }
        if (doctorId <= 0) {
            throw new IllegalArgumentException("doctorId must be positive");
        }
    }
}