package com.gmail.val59000mc.lobby.pvp.zone;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class RectangleZone implements Zone {

    private final int x1;
    private final int y1;
    private final int z1;

    private final int x2;
    private final int y2;
    private final int z2;

    public RectangleZone(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = min(x1, x2);
        this.y1 = min(y1, y2);
        this.z1 = min(z1, z2);

        this.x2 = max(x1, x2);
        this.y2 = max(y1, y2);
        this.z2 = max(z1, z2);
    }

    public boolean inZone(double x, double y, double z) {
        return x1 <= x && x <= x2 && y1 <= y && y <= y2 && z1 <= z && z <= z2;
    }

}
