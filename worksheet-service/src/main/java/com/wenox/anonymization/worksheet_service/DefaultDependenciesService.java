package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.Blueprint;
import com.wenox.anonymization.worksheet_service.domain.CreateWorksheetResponse;
import com.wenox.anonymization.worksheet_service.domain.Metadata;
import com.wenox.anonymization.worksheet_service.domain.Restoration;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultDependenciesService implements DependenciesService {

    private final BlueprintServiceHandler blueprintServiceHandler;
    private final RestorationServiceHandler restorationServiceHandler;
    private final MetadataServiceHandler metadataServiceHandler;


    public Either<FailureResponse, CreateWorksheetResponse> retrieveDependencies(CreateWorksheetRequest request) {
        Tuple3<Either<ErrorInfo, Blueprint>, Either<ErrorInfo, Restoration>, Either<ErrorInfo, Metadata>> responseTuple =
                Mono.zip(
                                blueprintServiceHandler.getResponse(request),
                                restorationServiceHandler.getResponse(request),
                                metadataServiceHandler.getResponse(request))
                        .block();

        List<ErrorInfo> errors = collectErrors(responseTuple);

        if (!errors.isEmpty()) {
            return Either.left(new FailureResponse(errors));
        }

        CreateWorksheetResponse response = new CreateWorksheetResponse(
                null,
                responseTuple.getT1().get(),
                responseTuple.getT2().get(),
                responseTuple.getT3().get()
        );

        log.info("Retrieved dependencies for worksheet create: {}, worksheet request: {}", response, request);
        return Either.right(response);
    }

    private List<ErrorInfo> collectErrors(Tuple3<Either<ErrorInfo, Blueprint>, Either<ErrorInfo, Restoration>, Either<ErrorInfo, Metadata>> responseTuple) {
        return Stream.of(responseTuple.getT1(), responseTuple.getT2(), responseTuple.getT3())
                .filter(Either::isLeft)
                .map(Either::getLeft)
                .collect(Collectors.toList());
    }
}
