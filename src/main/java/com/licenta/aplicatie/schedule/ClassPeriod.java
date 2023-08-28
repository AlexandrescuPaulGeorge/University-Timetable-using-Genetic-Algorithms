package com.licenta.aplicatie.schedule;

public enum ClassPeriod {
    PERIOD_1("08:00-10:00"),
    PERIOD_2("10:00-12:00"),
    PERIOD_3("12:00-14:00"),
    PERIOD_4("14:00-16:00"),
    PERIOD_5("16:00-18:00"),
    PERIOD_6("18:00-20:00");

    private String timePeriod;

    ClassPeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getTimePeriod() {
        return timePeriod;
    }
}

