package nl.hsac.fitnesse.junit;

import fitnesse.junit.JUnitRunNotifierResultsListener;
import fitnesse.testrunner.WikiTestPage;
import fitnesse.testsystems.TestPage;
import fitnesse.testsystems.TestSummary;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * Own subclass of JUnitRunNotifierResultsListener, to be able to control the descriptions made.
 */
public class RunNotifierResultsListener extends JUnitRunNotifierResultsListener {

    public RunNotifierResultsListener(RunNotifier notifier, Class<?> mainClass) {
        super(notifier, mainClass);
    }

    @Override
    public void testStarted(TestPage test) {
        setFirstFailure(null);
        getNotifier().fireTestStarted(descriptionFor(test));
    }

    @Override
    public void testComplete(TestPage test, TestSummary testSummary) {
        increaseCompletedTests();

        Throwable firstFailure = getFirstFailure();
        RunNotifier notifier = getNotifier();
        Description description = descriptionFor(test);
        if (firstFailure != null) {
            notifier.fireTestFailure(new Failure(description, firstFailure));
        } else if (testSummary.getExceptions() > 0) {
            notifier.fireTestFailure(new Failure(description, new Exception("Exception occurred on page " + test.getFullPath())));
        } else if (testSummary.getWrong() > 0) {
            notifier.fireTestFailure(new Failure(description, new AssertionError("Test failures occurred on page " + test.getFullPath())));
        }
        notifier.fireTestFinished(description);
    }

    protected Description descriptionFor(TestPage test) {
        Description description;
        if (test instanceof WikiTestPage) {
            description = DescriptionHelper.createDescription(getMainClass(), ((WikiTestPage) test).getSourcePage());
        } else {
            description = Description.createTestDescription(getMainClass(), test.getFullPath());
        }
        return description;
    }
}
