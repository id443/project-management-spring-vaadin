package com.pmvaadin.projecttasks.services.role.level.calculation;

import com.pmvaadin.common.ListOfObjectsToListItsIdConverter;
import com.pmvaadin.projecttasks.entity.ProjectTask;
import com.pmvaadin.projecttasks.entity.ProjectTaskImpl;
import com.pmvaadin.projecttasks.services.role.level.queries.QueryBuilderForOnlyInListAccess;
import com.pmvaadin.security.entities.UserProject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProjectTasksRoleLevelSecurity {
    private final EntityManager entityManager;
    private final QueryBuilderForOnlyInListAccess queryBuilder = new QueryBuilderForOnlyInListAccess();

    public ProjectTasksRoleLevelSecurity(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<ProjectTask> getProjectTasksIfParentIsNull(List<UserProject> userProjects) {

        var ids = getIdsAsString(userProjects);
        var queryText = queryBuilder.getQueryTextForUpperLevel(false, ids);

        return getProjectTasksByQueryText(queryText);

    }

    public List<ProjectTask> getProjectTasksOfParent(List<UserProject> userProjects, ProjectTask parent) {

        var ids = getIdsAsString(userProjects);
        var queryText = queryBuilder.getQueryTextForParent(false, ids, parent.getId().toString());

        return getProjectTasksByQueryText(queryText);

    }

    public int getCountProjectTasksIfParentIsNull(List<UserProject> userProjects) {

        var ids = getIdsAsString(userProjects);
        var queryText = queryBuilder.getQueryTextForUpperLevel(true, ids);

        return getCountProjectTasksByQueryText(queryText);

    }

    public int getCountProjectTasksOfParent(List<UserProject> userProjects, ProjectTask parent) {

        var ids = getIdsAsString(userProjects);
        var queryText = queryBuilder.getQueryTextForParent(true, ids, parent.getId().toString());

        return getCountProjectTasksByQueryText(queryText);

    }

    private int getCountProjectTasksByQueryText(String queryText) {
        var query = entityManager.createNativeQuery(queryText);
        List<Object> tasks = (List<Object>) query.getResultList();
        return tasks.stream().map(o -> (int) o).findFirst().orElse(0);
    }

    private List<ProjectTask> getProjectTasksByQueryText(String queryText) {
        var query = entityManager.createNativeQuery(queryText, ProjectTaskImpl.class);
        List<ProjectTaskImpl> tasks = (List<ProjectTaskImpl>) query.getResultList();
        return tasks.stream().map(o -> (ProjectTask) o).collect(Collectors.toList());
    }

    private String getIdsAsString(List<UserProject> userProjects) {
        return ListOfObjectsToListItsIdConverter.getIdsAsString(userProjects, UserProject::getProjectId);
    }

}
