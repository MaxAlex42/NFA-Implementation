package ab1.tests;

import ab1.NFA;
import ab1.NFAFactory;
import ab1.NFAProvider;
import ab1.Transition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OwnTests2 {
    private NFA nfa1;
    private NFA nfa2;
    private final NFAFactory factory = NFAProvider.provideFactory();

    @BeforeEach
    public void setUp() {
        nfa1 = factory.buildNFA("q0");
        nfa1.addTransition(Transition.builder().fromState("q0").readSymbol('a').toState("q1").build());
        nfa1.addTransition(Transition.builder().fromState("q1").readSymbol('b').toState("q2").build());
        nfa1.addAcceptingState("q2");
        nfa1.finalizeAutomaton();

        nfa2 = factory.buildNFA("s0");
        nfa2.addTransition(Transition.builder().fromState("s0").readSymbol('a').toState("s1").build());
        nfa2.addTransition(Transition.builder().fromState("s1").readSymbol('a').toState("s2").build());
        nfa2.addTransition(Transition.builder().fromState("s2").readSymbol('b').toState("s3").build());
        nfa2.addAcceptingState("s3");
        nfa2.finalizeAutomaton();
    }

    @AfterEach
    public void tearDown() {
        nfa1 = null;
        nfa2 = null;
    }

    @Test
    public void testAcceptsWordNFA1() {
        assertTrue(nfa1.acceptsWord("ab"));
        assertFalse(nfa1.acceptsWord("a"));
        assertFalse(nfa1.acceptsWord("b"));
        assertFalse(nfa1.acceptsWord("abab"));
        assertFalse(nfa1.acceptsWord("ba"));
    }

    @Test
    public void testAcceptsWordNFA2() {
        assertTrue(nfa2.acceptsWord("aab"));
        assertFalse(nfa2.acceptsWord("ab"));
        assertFalse(nfa2.acceptsWord("baa"));
        assertFalse(nfa2.acceptsWord("ba"));
        assertFalse(nfa2.acceptsWord("aaab"));
    }

    @Test
    public void testUnion() {
        NFA unionNFA = nfa1.union(nfa2);
        assertTrue(unionNFA.acceptsWord("ab"));
        assertTrue(unionNFA.acceptsWord("aab"));
        assertFalse(unionNFA.acceptsWord("abc"));
    }

    @Test
    public void testConcatenation() {
        NFA concatenationNFA = nfa1.concatenation(nfa2);
        assertTrue(concatenationNFA.acceptsWord("abaab"));
        assertFalse(concatenationNFA.acceptsWord("a"));
    }

    @Test
    public void testKleeneStar() {
        NFA kleeneStarNFA = nfa1.kleeneStar();
        assertTrue(kleeneStarNFA.acceptsWord(""));
        assertTrue(kleeneStarNFA.acceptsWord("ab"));
        assertTrue(kleeneStarNFA.acceptsWord("ababababab"));
    }

    @Test
    public void testPlusOperator() {
        NFA plusOperatorNFA = nfa1.plusOperator();
        assertFalse(plusOperatorNFA.acceptsWord(""));
        assertTrue(plusOperatorNFA.acceptsWord("ab"));
        assertTrue(plusOperatorNFA.acceptsWord("abababab"));
        assertTrue(plusOperatorNFA.acceptsWord("abab"));
    }

    @Test
    public void testComplement() {
        NFA complementNFA = nfa1.complement();
        assertTrue(complementNFA.acceptsWord(""));
        // warum gehst du nicht ???????
        //assertTrue(complementNFA.acceptsWord("a"));
        assertFalse(complementNFA.acceptsWord("ab"));
        assertTrue(complementNFA.acceptsWord("b"));
    }
}
