package com.PMVaadin.PMVaadin.CalendarsView;

import com.PMVaadin.PMVaadin.Entities.calendar.Calendar;
import com.PMVaadin.PMVaadin.Entities.calendar.CalendarImpl;
import com.PMVaadin.PMVaadin.MainLayout;
import com.PMVaadin.PMVaadin.Services.CalendarService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.security.PermitAll;
import java.util.List;

@Route(value = "calendars", layout = MainLayout.class)
@PageTitle("Calendars | PM")
@PermitAll
@Transactional
public class CalendarsView extends VerticalLayout {

    private final CalendarService calendarService;
    private CalendarForm calendarForm;
    private Grid<CalendarImpl> grid = new Grid<>(CalendarImpl.class, false);
    private final TextField filterText = new TextField();
    private Dialog dialog;

    public CalendarsView(CalendarService calendarService) {
        this.calendarService = calendarService;
        calendarForm = new CalendarForm();
        calendarForm.addListener(CalendarForm.SaveEvent.class, this::saveCalendar);
        addClassName("calendar-list-view");
        setSizeFull();
        configureGrid();

        populateDate();

        add(getToolbar(), grid);
    }

    private void configureGrid() {
        grid.addClassNames("calendar-grid");
        grid.setSizeFull();
        grid.addColumn(CalendarImpl::getName).setHeader("Name");
        grid.addColumn(CalendarImpl::getSetting).setHeader("Setting");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);

        Button addContactButton = new Button("Add calendar");
        dialog = new Dialog();
        dialog.setHeaderTitle("New Calendar");
        dialog.add(calendarForm);
        addContactButton.addClickListener(e -> {
            calendarForm.setCalendar(new CalendarImpl());
            dialog.open();
        });
        Button editContactButton = new Button("Edit calendar");
        editContactButton.addClickListener(e -> editCalendar());
        Button deleteContactButton = new Button("Delete calendar");
        deleteContactButton.addClickListener(e -> deleteCalendar());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton,
                editContactButton, deleteContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }


    private void populateDate() {
        List<CalendarImpl> calendars = calendarService.getCalendars();
        if (null != calendars) grid.setItems(calendars);
    }

    private void saveCalendar(CalendarForm.SaveEvent event) {
        CalendarImpl calendar = event.getCalendar();
        try {
            calendarService.saveCalendars(calendar);
        } catch (Exception e) {
            Notification.show(e.getMessage());
        } finally {
            dialog.close();
            populateDate();
        }
    }

    private void deleteCalendar() {
        CalendarImpl calendar = (CalendarImpl) grid.getSelectionModel().getSelectedItems()
                .stream().findFirst().orElseThrow();
        try {
            calendarService.deleteCalendar(calendar);
        } catch (Exception e) {
            Notification.show(e.getMessage());
            return;
        }

        populateDate();
    }

    private void editCalendar() {
        Integer selectedID = ((CalendarImpl) grid.getSelectionModel().getSelectedItems()
                .stream().findFirst().orElse(new CalendarImpl())).getId();
        CalendarImpl calendarForEdit = calendarService.getCalendarById(selectedID);
        calendarForm.setCalendar(calendarForEdit);
        dialog.add(calendarForm);
        dialog.open();
        addClassName("editing");
    }
}