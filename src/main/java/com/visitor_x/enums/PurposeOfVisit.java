package com.visitor_x.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PurposeOfVisit {

    MEETING("Meeting"),
    INTERVIEW("Interview"),
    INTERNSHIP("Internship"),
    FULL_TIME_EMPLOYEE("Full_Time_Employement");

    private final String label;

    PurposeOfVisit(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static PurposeOfVisit fromValue(String value) {
        for (PurposeOfVisit purpose : PurposeOfVisit.values()) {
            if (purpose.name().equalsIgnoreCase(value) ||
                    purpose.label.equalsIgnoreCase(value)) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("Invalid purpose of visit: " + value);
    }
}