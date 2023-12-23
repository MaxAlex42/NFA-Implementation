package ab1;

import ab1.impl.gruppe2_berishaj_sellner_vojticek.NFAFactoryImpl;

public class NFAProvider {
    public static NFAFactory provideFactory() {
        return new NFAFactoryImpl();
    }
}
