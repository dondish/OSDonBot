package DonBot.api;

public enum AutoModType {
    ANTIINV (1),
    ANTILINK (2),
    ANTICAPS (4),
    ANTISPAM (8),
    BANSELFBOTS (16),
    MASSMENTION (32);

    private int id;
    AutoModType(int id) {
        this.id = id;
    }
    public static boolean ifAutoMod(int automod, AutoModType type) {
        return (automod & type.id) != 0;
    }
}
