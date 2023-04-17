package com.wenox.anonymization.suppression_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/person")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        person.setId(UUID.randomUUID());
        log.info("saving person: {}", person);
        Person savedPerson = personRepository.save(person);
        log.info("saved person: {}", person);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
    }
}