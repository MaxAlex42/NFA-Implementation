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
    private boolean finalized;

    public NFAImpl(String startState) {
        initialState = startState;
        states = new HashSet<>();
        states.add(startState);
        //states.add("ACCEPT");
        transitions = new ArrayList<>();
        acceptingStates = new HashSet<>();
        //acceptingStates.add("ACCEPT");
    }

    @Override
    public Set<String> getStates() {
        return states;
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
    public void addTransition(Transition transition) throws FinalizedStateException {
        if (isFinalized()) {
            throw new FinalizedStateException();
        }
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
        return null;
    }

    @Override
    public NFA concatenation(NFA other) throws FinalizedStateException {
        if (!isFinalized()) {
            throw new FinalizedStateException();
        }

        this.acceptingStates.remove("ACCEPT");
        states.remove("ACCEPT");
        transitions.removeAll(this.getTransitions());
        states.add("Mitte");
        Transition t1 = new Transition("START", 'a', "Mitte");
        transitions.add(t1);
        Transition epsilonTransition = new Transition("Mitte", null, other.getInitialState());
        transitions.add(epsilonTransition);

        this.transitions.addAll(other.getTransitions());
        this.states.addAll(other.getStates());


        this.acceptingStates.addAll(other.getAcceptingStates());

        this.finalizeAutomaton();
        return this;
    }

    @Override
    public NFA kleeneStar() throws FinalizedStateException {
        if (!isFinalized()) {
            throw new FinalizedStateException();
        }

        NFAImpl NfaResult = new NFAImpl("NEWSTART");
        NfaResult.states.addAll(this.states);
        NfaResult.transitions.addAll(this.transitions);
        NfaResult.acceptingStates.addAll(this.acceptingStates);
        NfaResult.addAcceptingState("NEWSTART");
        NfaResult.addAcceptingState("START");

        Transition t1 = new Transition("NEWSTART", null, "START");
        NfaResult.transitions.add(t1);
        Transition t2 = new Transition("ACCEPT", 'a', "ACCEPT");
        NfaResult.transitions.add(t2);

        NfaResult.finalizeAutomaton();

        return NfaResult;
    }

    @Override
    public NFA plusOperator() throws FinalizedStateException {
        if (!isFinalized()) {
            throw new FinalizedStateException();
        }
        return null;
    }

    @Override
    public NFA complement() throws FinalizedStateException {
        if (!isFinalized()) {
            throw new FinalizedStateException();
        }
        NFAImpl complementNFA = new NFAImpl("START");

        for (String s : states) {
            complementNFA.states.add(s);
            if (!acceptingStates.contains(s)) {
                complementNFA.acceptingStates.add(s);
            }
        }
        complementNFA.acceptingStates.remove("ACCEPT");
        for (Transition t : transitions) {
            complementNFA.transitions.add(new Transition(t.toState(), t.readSymbol(), t.fromState()));
        }
        complementNFA.transitions.addAll(transitions);
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
        Set<String> visited = new HashSet<>();
        Map<String, Boolean> recursionStack = new HashMap<>();

        for (String state : states) {
            if (isCyclic(state, visited, recursionStack)) {
                return false;
            }
        }
        return true;
    }

    private boolean isCyclic(String state, Set<String> visited, Map<String, Boolean> recursionStack) {
        if (!visited.contains(state)) {
            visited.add(state);
            recursionStack.put(state, true);

            ArrayList<Transition> transitionsFromState = findTransition(null, state);
            for (Transition transition : transitionsFromState) {
                String nextState = transition.toState();
                if (!visited.contains(nextState) && isCyclic(nextState, visited, recursionStack)) {
                    return true;
                } else if (recursionStack.get(nextState) != null && recursionStack.get(nextState)) {
                    return true;
                }
            }
        }
        recursionStack.put(state, false);
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

    /*
    Deterministic approach
    @Override
    public boolean acceptsWord(String word) throws FinalizedStateException {
        if(!isFinalized()) {
            throw new FinalizedStateException();
        }
        String currentState = initialState;
        Transition match;
        char[] input = word.toCharArray();
        for (char c : input) {
            match = findTransition(c, currentState);
            if (match == null) {
                match = findTransition(null, currentState);
                if (match == null) {
                    return false;
                }
            }
            currentState = match.toState();
        }
        return acceptingStates.contains(currentState);
    }

    private Transition findTransition(Character c, String state) {
        for (Transition t : transitions) {
            if (t.readSymbol() == c && t.fromState().equals(state)) {
                return t;
            }
        }
        return null;
    }
     */
}
