package io.kf.coordinator.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class ClinicalDockerContainerConfig implements DockerContainerConfig {

    @Value("${clinical.docker.image.id}")
    private String dockerImage;

    @Value("${clinical.docker.image.useLocal}")
    private boolean useLocal;

    @Value("${clinical.docker.network.id}")
    private String networkId;

    @Value("${clinical.docker.input.conf}")
    private String inputConfig;

    private Map<String, String> mounts;

    @PostConstruct
    public void initialize() {
        mounts = new HashMap<>();
        mounts.put(inputConfig, "/kf-etl/conf/kf_etl.conf");
    }


}
