package io.kf.coordinator.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class PdfDockerContainerConfig implements DockerContainerConfig {

    @Value("${pdf.docker.image.id}")
    private String dockerImage;

    @Value("${pdf.docker.image.useLocal}")
    private boolean useLocal;

    @Value("${pdf.docker.network.id}")
    private String networkId;

    @Value("${pdf.docker.input.conf}")
    private String inputConfig;

    private Map<String, String> mounts;

    @PostConstruct
    public void initialize() {
        mounts = new HashMap<>();
        mounts.put(inputConfig, "/workdir/conf.properties");
    }
}
