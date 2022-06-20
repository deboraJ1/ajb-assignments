package model.atoms;

public enum CarbonPosition {

    GUIDE("Guide"),
    TWIST("Twist"),
    OPPOSITE("Opposite"),
    RESIDUE("Residue");

    private final String name;

    CarbonPosition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public static CarbonPosition get(String name) {
        for(CarbonPosition position : values()) {
            if (position.getName().equalsIgnoreCase(name)) {
                return position;
            }
        }
        return RESIDUE;
    }
}
