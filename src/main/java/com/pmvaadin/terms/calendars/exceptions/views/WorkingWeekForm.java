package com.pmvaadin.terms.calendars.exceptions.views;

import com.pmvaadin.commonobjects.ObjectGrid;
import com.pmvaadin.terms.calendars.common.Interval;
import com.pmvaadin.terms.calendars.workingweeks.IntervalSetting;
import com.pmvaadin.terms.calendars.workingweeks.WorkingTime;
import com.pmvaadin.terms.calendars.workingweeks.WorkingWeek;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkingWeekForm extends Dialog {

    private final WorkingWeek workingWeek;
    private final WorkingTime workingTimeInstance;

    private final TextField name = new TextField("Name");
    private final DatePicker start = new DatePicker("Start");
    private final DatePicker finish = new DatePicker("Finish");

    private final Grid<DayOfWeek> days = new Grid<>();

    private final Binder<WorkingWeek> binder = new Binder<>();

    private final IntervalGrid intervals = new IntervalGrid();

    private final Map<DayOfWeek, WorkingDaysSetting> mapIntervalChanges = new HashMap<>();

    public WorkingWeekForm(WorkingWeek workingWeek) {
        this.workingWeek = workingWeek;
        this.workingTimeInstance = workingWeek.getWorkingTimeInstance();
        fillMapIntervalChanges();
        customizeForm();
        customizeElements();
        customizeHeader();
        createButtons();
        refreshHeader();
    }

    private void fillMapIntervalChanges() {
        var workingTimes = workingWeek.getWorkingTimes();
        workingTimes.forEach(workingTime -> mapIntervalChanges.put(workingTime.getDayOfWeek(),
                new WorkingDaysSetting(workingTime.getIntervalSetting(), workingTime.getCopyOfIntervals())));
    }

    private void customizeElements() {

        binder.readBean(this.workingWeek);
        days.setItems(DayOfWeek.values());
        days.addColumn(DayOfWeek::toString);
//        days.setSelectionMode(Grid.SelectionMode.MULTI);
//        days.getElement().getNode().runWhenAttached(ui ->
//                ui.beforeClientResponse(this, context ->
//                        getElement().executeJs(
//                                "if (this.querySelector('vaadin-grid-flow-selection-column')) {" +
//                                        " this.querySelector('vaadin-grid-flow-selection-column').hidden = true }")));
        intervals.setEnabled(false);
        days.select(DayOfWeek.MONDAY);

    }

    private void customizeForm() {

        setDraggable(true);
        setResizable(true);
        addClassName("calendar-form");
        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        setModal(false);
        setCloseOnEsc(false);
        //this.addListener(Class<ProjectTaskForm>, )

        var mainLayout = new FormLayout();
        mainLayout.addFormItem(name, "Name");
        mainLayout.addFormItem(start, "Start");
        mainLayout.addFormItem(finish, "Finish");

        RadioButtonGroup<IntervalSetting> radioGroup = new RadioButtonGroup<>();
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setItems(IntervalSetting.values());
        radioGroup.addValueChangeListener(this::radioButtonChangeListener);

        var intervalSetting = new VerticalLayout(radioGroup, intervals);

        var horizontalLayout = new HorizontalLayout(days, intervalSetting);

        var verticalLayout = new VerticalLayout(mainLayout, horizontalLayout);

        add(verticalLayout);


    }

    private void radioButtonChangeListener(AbstractField.ComponentValueChangeEvent<RadioButtonGroup<IntervalSetting>, IntervalSetting> event) {

        var selectedSetting = event.getValue();
        intervals.setEnabled(selectedSetting == IntervalSetting.CUSTOM);
        intervals.endEditing();

        var selectedDays = days.getSelectedItems();

        selectedDays.forEach(dayOfWeek -> {
            var workingDaysSetting = mapIntervalChanges.get(dayOfWeek);
            if (workingDaysSetting == null) return;
            workingDaysSetting.setIntervalSetting(selectedSetting);
            if (selectedSetting == IntervalSetting.CUSTOM) {
                intervals.setItems(workingDaysSetting.getIntervals());
                return;
            }
            workingDaysSetting.getIntervals().clear();
            if (selectedSetting == IntervalSetting.DEFAULT) {
                var intervalList = workingTimeInstance.getDefaultIntervals(dayOfWeek, workingWeek.getCalendar().getSetting());
                workingDaysSetting.getIntervals().addAll(intervalList);
                intervals.setItems(intervalList);
            }
        });

    }

    private void customizeHeader() {

        Button closeButton = new Button(new Icon("lumo", "cross"),
                e -> fireEvent(new CloseEvent(this, this.workingWeek))
        );
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickShortcut(Key.ESCAPE);

        getHeader().add(closeButton);

    }

    private void createButtons() {

        Button ok = new Button("Ok");
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ok.addClickListener(event -> {

            boolean validationDone = validate();
            if (!validationDone) return;
            fireEvent(new SaveEvent(this, this.workingWeek));
            close();

        });

        Button close = new Button("Cancel");
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickListener(event -> fireEvent(new CloseEvent(this, this.workingWeek)));

        getFooter().add(ok, close);

    }

    private boolean validate() {
        return false;
    }

    private void refreshHeader() {
        var workingWeekName = workingWeek.getName();
        if (workingWeekName == null) workingWeekName = "";
        var title = WorkingWeek.getHeaderName();
        setHeaderTitle(title + " for " + workingWeekName);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    public static abstract class WorkingWeekFormEvent extends ComponentEvent<WorkingWeekForm> {

        private WorkingWeek workingWeek;

        protected WorkingWeekFormEvent(WorkingWeekForm source, WorkingWeek workingWeek) {
            super(source, false);
            this.workingWeek = workingWeek;
        }

        public WorkingWeek getWorkingWeek() {
            return workingWeek;
        }

    }

    public static class CloseEvent extends WorkingWeekFormEvent {
        CloseEvent(WorkingWeekForm source, WorkingWeek workingWeek) {
            super(source, workingWeek);
        }
    }

    public static class SaveEvent extends WorkingWeekFormEvent {
        SaveEvent(WorkingWeekForm source, WorkingWeek workingWeek) {
            super(source, workingWeek);
        }
    }

    @Data
    @AllArgsConstructor
    private static class WorkingDaysSetting {

        private IntervalSetting intervalSetting;
        private List<Interval> intervals;

    }

    private class IntervalGrid extends ObjectGrid<Interval> {

        IntervalGrid() {

            customizeGrid();
            addColumns();

        }

        private void customizeGrid() {

            this.setInstantiatable(this::onAddInterval);
            this.setDeletable(true);

        }

        private Interval onAddInterval() {

            var newInterval = workingTimeInstance.getIntervalInstance();
            var maxSort = this.grid.getListDataView().getItems().map(Interval::getSort).max(Comparator.naturalOrder()).orElse(-1);
            newInterval.setSort(++maxSort);
            var selectedDays = days.getSelectedItems();
            selectedDays.forEach(dayOfWeek -> {
                var workingDaysSetting = mapIntervalChanges.get(dayOfWeek);
                if (workingDaysSetting == null) return;
                var intervalList = workingDaysSetting.getIntervals();
                intervalList.add(newInterval);
            });
            return newInterval;

        }

        private void addColumns() {

            var fromColumn = addColumn(Interval::getFrom).
                    setHeader("From");
            var fromPicker = new TimePicker();
            fromPicker.setWidthFull();
            addCloseHandler(fromPicker, this.editor);
            this.binder.forField(fromPicker).withValidator(localTime -> {
                var currentInterval = this.editor.getItem();
                var previousIntervalOpt = grid.getListDataView().getPreviousItem(currentInterval);
                if (previousIntervalOpt.isEmpty()) return true;
                return localTime.compareTo(previousIntervalOpt.get().getTo()) >= 0;
            }, "The start of a shaft must be later then the end of the previous shift.");
            fromColumn.setEditorComponent(fromPicker);

            var toColumn = addColumn(Interval::getTo).
                    setHeader("To");
            var toPicker = new TimePicker();
            toPicker.setWidthFull();
            addCloseHandler(toPicker, this.editor);
            this.binder.forField(toPicker).withValidator(localTime -> {
                var currentInterval = this.editor.getItem();
                return localTime.compareTo(currentInterval.getFrom()) <= 0;
            }, "The end of a shaft must be later then the start.");
            toColumn.setEditorComponent(toPicker);

        }

    }

}

