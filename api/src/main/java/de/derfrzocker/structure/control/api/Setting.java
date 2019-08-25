package de.derfrzocker.structure.control.api;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class Setting {

    private static Map<String, Map<SettingType, Setting>> SETTINGS = new HashMap<>();

    public static synchronized Setting getSetting(final @NonNull String name, final @NonNull SettingType settingType) {
        final String upperName = name.toUpperCase();

        return SETTINGS.computeIfAbsent(upperName, key -> new HashMap<>()).computeIfAbsent(settingType, key -> new Setting(upperName, settingType));
    }

    @NonNull
    private final String name;

    @NonNull
    private final SettingType type;

}
