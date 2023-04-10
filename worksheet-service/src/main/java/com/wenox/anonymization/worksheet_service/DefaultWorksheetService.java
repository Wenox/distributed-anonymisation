package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.*;
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
public class DefaultWorksheetService {

    private final WorksheetRepository worksheetRepository;
    private final BlueprintServiceHandler blueprintServiceHandler;
    private final RestorationServiceHandler restorationServiceHandler;
    private final MetadataServiceHandler metadataServiceHandler;

    public Either<FailureResponse, CreateWorksheetResponse> createWorksheet(CreateWorksheetRequest dto) {
        Tuple3<Either<String, Blueprint>, Either<String, Restoration>, Either<String, Metadata>> responseTuple =
                Mono.zip(
                                blueprintServiceHandler.getResponse(dto),
                                restorationServiceHandler.getResponse(dto),
                                metadataServiceHandler.getResponse(dto))
                        .block();

        List<String> errors = collectErrors(responseTuple);

        if (!errors.isEmpty()) {
            return Either.left(new FailureResponse(errors));
        }

        CreateWorksheetResponse response = new CreateWorksheetResponse(
                responseTuple.getT1().get(),
                responseTuple.getT2().get(),
                responseTuple.getT3().get()
        );
        return Either.right(response);
    }

    private List<String> collectErrors(Tuple3<Either<String, Blueprint>, Either<String, Restoration>, Either<String, Metadata>> responseTuple) {
        return Stream.of(responseTuple.getT1(), responseTuple.getT2(), responseTuple.getT3())
                .filter(Either::isLeft)
                .map(Either::getLeft)
                .collect(Collectors.toList());
    }
}
