package apps;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Trigger file for Cucumber tests.
 * <p>
 * This file provides default options for cucumber.
 * <p>
 * To override those using maven add -Dcucumber.options="..." to the maven
 * command line
 * <p>
 * To override those in ant, run:<br>
 * JAVA_OPTIONS='-Dcucumber.options="..."' ant target
 *
 * @author Paul Bender Copyright 2017
 */
@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"junit:cucumber-results.xml", "progress", "json:cucumber-results.json"},
        features = "java/acceptancetest/features/apps",
        tags = "not @webtest and not @Disabled and not @Ignore and not @ignore",
        glue = {"apps"})
public class RunCucumberIT {

}
