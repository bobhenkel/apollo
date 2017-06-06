package io.logz.apollo.dao;

import io.logz.apollo.blockers.BlockerDefinition;

import java.util.List;

/**
 * Created by roiravhon on 6/4/17.
 */
public interface BlockerDefinitionDao {
    BlockerDefinition getBlockerDefinition(int id);
    List<BlockerDefinition> getAllBlockerDefinitions();
    void addBlockerDefinition(BlockerDefinition blockerDefinition);
    void updateBlockerDefinition(BlockerDefinition blockerDefinition);

}
