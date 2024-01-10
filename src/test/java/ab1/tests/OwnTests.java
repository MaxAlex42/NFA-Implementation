package ab1.tests;

import ab1.NFA;
import ab1.NFAFactory;
import ab1.NFAProvider;
import ab1.Transition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OwnTests {
    private final NFAFactory factory = NFAProvider.provideFactory();

    @Test
    public void testComplement() {
        NFA nfa = factory.buildNFA("q0");
        nfa.addTransition(Transition.builder().fromState("q0").readSymbol('a').toState("q1").build());
        nfa.addAcceptingState("q1");
        nfa.finalizeAutomaton();
        NFA complementNFA = nfa.complement();
        Assertions.assertTrue(nfa.acceptsWord("a"));
        Assertions.assertTrue(complementNFA.acceptsWord("b"));
    }

    @Test
    public void testComplexComplement() {
        NFA nfa = factory.buildNFA("q0");
        nfa.addTransition(new Transition("q0", null, "q1"));
        nfa.addTransition(new Transition("q0", 'c', "q2"));
        nfa.addTransition(new Transition("q1", 'a', "q3"));
        nfa.addTransition(new Transition("q2", 'b', "q3"));
        nfa.addAcceptingState("q3");
        nfa.finalizeAutomaton();
        NFA complementNFA = nfa.complement();
        Assertions.assertTrue(nfa.acceptsWord("a"));
        Assertions.assertTrue(nfa.acceptsWord("cb"));
        Assertions.assertTrue(complementNFA.acceptsWord("aa"));
        Assertions.assertTrue(complementNFA.acceptsWord(""));
        Assertions.assertFalse(complementNFA.acceptsWord("a"));
        Assertions.assertFalse(complementNFA.acceptsWord("cb"));
        Assertions.assertTrue(complementNFA.acceptsWord("bc"));
    }

    @Test
    public void testComplementOrig() {
        var instance = factory.buildNFA("START");
        instance.addTransition(
                Transition.builder()
                        .fromState("START")
                        .readSymbol(null)
                        .toState("S1")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .fromState("START")
                        .readSymbol(null)
                        .toState("S2")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .fromState("S1")
                        .readSymbol('a')
                        .toState("ACCEPT")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .fromState("S2")
                        .readSymbol('b')
                        .toState("ACCEPT")
                        .build()
        );
        instance.addAcceptingState("ACCEPT");
        instance.finalizeAutomaton();

        NFA complementNFA = instance.complement();

        assertFalse(complementNFA.acceptsWord("a"));
        assertFalse(complementNFA.acceptsWord("b"));
        assertTrue(complementNFA.acceptsWord("ba"));
        assertTrue(complementNFA.acceptsWord("xyaz"));
        assertTrue(complementNFA.acceptsWord("ETI is fun!"));
    }

    @Test
    public void testNull() {
        NFA nfa = factory.buildNFA("q0");
        nfa.addAcceptingState("q0");
        nfa.finalizeAutomaton();
        Assertions.assertFalse(nfa.acceptsWord(null));
        Assertions.assertTrue(nfa.acceptsWord(""));
    }

    @Test
    public void testUnion() {
        NFA nfa1 = factory.buildNFA("q0");
        nfa1.addTransition(Transition.builder().fromState("q0").readSymbol('a').toState("q1").build());
        nfa1.addAcceptingState("q1");
        NFA nfa2 = factory.buildNFA("q2");
        nfa2.addTransition(Transition.builder().fromState("q2").readSymbol('b').toState("q3").build());
        nfa2.addAcceptingState("q3");
        nfa1.finalizeAutomaton();
        nfa2.finalizeAutomaton();

        NFA unionNFA = nfa1.union(nfa2);
        assertTrue(unionNFA.acceptsWord("a"));
        assertTrue(unionNFA.acceptsWord("b"));
        assertFalse(unionNFA.acceptsWord("ab"));
    }

    @Test
    public void testConcatenation() {
        NFA nfa1 = factory.buildNFA("q0");
        nfa1.addTransition(Transition.builder().fromState("q0").readSymbol('a').toState("q1").build());
        nfa1.addTransition(Transition.builder().build());

        NFA nfa2 = factory.buildNFA("");
    }

    @Test
    public void testAcceptsWord() {
        NFA nfa = factory.buildNFA("q0");
        nfa.addTransition(Transition.builder().fromState("q0").readSymbol('a').toState("q1").build());
        nfa.addTransition(Transition.builder().fromState("q1").readSymbol('b').toState("q2").build());
        nfa.addTransition(Transition.builder().fromState("q2").readSymbol('c').toState("q3").build());
        nfa.addAcceptingState("q3");
        nfa.finalizeAutomaton();

        assertFalse(nfa.acceptsWord("a"));
        assertFalse(nfa.acceptsWord("b"));
        assertFalse(nfa.acceptsWord("c"));
        assertTrue(nfa.acceptsWord("abc"));
    }

    @Test
    public void testComplexWord() {
        NFA nfa = factory.buildNFA("q0");
        nfa.addTransition(new Transition("q0", 'a', "q1"));
        nfa.addTransition(new Transition("q1", 'b', "q2"));
        nfa.addTransition(new Transition("q2", 'b', "q2"));
        nfa.addAcceptingState("q2");
        nfa.finalizeAutomaton();

        Assertions.assertTrue(nfa.acceptsWord("ab"));
        Assertions.assertFalse(nfa.acceptsWord("a"));
        Assertions.assertFalse(nfa.acceptsWord("b"));
        Assertions.assertTrue(nfa.acceptsWord("abbbbbbbbbb"));
    }


}
