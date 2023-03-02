package com.pmvaadin.projecttasks.dependencies;

import java.util.List;

public interface DependenciesService {

    <I, L> DependenciesSet getAllDependencies(I parentId, List<I> ids);

    <I> DependenciesSet getAllDependenciesWithCheckedChildren(I parentId, List<I> ids);

    String getCycleLinkMessage(DependenciesSet dependenciesSet);

}