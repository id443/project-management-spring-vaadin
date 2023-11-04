package com.pmvaadin.terms.calendars.view;

import com.pmvaadin.commonobjects.ConfirmDialog;
import com.pmvaadin.commonobjects.ObjectGrid;
import com.pmvaadin.projectstructure.NotificationDialogs;
import com.pmvaadin.terms.calendars.dayofweeksettings.DayOfWeekSettings;
import com.pmvaadin.terms.calendars.entity.Calendar;
import com.pmvaadin.terms.calendars.entity.CalendarRepresentation;
import com.pmvaadin.terms.calendars.entity.CalendarSettings;
import com.pmvaadin.terms.calendars.exceptiondays.ExceptionDay;
import com.pmvaadin.terms.calendars.services.CalendarService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.time.DayOfWeek;
import java.util.*;

@SpringComponent
public class CalendarFormNew extends Dialog {

    private CalendarRepresentation calendarRep;
    private Calendar calendar;
    private final CalendarService calendarService;

    private final TextField name = new TextField();
    private final ComboBox<CalendarSettings> setting = new ComboBox<>();
    private final TimePicker startTime = new TimePicker();

    private final NumberField monday = new NumberField();
    private final NumberField tuesday = new NumberField();
    private final NumberField wednesday = new NumberField();
    private final NumberField thursday = new NumberField();
    private final NumberField friday = new NumberField();
    private final NumberField saturday = new NumberField();
    private final NumberField sunday = new NumberField();

    private final FormLayout workingDaysLayout = new FormLayout();

    private final ExceptionDays exceptionDays = new ExceptionDays();

    private final Binder<Calendar> binder = new BeanValidationBinder<>(Calendar.class);

    private final Button sync = new Button("Refresh", new Icon("lumo", "reload"));

    private final Map<DayOfWeek, NumberField> dayOfWeekMap = new LinkedHashMap<>(7);

    public CalendarFormNew(CalendarService calendarService) {

        this.calendarService = calendarService;
        fillDayOfWeekMap();
        customizeHeader();
        customizeForm();
        customizeDataLayout();
        createButtons();
        customizeBinder();
        customizeElements();

    }

    public CalendarFormNew newInstance() {
        return new CalendarFormNew(calendarService);
    }

    public void read(CalendarRepresentation calendarRep) {

        this.calendarRep = calendarRep;
        this.calendar = calendarService.getCalendar(this.calendarRep);

        read();

    }

    public void read(Calendar calendar) {

        this.calendar = calendar;
        read();

    }

    private void fillDayOfWeekMap() {
        dayOfWeekMap.put(DayOfWeek.MONDAY, monday);
        dayOfWeekMap.put(DayOfWeek.TUESDAY, tuesday);
        dayOfWeekMap.put(DayOfWeek.WEDNESDAY, wednesday);
        dayOfWeekMap.put(DayOfWeek.THURSDAY, thursday);
        dayOfWeekMap.put(DayOfWeek.FRIDAY, friday);
        dayOfWeekMap.put(DayOfWeek.SATURDAY, saturday);
        dayOfWeekMap.put(DayOfWeek.SUNDAY, sunday);
    }

    private void customizeElements() {

        this.setting.setItems(CalendarSettings.values());
        setting.addValueChangeListener(l -> {
            var value = l.getValue();
            workingDaysLayout.setEnabled(value == CalendarSettings.DAYSOFWEEKSETTINGS);
        });

        startTime.addValueChangeListener(event -> {

        });

    }

    private void customizeBinder() {

        binder.bindInstanceFields(this);
        dayOfWeekMap.forEach((dayOfWeek, numberField) -> {
            binder.forField(numberField).bind(c -> getHoursInTheDay(dayOfWeek), (c, aDouble) -> setHoursInTheDay(dayOfWeek, aDouble));
        });
//        binder.forField(monday).bind(c -> getHoursInTheDay(DayOfWeek.MONDAY), (c, aDouble) -> setHoursInTheDay(DayOfWeek.MONDAY, aDouble));
//        binder.forField(tuesday).bind(c -> getHoursInTheDay(DayOfWeek.TUESDAY), (c, aDouble) -> setHoursInTheDay(DayOfWeek.TUESDAY, aDouble));
//        binder.forField(wednesday).bind(c -> getHoursInTheDay(DayOfWeek.WEDNESDAY), (c, aDouble) -> setHoursInTheDay(DayOfWeek.WEDNESDAY, aDouble));
//        binder.forField(thursday).bind(c -> getHoursInTheDay(DayOfWeek.THURSDAY), (c, aDouble) -> setHoursInTheDay(DayOfWeek.THURSDAY, aDouble));
//        binder.forField(friday).bind(c -> getHoursInTheDay(DayOfWeek.FRIDAY), (c, aDouble) -> setHoursInTheDay(DayOfWeek.FRIDAY, aDouble));
//        binder.forField(saturday).bind(c -> getHoursInTheDay(DayOfWeek.SATURDAY), (c, aDouble) -> setHoursInTheDay(DayOfWeek.SATURDAY, aDouble));
//        binder.forField(sunday).bind(c -> getHoursInTheDay(DayOfWeek.SUNDAY), (c, aDouble) -> setHoursInTheDay(DayOfWeek.SUNDAY, aDouble));

    }

