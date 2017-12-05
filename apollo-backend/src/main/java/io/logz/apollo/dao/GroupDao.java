package io.logz.apollo.dao;

import io.logz.apollo.models.Group;
import java.util.List;

public interface GroupDao {
    Group getGroup(int id);
    Group getGroupByName(String name);
    List<Group> getAllGroups();
    int getScalingFactor(int id);
    void addGroup(Group group);
    void updateGroup(Group group);
    void deleteGroup(int id);
    void updateScalingFactor(Group group);
}
