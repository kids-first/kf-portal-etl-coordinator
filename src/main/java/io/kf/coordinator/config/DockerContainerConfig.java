package io.kf.coordinator.config;

import java.util.Map;

public interface DockerContainerConfig {

    String getDockerImage();

    boolean isUseLocal();

    String getNetworkId();

    Map<String,String> getMounts();
}
