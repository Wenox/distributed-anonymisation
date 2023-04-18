package com.wenox.anonymization.suppression_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/person")
public class PersonController {

    private final PersonRepository personRepository;
    private final Jedis jedis;

    public PersonController(PersonRepository personRepository, Jedis jedis) {
        this.personRepository = personRepository;
        this.jedis = jedis;
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) throws JsonProcessingException {
        person.setId(UUID.randomUUID());
        Person savedPerson = personRepository.save(person);
        jedis.set(person.getId().toString(), new ObjectMapper().writeValueAsString(person));
        log.info("saved person: {}", person);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
    }
}