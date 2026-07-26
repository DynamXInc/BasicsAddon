package fr.dynamx.addon.basic.plate;

public record PlateConfig(String pattern) {

    public String patternOrDefault() {
        return pattern != null && !pattern.isBlank() ? pattern : "aa-111-aa";
    }
}
