package DonBot.utils;

import java.awt.*;

class TypeUtils {
    private static String[] types2String = {"Mute", "Ban", "Kick", "Unmute", "Unban", "Softban"};
    private static Color[] types2Color = {Color.yellow, Color.red, Color.yellow, Color.green, Color.green, Color.BLUE};

    static String getTypeName(int type) {
        return types2String[type];
    }

    static Color getTypeColor(int type) {
        return types2Color[type];
    }
}
