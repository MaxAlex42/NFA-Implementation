package ab1.impl.gruppe2_berishaj_sellner_vojticek;

import ab1.NFA;
import ab1.NFAFactory;

public class NFAFactoryImpl implements NFAFactory {
    @Override
    public NFA buildNFA(String startState) {
        return new NFAImpl(startState);
    }
}
