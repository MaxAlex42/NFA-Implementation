package ab1.impl.gruppe2_berishaj_sellner_vojticek;

import ab1.FinalizedStateException;
import ab1.NFA;
import ab1.Transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NFAImpl implements NFA {
    private Set<String> states;
    private Collection<Transition> transitions;
    private Set<String> acceptingStates;
    private final String initialState;
    private char[] alphabet;
    private boolean finalized;

    public NFAImpl(String startState) {
        initialState = startState;
        states = new HashSet<>();
        states.add(startState);
        states.add("ACCEPT");
        transitions = new ArrayList<>();
        acceptingStates = new HashSet<>();
        acceptingStates.add("ACCEPT");
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
        if (isFinalized()) {
            throw new FinalizedStateException();
        }
        return null;
    }

    @Override
    public NFA intersection(NFA other) throws FinalizedStateException {
        if (isFinalized()) {
            throw new FinalizedStateException();
        }
        return null;
    }

    @Override
    public NFA concatenation(NFA other) throws FinalizedStateException {
        if (isFinalized()) {
            throw new FinalizedStateException();
        }
        return null;
    }

    @Override
    public NFA kleeneStar() throws FinalizedStateException {
        if (isFinalized()) {
            throw new FinalizedStateException();
        }
        return null;
    }

    @Override
    public NFA plusOperator() throws FinalizedStateException {
        if (isFinalized()) {
            throw new FinalizedStateException();
        }
        return null;
    }

    @Override
    public NFA complement() throws FinalizedStateException {
        if (isFinalized()) {
            throw new FinalizedStateException();
        }
        return null;
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
        return false;
    }

    @Override
    public boolean acceptsWord(String word) throws FinalizedStateException {
        if(!isFinalized()) {
            throw new FinalizedStateException();
        }
        char[] input = word.toCharArray();

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
