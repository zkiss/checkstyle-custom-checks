package hu.zkiss.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MethodParameterLinesCheckTest {

    protected static DefaultConfiguration createTreeWalkerConfig(Configuration config) {
        final DefaultConfiguration dc =
                new DefaultConfiguration("configuration");
        final DefaultConfiguration twConf = new DefaultConfiguration(TreeWalker.class.getName());
        // make sure that the tests always run with this charset
        dc.addAttribute("charset", StandardCharsets.UTF_8.name());
        dc.addChild(twConf);
        twConf.addChild(config);
        return dc;
    }

    private static void assertError(List<AuditEvent> errors, int line, int col, String message) {
        var found = errors.stream()
                .filter(e -> e.getLine() == line)
                .filter(e -> e.getColumn() == col)
                .filter(e -> e.getMessage().equals(message))
                .findFirst();
        assertThat(found).isPresent();
    }

    private static Checker createChecker(List<AuditEvent> errors) {
        try {
            var cfg = new DefaultConfiguration(MethodParameterLinesCheck.class.getName());
            var checker = new Checker();
            checker.setModuleClassLoader(Thread.currentThread().getContextClassLoader());
            checker.configure(createTreeWalkerConfig(cfg));
            checker.addListener(errorListener(errors));
            return checker;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static AuditListener errorListener(List<AuditEvent> target) {
        return new AuditListener() {
            @Override
            public void auditStarted(AuditEvent event) {

            }

            @Override
            public void auditFinished(AuditEvent event) {

            }

            @Override
            public void fileStarted(AuditEvent event) {

            }

            @Override
            public void fileFinished(AuditEvent event) {

            }

            @Override
            public void addError(AuditEvent event) {
                target.add(event);
            }

            @Override
            public void addException(AuditEvent event, Throwable throwable) {

            }
        };
    }

    private List<AuditEvent> errors = new ArrayList<>();
    private Checker checker = createChecker(errors);

    @Test
    void noMethodIssues() throws Exception {
        checker.process(List.of(new File("src/checked/java/lines/NoMethodIssues.java")));

        assertThat(errors).isEmpty();
    }

    @Test
    void noConstructorIssues() throws Exception {
        checker.process(List.of(new File("src/checked/java/lines/NoConstructorIssues.java")));

        assertThat(errors).isEmpty();
    }

    @Test
    void MethodParamsNotAllOneLineOrSeparateLines() throws Exception {
        checker.process(List.of(new File("src/checked/java/lines/MethodParamsNotAllOneLineOrSeparateLines.java")));

        assertThat(errors).hasSize(1);
        assertError(errors, 4, 5, "Method parameters must be placed on a single line or in separate lines.");
    }

    @Test
    void ConstructorParamsNotAllOneLineOrSeparateLines() throws Exception {
        checker.process(List.of(new File("src/checked/java/lines/ConstructorParamsNotAllOneLineOrSeparateLines.java")));

        assertThat(errors).hasSize(1);
        assertError(errors, 4, 5, "Method parameters must be placed on a single line or in separate lines.");
    }
}