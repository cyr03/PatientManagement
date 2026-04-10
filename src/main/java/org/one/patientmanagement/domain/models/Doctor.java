package org.one.patientmanagement.domain.models;

/**
 * Represents a doctor.
 */
public record Doctor(
        long id,
        long accountId, // TODO use the Long wrapper
        String profession,
        String name
) {
    public Doctor {
//        if (accountId == null || accountId <= 0) {
//            throw new IllegalArgumentException("accountId is required");
//        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
    }
}