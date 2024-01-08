package com.pmvaadin.projecttasks.resources.entity;

import com.pmvaadin.resources.entity.LaborResource;

import java.math.BigDecimal;

public interface TaskResource {

    Integer getId();

    void setId(Integer id);

    Integer getVersion() ;

    Integer getProjectTaskId();

    void setProjectTaskId(Integer projectTaskId);

    Integer getResourceId();

    void setResourceId(Integer resourceId);

    BigDecimal getDuration();

    void setDuration(BigDecimal duration);

    int getSort();

    void setSort(int sort);

    LaborResource getLaborResource();
    void setLaborResource(LaborResource laborResource);

    TaskResource copy();

}
