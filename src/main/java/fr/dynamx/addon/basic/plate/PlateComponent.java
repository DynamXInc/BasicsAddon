package fr.dynamx.addon.basic.plate;

import fr.dynamx.api.ecs.Component;

import java.util.Random;

public record PlateComponent(String plate) implements Component {

    public PlateComponent {
        if (plate == null || plate.isBlank()) {
            throw new IllegalArgumentException("plate must not be blank");
        }
    }

    /** Génère une plaque depuis un pattern, sémantique identique à l'addon 1.12. */
    public static String generate(String pattern) {
        Random r = new Random();
        StringBuilder builder = new StringBuilder(pattern.length());
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '%') {
                builder.append(r.nextInt(10));
            } else if (c == '@') {
                builder.append((char) (r.nextInt(26) + 'A'));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
