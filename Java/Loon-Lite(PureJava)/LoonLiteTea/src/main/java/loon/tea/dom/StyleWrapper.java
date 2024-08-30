package loon.tea.dom;

public interface StyleWrapper {

    static String UNIT_MM = "mm";
    static String UNIT_CM = "cm";
    static String UNIT_IN = "in";
    static String UNIT_PC = "pc";
    static String UNIT_PT = "pt";
    static String UNIT_EX = "ex";
    static String UNIT_EM = "em";
    static String UNIT_PCT = "%";
    static String UNIT_PX = "px";

    public enum Unit {
        PX {
            @Override
            public String getType() {
                return UNIT_PX;
            }
        }, PCT {
            @Override
            public String getType() {
                return UNIT_PCT;
            }
        }, EM {
            @Override
            public String getType() {
                return UNIT_EM;
            }
        }, EX {
            @Override
            public String getType() {
                return UNIT_EX;
            }
        }, PT {
            @Override
            public String getType() {
                return UNIT_PT;
            }
        }, PC {
            @Override
            public String getType() {
                return UNIT_PC;
            }
        }, IN {
            @Override
            public String getType() {
                return UNIT_IN;
            }
        }, CM {
            @Override
            public String getType() {
                return UNIT_CM;
            }
        }, MM {
            @Override
            public String getType() {
                return UNIT_MM;
            }
        };

        public abstract String getType();
    }

    void setProperty(String property, String value);
}
