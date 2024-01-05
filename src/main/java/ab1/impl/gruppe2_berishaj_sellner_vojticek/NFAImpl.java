package ab1.impl.gruppe2_berishaj_sellner_vojticek;

import ab1.FinalizedStateException;
import ab1.NFA;
import ab1.Transition;
import java.util.*;

public class NFAImpl implements NFA {
    private final Set<String> states;
    private final Collection<Transition> transitions;
    private final Set<String> acceptingStates;
    private String initialState;
    private final Set<Character> alphabet;
    private boolean finalized;

    public NFAImpl(String startState) {
        states = new HashSet<>();
        transitions = new ArrayList<>();
        acceptingStates = new HashSet<>();
        alphabet = new HashSet<>();

        initialState = startState;
        states.add(startState);
    }

    @Override
    public Set<String> getStates() {
        return states;
    }

    @Override
    public String getNextState(String currentState, char symbol) {
        String nextState = "";
        for (Transition transition : transitions) {
            if (transition.fromState().equals(currentState) && transition.readSymbol() == symbol) {
                nextState = transition.toState();
            }
        }
        return nextState;
    }


    @Override
    public Collection<Transition> getTransitions() {
        return transitions;
    }

    @Override
    public Set<String> getAcceptingStates() {
        return acceptingStates;
    }

    @Override
    public String getInitialState() {
        return initialState;
    }

    @Override
    public Set<Character> getAlphabet() {
        return alphabet;
    }

    @Override
    public void addTransition(Transition transition) throws FinalizedStateException {
        if (isFinalized()) {
            throw new FinalizedStateException();
        }
        alphabet.add(transition.readSymbol());
        transitions.add(transition);
    }

    @Override
    public void addAcceptingState(String state) throws FinalizedStateException {
        if (isFinalized()) {
            throw new FinalizedStateException();
        }
        states.add(state);
        acceptingStates.add(state);
    }

    @Override
    public NFA union(NFA other) throws FinalizedStateException {
        if (!isFinalized()) {
            throw new FinalizedStateException();
        }

        NFAImpl NfaResult = new NFAImpl("NEWSTART");
        NfaResult.states.addAll(this.states);
        NfaResult.transitions.addAll(this.transitions);
        NfaResult.acceptingStates.addAll(this.acceptingStates);
        NfaResult.states.addAll(other.getStates());
        NfaResult.transitions.addAll(other.getTransitions());
        NfaResult.acceptingStates.addAll(other.getAcceptingStates());

        Transition t1 = new Transition("NEWSTART", null, "START");
        NfaResult.transitions.add(t1);
        Transition t2 = new Transition("NEWSTART", null, "START");
        NfaResult.transitions.add(t2);

        NfaResult.finalizeAutomaton();

        return NfaResult;
    }

    @Override
    public NFA intersection(NFA other) throws FinalizedStateException {
        if (!isFinalized()) {
            throw new FinalizedStateException();
        }
        NFAImpl intersectionNFA = new NFAImpl("intersectionStartState");
        intersectionNFA.states.removeAll(states);

        //Add states and alphabet from NFAs
        intersectionNFA.states.addAll(this.states);
        intersectionNFA.initialState = this.initialState;
        intersectionNFA.states.retainAll(other.getStates());

        intersectionNFA.alphabet.addAll(this.alphabet);
        intersectionNFA.alphabet.retainAll(other.getAlphabet());

        //Create transitions for the new NFA
        for (String state : intersectionNFA.states) {
            for (char symbol : intersectionNFA.alphabet) {
                String nextStates1 = this.getNextState(state, symbol);
                String nextStates2 = other.getNextState(state, symbol);

                //Check if transitions are not null
                if (nextStates1 != null && nextStates2 != null) {
                    intersectionNFA.addTransition(new Transition(state, symbol, nextStates1));
                    for (String a : this.acceptingStates) {
                        for (String b : other.getAcceptingStates()) {
                            if (nextStates1 == a && nextStates2 == b) {
                                intersectionNFA.addAcceptingState(nextStates1);
                            }
                        }
                    }
                }
            }
        }

        intersectionNFA.finalizeAutomaton();
        return intersectionNFA;
    }

    @Override
    public NFA concatenation (NFA other) throws FinalizedStateException {
        if (!isFinalized()) {
            throw new FinalizedStateException();
        }
        NFAImpl concatNFA = new NFAImpl("NEWSTART");
        concatNFA.states.addAll(this.states);
        concatNFA.transitions.addAll(this.transitions);
        concatNFA.transitions.add(new Transition("NEWSTART", null, "START"));


        for (String state : other.getStates()) {
            concatNFA.states.add(state + "2");
        }

        for (Transition transition : other.getTransitions()) {
            concatNFA.transitions.add(new Transition(
                    transition.fromState() + "2",
                    transition.readSymbol(),
                    transition.toState() + "2"
            ));
        }
        for(String s : other.getAcceptingStates()) {
            concatNFA.addAcceptingState(s + "2");
        }

        for(String s : this.getAcceptingStates()) {
            concatNFA.transitions.add(new Transition(s, null, other.getInitialState() + "2"));
        }
        concatNFA.finalizeAutomaton();
        return concatNFA;
    }

