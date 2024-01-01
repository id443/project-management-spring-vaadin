package com.pmvaadin.projecttasks.data;

import com.pmvaadin.common.ChangedTableData;
import com.pmvaadin.projecttasks.entity.ProjectTask;
import com.pmvaadin.projecttasks.links.entities.Link;
import com.pmvaadin.terms.calendars.entity.Calendar;
import com.pmvaadin.terms.timeunit.entity.TimeUnit;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
//@AllArgsConstructor
public class ProjectTaskDataImpl implements ProjectTaskData {

    private ProjectTask projectTask;

    // Links
    private ChangedTableData<? extends Link> linksChangedTableData;
    private List<Link> links;

    private LocalDateTime projectStartDate;

    private Calendar calendar;

    private TimeUnit timeUnit;

    private Link linkSample;

    public ProjectTaskDataImpl(ProjectTask projectTask, List<Link> links, LocalDateTime projectStartDate,
                               Calendar calendar, TimeUnit timeUnit, Link linkSample) {
        this.projectTask = projectTask;
        this.links = links;
        this.projectStartDate = projectStartDate;
        this.calendar = calendar;
        this.timeUnit = timeUnit;
        this.linkSample = linkSample;
    }

}
