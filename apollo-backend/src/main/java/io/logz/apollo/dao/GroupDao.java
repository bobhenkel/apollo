package io.logz.apollo.dao;

import io.logz.apollo.auth.Group;

import java.util.List;

/**
 * Created by roiravhon on 1/10/17.
 */
public interface GroupDao {

    Group getGroup(int id);
    List<Group> getAllGroups();
    void addGroup(Group group);
}
