package com.pmvaadin.calendars.services;

import com.pmvaadin.calendars.entity.Calendar;
import com.pmvaadin.projectstructure.termscalculation.TermCalculationData;

import java.util.List;

public interface CalendarService {

    List<Calendar> getCalendars();

    Calendar getCalendarById(Integer id);

    void saveCalendars(Calendar calendar);

    void deleteCalendar(Calendar calendar);

    void fillCalendars(TermCalculationData termCalculationData);

}
