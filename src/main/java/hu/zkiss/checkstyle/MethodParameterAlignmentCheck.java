package hu.zkiss.checkstyle;

import com.google.common.collect.Streams;
import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@StatelessCheck
public class MethodParameterAlignmentCheck extends AbstractCheck {

    public static final String MSG_PARAM_ALIGNMENT = "method.params.alignment";

    private static DetailAST getFirstChild(DetailAST ast, int type) {
        DetailAST c = ast.getFirstChild();
        while (c != null && c.getType() != type) {
            c = c.getNextSibling();
        }
        return requireNonNull(c);
    }

    public static Stream<DetailAST> walkDfs(DetailAST start) {
        return Streams.stream(new Iterator<DetailAST>() {
            private LinkedList<DetailAST> q = new LinkedList<>();
            {
                q.push(start);
            }

            @Override
            public boolean hasNext() {
                return !q.isEmpty();
            }

            @Override
            public DetailAST next() {
                DetailAST r = q.pop();
                if(r.getNextSibling() != null) q.push(r.getNextSibling());
                if(r.hasChildren()) q.push(r.getFirstChild());
                return r;
            }
        });
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF};
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST ast) {
        final DetailAST parameters = getFirstChild(ast, TokenTypes.PARAMETERS);

        Set<Integer> uniqueFirstColNumbersInLines = walkDfs(parameters.getFirstChild())
                .collect(Collectors.toMap(
                        it -> (Integer) it.getLineNo(),
                        it -> {
                            ArrayList<Integer> r = new ArrayList<>();
                            r.add(it.getColumnNo());
                            return r;
                        },
                        (l1, l2) -> {
                            ArrayList<Integer> allColNumbersInLine = new ArrayList<>(l1);
                            allColNumbersInLine.addAll(l2);
                            Collections.sort(allColNumbersInLine);
                            return allColNumbersInLine;
                        }
                ))
                .entrySet()
                .stream()
                .map(e -> e.getValue().get(0))
                .collect(Collectors.toSet());

        if (uniqueFirstColNumbersInLines.size() > 1) {
            log(ast.getLineNo(), ast.getColumnNo(), MSG_PARAM_ALIGNMENT);
        }
    }

}
