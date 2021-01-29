package hu.zkiss.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCheckstyle {
    static Checker createChecker(Class<? extends AbstractCheck> check, List<AuditEvent> errors) {
        return createChecker(check, errors, (__) -> {
        });
    }

    static Checker createChecker(Class<? extends AbstractCheck> check, List<AuditEvent> errors, Consumer<? super DefaultConfiguration> cfg) {
        try {
            var checkConfig = new DefaultConfiguration(check.getName());
            cfg.accept(checkConfig);

            var checker = new Checker();
            checker.setModuleClassLoader(Thread.currentThread().getContextClassLoader());
            checker.configure(config(checkConfig));
            checker.addListener(errorListener(errors));
            return checker;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static File checkedFile(String name) {
        return new File("src/checked/java/" + name);
    }

    private static DefaultConfiguration config(Configuration checkConfig) {
        var tw = new DefaultConfiguration(TreeWalker.class.getName());
        tw.addChild(checkConfig);

        var cfg = new DefaultConfiguration("configuration");
        // make sure that the tests always run with this charset
        cfg.addAttribute("charset", StandardCharsets.UTF_8.name());
        cfg.addChild(tw);

        return cfg;
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

    private final List<AuditEvent> errors;
    private final Checker checker;

    public TestCheckstyle(Class<? extends AbstractCheck> check) {
        this(check, __ -> {});
    }

    public TestCheckstyle(Class<? extends AbstractCheck> check, Consumer<? super DefaultConfiguration> cfg) {
        errors = new ArrayList<>();
        checker = createChecker(check, errors, cfg);
    }

    void check(String checkedFile) {
        try {
            checker.process(List.of(checkedFile(checkedFile)));
        } catch (CheckstyleException e) {
            throw new RuntimeException(e);
        }
    }

    void assertViolation(int line, int col, String message) {
        var found = errors.stream()
                .filter(e -> e.getLine() == line)
                .filter(e -> e.getColumn() == col)
                .filter(e -> e.getMessage().equals(message))
                .findFirst();
        assertThat(found).isPresent();
    }

    void assertNoViolations() {
        assertThat(errors).isEmpty();
    }
}
