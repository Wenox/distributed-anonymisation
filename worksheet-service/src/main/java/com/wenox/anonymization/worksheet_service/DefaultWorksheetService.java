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

    public Either<FailureResponse, CreateWorksheetResponse> retrieveCreateWorksheetDependencies(CreateWorksheetRequest dto) {
        Tuple3<Either<ErrorInfo, Blueprint>, Either<ErrorInfo, Restoration>, Either<ErrorInfo, Metadata>> responseTuple =
                Mono.zip(
                        blueprintServiceHandler.getResponse(dto),
                        restorationServiceHandler.getResponse(dto),
                        metadataServiceHandler.getResponse(dto))
                .block();

        List<ErrorInfo> errors = collectErrors(responseTuple);

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

    public Either<FailureResponse, CreateWorksheetResponse> createWorksheet(CreateWorksheetRequest dto) {
        Either<FailureResponse, CreateWorksheetResponse> eitherResponse = retrieveCreateWorksheetDependencies(dto);
        return eitherResponse.flatMap(createWorksheetResponse -> {
            try {
                Worksheet worksheet = new Worksheet();
                worksheet.setMetadata(createWorksheetResponse.metadata());
                worksheet.setBlueprintId(null);
                worksheet.setDatabaseName(createWorksheetResponse.blueprint().blueprintDatabaseName());
                worksheetRepository.save(worksheet);
                return Either.right(createWorksheetResponse);
            } catch (Exception ex) {
                log.error("Error while saving the worksheet: {}", ex.getMessage(), ex);
                return Either.left(new FailureResponse(List.of())); // todo
            }
        });
    }

    private List<ErrorInfo> collectErrors(Tuple3<Either<ErrorInfo, Blueprint>, Either<ErrorInfo, Restoration>, Either<ErrorInfo, Metadata>> responseTuple) {
        return Stream.of(responseTuple.getT1(), responseTuple.getT2(), responseTuple.getT3())
                .filter(Either::isLeft)
                .map(Either::getLeft)
                .collect(Collectors.toList());
    }
}
