package io.kf.coordinator.task.etl;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import io.kf.coordinator.config.ETLDockerContainerConfig;
import io.kf.coordinator.dto.ReleaseResponse;
import io.kf.coordinator.exceptions.TaskException;
import io.kf.coordinator.service.release.ReleaseService;
import io.kf.coordinator.task.Task;
import io.kf.coordinator.task.TaskManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.kf.coordinator.exceptions.TaskException.checkTask;
import static java.lang.String.format;

@Slf4j
@Component
public class ETLTaskManager extends TaskManager {

  //TODO: must manage studyId:ReleaseId pairs. if another task is created with the same releaseId and one of the previoulsy submitted study_ids, it should error out
  private final ETLDockerContainerConfig config;
  private final ReleaseService releaseService;
  private final DockerClient docker;

  @Autowired
  public ETLTaskManager(@NonNull ReleaseService releaseService,
      @NonNull ETLDockerContainerConfig config,
      @NonNull DockerClient docker,
      @Value("${task-manager.maxQueueSize}") int maxQueueSize) {
    super(maxQueueSize);
    this.releaseService = releaseService;
    this.config = config;
    this.docker = docker;
  }

  @Override
  protected Task createTask(@NonNull String taskId, @NonNull String releaseId) throws TaskException {
    val studyIds = releaseService.getRelease(releaseId)
        .map(ReleaseResponse::getStudies)
        .orElseThrow(
            () -> new TaskException(format("ETL Task ERROR[%s]: The release '%s' was not found", taskId, releaseId))
        );

    checkTask(!studyIds.isEmpty(),
        "ETL Task ERROR[%s]: Must have at least one studyId for the release '%s'", taskId, releaseId);

    try {
      return new ETLTask(createETLDockerContainer(taskId), taskId, releaseId, studyIds);
    } catch (Exception e) {
      throw new TaskException(
          format("ETL Task ERROR[%s]: Could not create new Task for release '%s': %s",
              taskId, releaseId, e.getMessage()));
    }

  }

  private ETLDockerContainer createETLDockerContainer(String taskId)
      throws InterruptedException, DockerException, DockerCertificateException {
    return new ETLDockerContainer(
        config.getDockerImage(),
        config.isUseLocal(),
        config.getEtlConfFilePath(),
        config.getEtlJarFilePath(),
        config.getNetworkId(),
        docker );
  }

}

