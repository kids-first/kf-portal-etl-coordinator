package io.kf.coordinator.config;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import io.kf.coordinator.service.PublishService;
import io.kf.coordinator.service.ReleaseService;
import io.kf.coordinator.task.etl.ETLTaskManager;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ETLTaskManagerConfig {

    @Bean
    @SneakyThrows
    public DockerClient docker() {
        return DefaultDockerClient.fromEnv().build();
    }

    @Bean
    public ETLTaskManager clinicalEtlTaskManager(ReleaseService releaseService, PublishService publishService, ClinicalDockerContainerConfig clinicalDockerContainerConfig, DockerClient docker) {
        return new ETLTaskManager(releaseService, publishService, clinicalDockerContainerConfig, docker);
    }

    @Bean
    public ETLTaskManager pdfEtlTaskManager(ReleaseService releaseService, PublishService publishService, PdfDockerContainerConfig pdfDockerContainerConfig, DockerClient docker) {
        return new ETLTaskManager(releaseService, publishService, pdfDockerContainerConfig, docker);
    }


}
