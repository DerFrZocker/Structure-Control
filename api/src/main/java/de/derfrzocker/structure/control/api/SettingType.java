package de.derfrzocker.structure.control.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SettingType {

    INTEGER(Integer.class), BOOLEAN(Boolean.class);

    @NonNull
    @Getter
    private final Class type;

}
