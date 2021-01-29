package hu.zkiss.checkstyle;

import org.junit.jupiter.api.Test;

class MethodParameterLinesCheckTest {

    private TestCheckstyle checkstyle = new TestCheckstyle(MethodParameterLinesCheck.class);

    @Test
    void noMethodIssues() {
        checkstyle.check("lines/NoMethodIssues.java");

        checkstyle.assertNoViolations();
    }

    @Test
    void noConstructorIssues() {
        checkstyle.check("lines/NoConstructorIssues.java");

        checkstyle.assertNoViolations();
    }

    @Test
    void MethodParamsNotAllOneLineOrSeparateLines() {
        checkstyle.check("lines/MethodParamsNotAllOneLineOrSeparateLines.java");

        checkstyle.assertViolation(4, 5, "Method parameters must be placed on a single line or in separate lines.");
    }

    @Test
    void ConstructorParamsNotAllOneLineOrSeparateLines() {
        checkstyle.check("lines/ConstructorParamsNotAllOneLineOrSeparateLines.java");

        checkstyle.assertViolation(4, 5, "Method parameters must be placed on a single line or in separate lines.");
    }
}