    @Override
    public NFA kleeneStar() throws FinalizedStateException {
        if (!isFinalized()) {
            throw new FinalizedStateException();
        }

        NFAImpl NFAKleene = new NFAImpl("NEWSTART");
        NFAKleene.states.addAll(this.states);
        NFAKleene.transitions.addAll(this.transitions);
        NFAKleene.acceptingStates.addAll(this.acceptingStates);
        NFAKleene.addAcceptingState("NEWSTART");
        NFAKleene.addAcceptingState("START");

        Transition t1 = new Transition("NEWSTART", null, "START");
        NFAKleene.transitions.add(t1);
        for (char c : alphabet) {
            NFAKleene.transitions.add(new Transition("ACCEPT", c, "ACCEPT"));
        }
        NFAKleene.finalizeAutomaton();

        return NFAKleene;
    }

    @Override
    public NFA plusOperator() throws FinalizedStateException {
        if (!isFinalized()) {
            throw new FinalizedStateException();
        }

        NFAImpl NFAPlus = new NFAImpl("NEWSTART");
        NFAPlus.states.addAll(this.states);
        NFAPlus.transitions.addAll(this.transitions);
        NFAPlus.acceptingStates.addAll(this.acceptingStates);

        Transition t1 = new Transition("NEWSTART", null, "START");
        NFAPlus.transitions.add(t1);
        for(char c : alphabet) {
            NFAPlus.addTransition(new Transition("ACCEPT", c, "ACCEPT"));
        }
        NFAPlus.finalizeAutomaton();

        return NFAPlus;
    }

    @Override
    public NFA complement() throws FinalizedStateException {
        if (!isFinalized()) {
            throw new FinalizedStateException();
        }
        NFAImpl complementNFA = new NFAImpl("START");
        complementNFA.states.addAll(this.states);
        complementNFA.alphabet.addAll(this.alphabet);
        complementNFA.transitions.addAll(this.transitions);
        complementNFA.initialState = this.initialState;

        for (String state : this.states) {
            if (!this.acceptingStates.contains(state)) {
                complementNFA.acceptingStates.add(state);
            }
        }

        complementNFA.addAcceptingState("NEWACCEPT");
        for (Character c : alphabet) {
            complementNFA.addTransition(new Transition("ACCEPT", c, "NEWACCEPT"));
            complementNFA.addTransition(new Transition("NEWACCEPT", c, "NEWACCEPT"));
        }

        for (int i = 0; i < 128; i++) {
            if(!alphabet.contains((char)i)) {
                complementNFA.addTransition(new Transition("START", (char)i, "NEWACCEPT"));
                complementNFA.addTransition(new Transition("ACCEPT", (char)i, "NEWACCEPT"));
            }
        }
        complementNFA.finalizeAutomaton();
        return complementNFA;
    }

    @Override
    public boolean isFinalized() {
        return finalized;
    }

    @Override
    public void finalizeAutomaton() {
        finalized = true;
    }

    @Override
    public boolean isFinite() {
        Set<String> visitedStates = new HashSet<>();
        Set<String> currentlyVisiting = new HashSet<>();

        return !hasCycle(initialState, visitedStates, currentlyVisiting);
    }

    private boolean hasCycle(String currentState, Set<String> visitedStates, Set<String> currentlyVisiting) {
        visitedStates.add(currentState);
        currentlyVisiting.add(currentState);
        for (Transition t : transitions) {
            if (currentState.equals(t.fromState())) {
                String nextState = t.toState();
                if (currentlyVisiting.contains(nextState)) {
                    return true;
                }
                if (!visitedStates.contains(nextState) && hasCycle(nextState, visitedStates, currentlyVisiting)) {
                    return true;
                }
            }
        }
        currentlyVisiting.remove(currentState);
        return false;
    }

    @Override
    public boolean acceptsWord(String word) throws FinalizedStateException {
        if (!isFinalized()) {
            throw new FinalizedStateException();
        }
        return acceptsWordRecursive(word, initialState);
    }

    private boolean acceptsWordRecursive(String remainingWord, String currentState) {
        if (remainingWord.isEmpty()) {
            return acceptingStates.contains(currentState);
        }
        char currentSymbol = remainingWord.charAt(0);
        ArrayList<Transition> possibleTransitions = findTransition(currentSymbol, currentState);
        possibleTransitions.addAll(findTransition(null, currentState));

        for (Transition transition : possibleTransitions) {
            String nextState = transition.toState();
            if (transition.readSymbol() == null) {
                if (acceptsWordRecursive(remainingWord, nextState)) {
                    return true;
                }
            } else {
                if (transition.readSymbol() == currentSymbol && acceptsWordRecursive(remainingWord.substring(1), nextState)) {
                    return true;
                }
            }
        }
        return false;
    }

    private ArrayList<Transition> findTransition(Character c, String state) {
        ArrayList<Transition> matches = new ArrayList<>();
        for (Transition t : transitions) {
            if (t.readSymbol() == c && t.fromState().equals(state)) {
                matches.add(t);
            }
        }
        return matches;
    }
}
