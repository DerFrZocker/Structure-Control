package de.derfrzocker.structure.control.impl.v1_14_R1;

import de.derfrzocker.structure.control.api.NMSUtil;
import de.derfrzocker.structure.control.api.StructureControlService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class NMSUtil_v1_14_R1 implements NMSUtil {

    @NonNull
    private final Supplier<StructureControlService> serviceSupplier;

    @Override
    public void replaceNMS() {
        new NMSReplacer_v1_14_R1(serviceSupplier).replaceNMS();
    }
}
