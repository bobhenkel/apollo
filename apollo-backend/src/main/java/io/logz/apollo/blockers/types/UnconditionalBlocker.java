package io.logz.apollo.blockers.types;

import io.logz.apollo.blockers.BlockerFunction;
import io.logz.apollo.blockers.BlockerInjectableCommons;
import io.logz.apollo.blockers.BlockerType;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Deployment;

import java.io.IOException;

/**
 * Created by roiravhon on 6/4/17.
 */
@BlockerType(name = "unconditional")
public class UnconditionalBlocker implements BlockerFunction {
    @Override
    public void init(String jsonConfiguration) throws IOException {

    }

    @Override
    public boolean shouldBlock(BlockerInjectableCommons blockerInjectableCommons, Deployment deployment) {
        return true;
    }
}
