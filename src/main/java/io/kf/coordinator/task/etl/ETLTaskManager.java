package io.kf.coordinator.task.etl;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import io.kf.coordinator.config.ETLDockerContainerConfig;
import io.kf.coordinator.exceptions.TaskException;
import io.kf.coordinator.service.PublishService;
import io.kf.coordinator.task.Task;
import io.kf.coordinator.task.TaskManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.kf.coordinator.exceptions.TaskException.checkTask;
import static java.lang.String.format;

@Slf4j
@Component
public class ETLTaskManager extends TaskManager {

    //TODO: must manage studyId:ReleaseId pairs. if another task is created with the same releaseId and one of the previoulsy submitted study_ids, it should error out
    private final ETLDockerContainerConfig config;
    private final DockerClient docker;
    private final PublishService publishService;

    @Autowired
    public ETLTaskManager(
            @NonNull PublishService publishService,
            @NonNull ETLDockerContainerConfig config,
            @NonNull DockerClient docker) {
        this.config = config;
        this.docker = docker;
        this.publishService = publishService;
    }

    @Override
    protected Task createTask(@NonNull String accessToken, @NonNull String taskId, @NonNull String releaseId, List<String> studies) throws TaskException {
        boolean isStudiesNullOrEmpty = studies == null || studies.isEmpty();
        checkTask(!isStudiesNullOrEmpty,
                "ETL Task ERROR[%s]: Must have at least one studyId for the release '%s' (received %s)", taskId, releaseId, studies);
        try {
            Set<String> setOfStudies = new HashSet<>(studies);
            return new ETLTask(createETLDockerContainer(), publishService, taskId, releaseId, setOfStudies);
        } catch (Exception e) {
            throw new TaskException(
                    format("ETL Task ERROR[%s]: Could not create new Task for release '%s': %s",
                            taskId, releaseId, e.getMessage()));
        }
    }

    private ETLDockerContainer createETLDockerContainer()
            throws InterruptedException, DockerException {
        return new ETLDockerContainer(
                config.getDockerImage(),
                config.isUseLocal(),
                config.getEtlConfFilePath(),
                config.getNetworkId(),
                config.getDriverMemory(),
                config.getExecutorMemory(),
                docker);
    }

}

