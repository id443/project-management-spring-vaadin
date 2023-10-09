package com.pmvaadin.projecttasks.services;

import com.pmvaadin.commonobjects.ChangedTableData;
import com.pmvaadin.projecttasks.data.ProjectTaskData;
import com.pmvaadin.projecttasks.data.ProjectTaskDataImpl;
import com.pmvaadin.projecttasks.entity.ProjectTask;
import com.pmvaadin.projecttasks.links.entities.Link;
import com.pmvaadin.projecttasks.links.services.LinkService;
import com.pmvaadin.projecttasks.repositories.ProjectTaskRepository;
import com.pmvaadin.terms.calendars.entity.Calendar;
import com.pmvaadin.terms.calendars.services.CalendarService;
import com.pmvaadin.terms.timeunit.entity.TimeUnit;
import com.pmvaadin.terms.timeunit.services.TimeUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectTaskDataServiceImpl implements ProjectTaskDataService{

    private ProjectTaskService projectTaskService;
    private LinkService linkService;
    private CalendarService calendarService;
    private TimeUnitService timeUnitService;
    private ProjectTaskRepository projectTaskRepository;
    private ApplicationContext applicationContext;

    @Autowired
    void setProjectTaskService(ProjectTaskService projectTaskService) {
        this.projectTaskService = projectTaskService;
    }

    @Autowired
    void setLinkService(LinkService linkService) {
        this.linkService = linkService;
    }

    @Autowired
    void setCalendarService(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @Autowired
    void setProjectTaskRepository(ProjectTaskRepository projectTaskRepository) {
        this.projectTaskRepository = projectTaskRepository;
    }

    @Autowired
    public void setTimeUnitService(TimeUnitService timeUnitService) {
        this.timeUnitService = timeUnitService;
    }

    @Autowired
    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    @Transactional
    public ProjectTaskData save(ProjectTaskData projectTaskData) {

        // validation
        boolean validationPass = projectTaskService.validate(projectTaskData.getProjectTask());
        if (!validationPass) return projectTaskData;

        fillLinksByChanges(projectTaskData);

        validationPass = linkService.validate(projectTaskData);
        if (!validationPass) return projectTaskData;

        return saveData(projectTaskData);

    }

    @Override
    @Transactional(readOnly = true)
    public ProjectTaskData read(ProjectTask projectTask) {

        ProjectTask syncedProjectTask = projectTask;
        List<Link> links = new ArrayList<>(0);
        if (!projectTask.isNew()) {
            syncedProjectTask = projectTaskService.sync(projectTask);
            links = linkService.getLinksWithProjectTaskRepresentation(syncedProjectTask);
        }

        if (syncedProjectTask.isNew()) syncedProjectTask.setDuration(Calendar.DAY_DURATION_SECONDS);

        return getProjectTaskData(syncedProjectTask, links);

    }

    private void fillAdditionalData(ProjectTaskData projectTaskData) {

        fillTerms(projectTaskData);
        fillTimeUnitId(projectTaskData);
        fillDurationRepresentation(projectTaskData);

    }

    private void fillTimeUnitId(ProjectTaskData projectTaskData) {

        ProjectTask projectTask = projectTaskData.getProjectTask();
        if (projectTask.getTimeUnitId() != null) return;
        projectTask.setTimeUnitId(projectTaskData.getTimeUnit().getId());

    }

    private void fillDurationRepresentation(ProjectTaskData projectTaskData) {

        ProjectTask projectTask = projectTaskData.getProjectTask();
        TimeUnit timeUnit = projectTaskData.getTimeUnit();
        BigDecimal durationRep = timeUnit.getDurationRepresentation(projectTask.getDuration());
        projectTask.setDurationRepresentation(durationRep);

    }

    private void fillTerms(ProjectTaskData projectTaskData) {

        ProjectTask projectTask = projectTaskData.getProjectTask();
        LocalDateTime start = projectTask.getStartDate();
        LocalDateTime finish = projectTask.getFinishDate();
        if (start != null && finish != null) return;
        Calendar calendar = projectTaskData.getCalendar();
        long duration = projectTask.getDuration();
        if (start == null) {
            start = projectTaskData.getProjectStartDate();
            projectTask.setStartDate(start);
            projectTask.setFinishDate(calendar.getDateByDuration(start, duration));
        }
        if (finish == null)
            projectTask.setFinishDate(calendar.getDateByDuration(start, duration));

    }

    private AdditionalData getAdditionalData(ProjectTask projectTask) {

        // TODO getting a project of the task, instead of the parent
        ProjectTask parent = null;
        if (projectTask.getParentId() != null)
            parent = projectTaskRepository.findById(projectTask.getParentId()).orElse(null);

        Object calendarId = projectTask.getCalendarId();
        LocalDateTime defaultStartDate = null;
        if (calendarId == null && parent != null) {
            calendarId = parent.getCalendarId();
            defaultStartDate = parent.getStartDate();
        }

        Calendar calendar;
        if (calendarId != null) calendar = calendarService.getCalendarById(calendarId);
        else calendar = calendarService.getDefaultCalendar();

        if (defaultStartDate == null) {
            LocalDateTime nowDate = LocalDateTime.now();
            defaultStartDate = LocalDateTime.of(nowDate.toLocalDate(), calendar.getStartTime());
        }

        Integer timeUnitId = projectTask.getTimeUnitId();
        if (timeUnitId == null && parent != null) {
            timeUnitId = parent.getTimeUnitId();
        }

        TimeUnit timeUnit;
        if (timeUnitId != null) timeUnit = timeUnitService.getTimeUnitById(timeUnitId);
        else timeUnit = timeUnitService.getPredefinedTimeUnit();

        return new AdditionalData(calendar, defaultStartDate, timeUnit);

    }

    private void fillLinksByChanges(ProjectTaskData projectTaskData) {

        if (projectTaskData.getLinksChangedTableData() == null && projectTaskData.getLinks() != null) return;

        linkService.fillLinksByChanges(projectTaskData);

    }

    private ProjectTaskData saveData(ProjectTaskData projectTaskData) {

        ProjectTask projectTask = projectTaskData.getProjectTask();
        boolean isNew = projectTask.isNew();
        if (isNew) {
            projectTask = projectTaskService.save(projectTask, false, false);
            projectTaskData.setProjectTask(projectTask);
        }

        fillMainFields(projectTaskData);

        saveChanges(projectTaskData, isNew);

        calculateTerms(projectTaskData);

        return getProjectTaskDateRespond(projectTaskData);

    }

    private ProjectTaskData getProjectTaskDateRespond(ProjectTaskData projectTaskData) {

        ProjectTask projectTask = projectTaskData.getProjectTask();
        List<Link> links = linkService.getLinksWithProjectTaskRepresentation(projectTask);
        //projectTaskService.fillParent(projectTask);

        return getProjectTaskData(projectTask, links);

    }

    private ProjectTaskData getProjectTaskData(ProjectTask projectTask, List<Link> links) {

        Link sampleLink = applicationContext.getBean(Link.class);
        AdditionalData additionalData = getAdditionalData(projectTask);
        ProjectTaskData projectTaskData = new ProjectTaskDataImpl(projectTask, links,
                additionalData.defaultStartDate,
                additionalData.calendar,
                additionalData.timeUnit,
                sampleLink);

        fillAdditionalData(projectTaskData);

        return projectTaskData;

    }

    private void saveChanges(ProjectTaskData projectTaskData, boolean isNew) {

        ChangedTableData<? extends Link> changedTableData = projectTaskData.getLinksChangedTableData();
        if (changedTableData != null) {
            List<? extends Link> newLinks = changedTableData.getNewItems();
            List<? extends Link> deletedLinks = changedTableData.getDeletedItems();
            boolean increaseCheckSum = newLinks.size() > 0 || deletedLinks.size() > 0;
            linkService.delete(deletedLinks);

            increaseCheckSum = increaseCheckSum || isChangedFields(projectTaskData);

            saveChanges(changedTableData);

            if (!isNew && increaseCheckSum) {
                renewLinksCheckSum(projectTaskData);
            }

            return;

        }

        List<Link> linksInDatabase = linkService.getLinks(projectTaskData.getProjectTask());
        List<Link> currentLinks = projectTaskData.getLinks();

        Set<Object> idsCurrentLinks = currentLinks.stream()
                .map(Link::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Object> idsLinksInDataBase = linksInDatabase.stream().map(Link::getId).collect(Collectors.toSet());

        boolean isChanges = false;
        if (idsLinksInDataBase.removeAll(idsCurrentLinks) && !idsLinksInDataBase.isEmpty()) {
            List<Link> deletedLinks = linksInDatabase.stream()
                    .filter(l -> idsLinksInDataBase.contains(l.getId())).toList();
            linkService.delete(deletedLinks);
            linksInDatabase.removeAll(deletedLinks);
            isChanges = true;
        }

        if (!isNew) {
            isChanges = isChanges || currentLinks.stream().map(Link::getId).anyMatch(Objects::isNull);
            isChanges = isChanges || identifyChanges(linksInDatabase, currentLinks);
            if (isChanges) renewLinksCheckSum(projectTaskData);
        }

        linkService.save(currentLinks);

    }

    private void saveChanges(ChangedTableData<? extends Link> changedTableData) {

        List<? extends Link> newLinks = changedTableData.getNewItems();
        List<? extends Link> changedLinks = changedTableData.getChangedItems();
        List<Link> savedLinks = new ArrayList<>(newLinks.size() + changedLinks.size());
        savedLinks.addAll(newLinks);
        // TODO to check an existence of the changed links
        savedLinks.addAll(changedLinks);
        linkService.save(savedLinks);

    }

    private void calculateTerms(ProjectTaskData projectTaskData) {

        Set<Object> ids = new HashSet<>(1);
        ids.add(projectTaskData.getProjectTask().getId());
        List<ProjectTask> changedTasks = projectTaskService.recalculateTerms(ids);
        ProjectTask projectTask = projectTaskData.getProjectTask();
        for (ProjectTask changedTask: changedTasks) {
            if (changedTask.equals(projectTask)) {
                projectTaskData.setProjectTask(changedTask);
                break;
            }
        }

    }

    private void renewLinksCheckSum(ProjectTaskData projectTaskData) {

        ProjectTask projectTask = projectTaskData.getProjectTask();
        int checkSum = projectTask.getLinksCheckSum();
        projectTask.setLinksCheckSum(++checkSum);
        projectTask = projectTaskService.save(projectTask, false, false);
        projectTaskData.setProjectTask(projectTask);

    }

    private void fillMainFields(ProjectTaskData projectTaskData) {

        var links= projectTaskData.getLinks();
        var projectTask = projectTaskData.getProjectTask();

        if (links.size() == 0) return;

        int maxSort = links.stream().map(Link::getSort).filter(Objects::nonNull).max(Integer::compare).orElse(0);

        for (Link link:links) {
            if (link.getProjectTaskId() == null) link.setProjectTaskId(projectTask.getId());
            if (Objects.isNull(link.getSort())) link.setSort(++maxSort);
        }

    }

    private boolean isChangedFields(ProjectTaskData projectTaskData) {

        List<? extends Link> changedLinks = projectTaskData.getLinksChangedTableData().getChangedItems();

        if (changedLinks.size() == 0) return false;

        return identifyChanges(changedLinks, projectTaskData.getLinks());

    }

    private boolean identifyChanges(List<? extends Link> changedLinks, List<? extends Link> links) {

        var changedLinksMap = changedLinks.stream().collect(Collectors.toMap(Link::getId, l -> l));

        return links.stream().anyMatch(link -> {

            var changedLink = changedLinksMap.getOrDefault(link.getId(), null);
            if (changedLink == null) return true;
            return isChanges(link, changedLink);

        });

    }

    private boolean isChanges(Link link, Link equaledLink) {

        return !(Objects.equals(link.getLinkedProjectTaskId(), equaledLink.getLinkedProjectTaskId())
                && Objects.equals(link.getLinkType(), equaledLink.getLinkType())
                && Objects.equals(link.getLag(), equaledLink.getLag())
                && Objects.equals(link.getTimeUnit(), equaledLink.getTimeUnit())
                && Objects.equals(link.getSort(), equaledLink.getSort()));

    }

    private record AdditionalData(Calendar calendar, LocalDateTime defaultStartDate, TimeUnit timeUnit) {}

}
