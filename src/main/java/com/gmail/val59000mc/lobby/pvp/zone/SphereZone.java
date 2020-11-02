package com.gmail.val59000mc.lobby.pvp.zone;

public class SphereZone implements Zone {

    private final int x;
    private final int y;
    private final int z;

    private final int r;
    private final int sqR;

    public SphereZone(int x, int y, int z, int r) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.r = r;
        this.sqR = r * r;
    }

    @Override
    public boolean inZone(double x, double y, double z) {
        double distance = (x - this.x) * (x - this.x) + (y - this.y) * (y - this.y) + (z - this.z) * (z - this.z);
        return distance <= sqR;
    }

}
