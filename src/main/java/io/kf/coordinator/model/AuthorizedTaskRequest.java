package io.kf.coordinator.model;

import io.kf.coordinator.task.TaskAction;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class AuthorizedTaskRequest {
    @NonNull
    TaskAction action;
    @NonNull
    String task_id;
    String release_id;
    String accessToken;
    List<String> studies;
}
