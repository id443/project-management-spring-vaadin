package com.PMVaadin.PMVaadin.ProjectStructure;

import com.PMVaadin.PMVaadin.Entities.ProjectTask;

import java.util.ArrayList;
import java.util.List;

public class TreeProjectTasksImpl implements TreeProjectTasks{

    private TreeItem<ProjectTask> rootItem;
    private List<TreeItem<ProjectTask>> treeItems = new ArrayList<>();
    private final Validations validations = new ValidationsImpl();

    @Override
    public void fillWbs() {

        fillWbsRecursively(rootItem.getChildren(), "");

    }

    @Override
    public void populateTreeByList(List<ProjectTask> projectTasks) {

        rootItem = new SimpleTreeItem<>();
        TreeItemList<ProjectTask> treeItemList = new TreeItemList<>();
        this.treeItems = treeItemList.getTreeItemList(projectTasks, ProjectTask::getId, ProjectTask::getParentId);
        this.rootItem = treeItemList.getRootItem();

    }

    @Override
    public void validateTree() throws Exception {

        validations.detectCycle(treeItems);
        validations.checkQuantitiesTreeItemInTree(rootItem, treeItems);

    }

    @Override
    public TreeItem<ProjectTask> getRootItem() {
        return rootItem;
    }

    @Override
    public List<ProjectTask> recalculateProjectProperties() {

        return getProjectTasksWithChangedLevelOrder(this.rootItem);

    }

    private void fillWbsRecursively(List<TreeItem<ProjectTask>> children, String previousWbs) {

        for (TreeItem<ProjectTask> child: children) {

            ProjectTask hierarchyElement = child.getValue();
            String newWbs = previousWbs + hierarchyElement.getLevelOrder().toString();
            hierarchyElement.setWbs(newWbs);
            fillWbsRecursively(child.getChildren(), newWbs + ".");

        }

    }

    private List<ProjectTask> getProjectTasksWithChangedLevelOrder(TreeItem<ProjectTask> rootItem){

        List<ProjectTask> savedTasks = new ArrayList<>();
        calculateLevelOrderRecursively(rootItem, savedTasks);

        return savedTasks;

    }

    private void calculateLevelOrderRecursively(TreeItem<ProjectTask> rootItem, List<ProjectTask> savedTasks){

        Integer iterator = 1;
        for (TreeItem<ProjectTask> treeItem: rootItem.getChildren()) {

            ProjectTask projectTask = treeItem.getValue();
            if (!(iterator).equals(projectTask.getLevelOrder())) {
                projectTask.setLevelOrder(iterator);
                savedTasks.add(projectTask);
            }
            iterator++;
            calculateLevelOrderRecursively(treeItem, savedTasks);
        }

    }

}