    private Double getHoursInTheDay(DayOfWeek dayOfWeek) {
        var dayIndex = dayOfWeek.getValue();
        var daysOfWeek = this.calendar.getDaysOfWeekSettings();
        if (daysOfWeek == null || daysOfWeek.isEmpty()) return 0D;
        return daysOfWeek.stream().filter(d -> d.getDayOfWeek() == dayIndex)
                .map(DayOfWeekSettings::getCountHours).map(Calendar::getCountOfHoursDouble)
                .findFirst().orElse(0D);
    }

    private void setHoursInTheDay(DayOfWeek dayOfWeek, Double aDouble) {

        var dayIndex = dayOfWeek.getValue();
        var daysOfWeek = this.calendar.getDaysOfWeekSettings();
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            daysOfWeek = this.calendar.getSetting().getDaysOfWeekSettings();
            this.calendar.setDaysOfWeekSettings(daysOfWeek);
        }

        var dayOfWeekOptional = daysOfWeek.stream().filter(d -> d.getDayOfWeek() == dayIndex).findFirst();
        var countOfHours = Calendar.getCountOfHoursInteger(aDouble);

        DayOfWeekSettings currentDayOfWeek;
        if (dayOfWeekOptional.isEmpty()) {
            currentDayOfWeek = new DayOfWeekSettings(dayIndex, countOfHours);
            daysOfWeek.add(currentDayOfWeek);
        } else {
            currentDayOfWeek = dayOfWeekOptional.get();
            currentDayOfWeek.setCountHours(countOfHours);
        }

    }

    private void read() {

        binder.readBean(this.calendar);
        fillExceptions();
        refreshHeader();

    }

    private void fillExceptions() {

        this.exceptionDays.setItems(new ArrayList<>());
        var exceptions = this.calendar.getCalendarException();
        if (exceptions != null && !exceptions.isEmpty()) this.exceptionDays.setItems(exceptions);

    }

    private void customizeForm() {

        setDraggable(true);
        setResizable(true);
        addClassName("calendar-form");
        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        setModal(false);
        setCloseOnEsc(false);
        //this.addListener(Class<ProjectTaskForm>, )

    }

    private void customizeDataLayout() {

        //var mainLayout = new VerticalLayout(name, setting, startTime);

        var mainLayout = new FormLayout();
        mainLayout.addFormItem(name, "Name");
        mainLayout.addFormItem(setting, "Setting");
        mainLayout.addFormItem(startTime, "Start time");
//        mainLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("300px", 3));

        //var workingDaysLayout = new VerticalLayout(monday, tuesday, wednesday, thursday, friday, saturday, sunday);

        workingDaysLayout.addFormItem(monday, "Monday");
        workingDaysLayout.addFormItem(tuesday, "Tuesday");
        workingDaysLayout.addFormItem(wednesday, "Wednesday");
        workingDaysLayout.addFormItem(thursday, "Thursday");
        workingDaysLayout.addFormItem(friday, "Friday");
        workingDaysLayout.addFormItem(saturday, "Saturday");
        workingDaysLayout.addFormItem(sunday, "Sunday");

        workingDaysLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("0px", 1)
                );

        var horizontalLayout = new HorizontalLayout(mainLayout, workingDaysLayout, exceptionDays);

        add(horizontalLayout);

    }

    private void refreshHeader() {
        var calendarName = calendar.getName();
        if (calendarName == null) calendarName = "";
        var title = Calendar.getHeaderName();
        setHeaderTitle(title + ": " + calendarName);
    }

    private void customizeHeader() {

        Button closeButton = new Button(new Icon("lumo", "cross"),
                e -> fireEvent(new CloseEvent(this))
        );
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickShortcut(Key.ESCAPE);

        getHeader().add(closeButton);

    }

    private void createButtons() {

        Button saveAndClose = new Button("Save and close");
        saveAndClose.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveAndClose.addClickListener(event -> {

            boolean validationDone = validateAndSave();
            if (!validationDone) return;
            fireEvent(new SaveEvent(this));

        });
        binder.addStatusChangeListener(e -> saveAndClose.setEnabled(binder.isValid()));

        sync.addClickListener(event -> syncData());
        sync.getStyle().set("margin-right", "auto");

        Button close = new Button("Close");
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        Button save = new Button("Save");
        save.addClickListener(event -> validateAndSave());
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        getFooter().add(saveAndClose, save, sync, close);

    }

    private boolean validateAndSave() {
        try {
            binder.writeBean(this.calendar);
            this.calendar.setCalendarException(exceptionDays.getItems());
            calendarService.saveCalendars(calendar);
        } catch (Throwable e) {
            NotificationDialogs.notifyValidationErrors(e.getMessage());
            return false;
        }

        return true;

    }

    private void syncData() {
        if (this.calendarRep == null) return;
        read(this.calendarRep);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    public static abstract class CalendarFormEvent extends ComponentEvent<CalendarFormNew> {

        protected CalendarFormEvent(CalendarFormNew source) {
            super(source, false);
        }

    }

    public static class CloseEvent extends CalendarFormEvent {
        CloseEvent(CalendarFormNew source) {
            super(source);
        }
    }

    public static class SaveEvent extends CalendarFormEvent {
        SaveEvent(CalendarFormNew source) {
            super(source);
        }
    }

    private class ExceptionDays extends ObjectGrid<ExceptionDay> {

        ExceptionDays() {
            customizeColumnsAndBinder();
            customizeGrid();
            setDeletable(true);
            setInstantiatable(this::setInstantiatable);
            setCopyable(this::setCopyable);
        }

        private void customizeGrid() {

            this.grid.addItemClickListener(listener -> {
                var item = listener.getItem();
                if (item != null) return;
                grid.deselectAll();
                this.endEditing();
            });

        }

        private ExceptionDay setInstantiatable() {
            return new ExceptionDay();
        }

        private ExceptionDay setCopyable(ExceptionDay exceptionDay) {
            ExceptionDay copy = new ExceptionDay();
            copy.setDate(exceptionDay.getDate());
            copy.setDuration(exceptionDay.getDuration());
            return copy;
        }

        private void customizeColumnsAndBinder() {

            var dayColumn = addColumn(ExceptionDay::getDate).setHeader("Day");
            var hoursColumn = addColumn(this::getCountOfHours).setHeader("Hours");

            var dayColumnField = new DatePicker();
            dayColumnField.setWidthFull();
            addCloseHandler(dayColumnField, editor);
            binder.forField(dayColumnField)
                    .bind(ExceptionDay::getDate, ExceptionDay::setDate);
            dayColumn.setEditorComponent(dayColumnField);
            dayColumnField.addValueChangeListener(event -> {
                //var value = dayColumnField.getValue();
                var value = event.getValue();
                String errorMessage = null;
                var ifExistedDate = this.grid.getListDataView().getItems().anyMatch(exceptionDay -> Objects.equals(value, exceptionDay.getDate()));
                if (ifExistedDate) {
                    errorMessage = "The selected date is already contained in the table.";
                }
                dayColumnField.setErrorMessage(errorMessage);
            });

            var numberOfHoursField = new NumberField();
            numberOfHoursField.addThemeVariants(
                    //TextFieldVariant.LUMO_SMALL,
                    TextFieldVariant.LUMO_ALIGN_RIGHT
            );
            numberOfHoursField.setWidthFull();
            numberOfHoursField.setMin(0);
            numberOfHoursField.setMax(24.00);
            addCloseHandler(numberOfHoursField, editor);
            binder.forField(numberOfHoursField)//.withConverter(new BigDecimalToDoubleConverter(lagRepresentation))
                    .bind(this::getCountOfHours, this::setCountOfHours);
            //countOfHoursField.addValidationStatusChangeListener(this::validationStatusChangeEvent);
            hoursColumn.setEditorComponent(numberOfHoursField);

        }

        private void setCountOfHours(ExceptionDay exceptionDay, Double value) {

            var startTime = calendar.getStartTime();
            var seconds = startTime.getSecond();
            var secondsInHour = 24 * 3600;
            var secondsLeft = secondsInHour - seconds;
            var duration = Calendar.getCountOfHoursInteger(value);
            if (duration > secondsLeft) {
                var dialog = new ConfirmDialog();
                dialog.add("The number of hours selected is greater than the number available.");
                dialog.open();
                return;
            }

            exceptionDay.setDuration(duration);

        }

        private Double getCountOfHours(ExceptionDay exceptionDay) {
            return Calendar.getCountOfHoursDouble(exceptionDay.getDuration());
        }

    }

}
