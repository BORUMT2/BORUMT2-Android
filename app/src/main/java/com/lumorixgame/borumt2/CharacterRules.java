package com.lumorixgame.borumt2;

public class CharacterRules {
    public static final int START_LEVEL = 10;
    public static final long START_YANG = 1000000L;
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 12;

    public static boolean isValidName(String name) {
        if (name == null) return false;
        String value = name.trim();
        if (value.length() < MIN_NAME_LENGTH || value.length() > MAX_NAME_LENGTH) return false;
        if (isReservedName(value)) return false;
        return value.matches("[A-Za-zÇçĞğİıÖöŞşÜü0-9]+");
    }

    public static boolean isReservedName(String name) {
        if (name == null) return false;
        return name.equalsIgnoreCase("BÖRÜ") || name.equalsIgnoreCase("ATTİLLA");
    }
}
