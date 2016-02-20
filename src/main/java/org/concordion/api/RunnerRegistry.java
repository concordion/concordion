package org.concordion.api;

/**
 * Created by tim on 20/02/16.
 */
public interface RunnerRegistry {
    Runner createRunner(String runnerType) throws Exception;
}
