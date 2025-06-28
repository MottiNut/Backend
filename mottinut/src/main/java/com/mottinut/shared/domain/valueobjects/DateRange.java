package com.mottinut.shared.domain.valueobjects;

import com.mottinut.shared.domain.exceptions.ValidationException;

import java.time.LocalDate;

public class DateRange {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public DateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Las fechas no pueden ser nulas");
        }

        if (startDate.isAfter(endDate)) {
            throw new ValidationException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean contains(LocalDate date) {
        return date != null &&
                !date.isBefore(startDate) &&
                !date.isAfter(endDate);
    }

    public long getDays() {
        return startDate.until(endDate).getDays() + 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DateRange dateRange = (DateRange) obj;
        return startDate.equals(dateRange.startDate) && endDate.equals(dateRange.endDate);
    }

    @Override
    public int hashCode() {
        return startDate.hashCode() * 31 + endDate.hashCode();
    }
}
