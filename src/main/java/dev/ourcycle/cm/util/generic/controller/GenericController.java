/*
 * GenericController.java
 * Copyright (c) 2020 Ourcycle Sistemas.
 *
 * This software is confidential and Ourycle Sistemas property.
 * Its distribution or dissemination of its content is not permitted without
 * express authorization from Ourcycle Sistemas.
 * This archive contains proprietary information
 */

package dev.ourcycle.cm.util.generic.controller;

import dev.ourcycle.cm.util.dto.GenericDTO;
import dev.ourcycle.cm.util.dto.IGenericAdapter;
import dev.ourcycle.cm.util.generic.entity.GenericEntity;
import dev.ourcycle.cm.util.generic.service.IGenericService;
import dev.ourcycle.cm.util.jsonResponse.json.JsonResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class GenericController<ENTITY extends GenericEntity<ENTITY>, DTO extends GenericDTO<DTO>> {

    private final IGenericAdapter<ENTITY, DTO> adapter;
    private final IGenericService<ENTITY> service;
    private Logger LOG = LoggerFactory.getLogger(GenericController.class.getName());

    protected GenericController(IGenericAdapter<ENTITY, DTO> adapter, IGenericService<ENTITY> service) {
        this.adapter = adapter;
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<JsonResponseDTO> findAll(@RequestParam(defaultValue = "0", required = false) Integer page,
                                                   @RequestParam(defaultValue = "10", value = "pageSize", required = false) Integer size,
                                                   @RequestParam(required = false) String sortProperty,
                                                   @RequestParam(defaultValue = "asc", required = false) String sortDirection,
                                                   @RequestParam(defaultValue = "") Object searchTerm,
                                                   @RequestBody(required = false) Object searchObject
    ) {
        Object resultObject = null;
        List<ENTITY> entities = new ArrayList<>();
        try {
            if (searchTerm == null) {
                searchTerm = searchObject;
            }
            entities = service.findAll(searchTerm, page, size, sortProperty, sortDirection).getContent();
            resultObject = adapter.toDtos(entities);
        } catch (UnsupportedOperationException uoe) {
            LOG.info("toDtos method not implemented for adapter of class " + adapter.getClass().getName() + ", because of this it will returned the list of entity class.");
            resultObject = entities;
        }

        try {
            return ResponseEntity.ok(JsonResponseDTO.ok(resultObject));
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseDTO.otherReponse("Erro interno no servidor"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping
    public ResponseEntity<JsonResponseDTO> save(@RequestBody DTO body) {
        return ResponseEntity.ok(JsonResponseDTO.ok(adapter.toDto(service.save(adapter.toEntity(body, true)), true)));
    }

    @PutMapping
    public ResponseEntity<JsonResponseDTO> edit(@RequestBody DTO body) {
        return ResponseEntity.ok(JsonResponseDTO.ok(adapter.toDto(service.save(adapter.toEntity(body)), true)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonResponseDTO> findById(@PathVariable UUID id) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return ResponseEntity.ok(
                JsonResponseDTO.ok(
                        adapter.toDto(
                                service.findById(id).
                                        orElse(
                                                service.getEntityClass().getConstructor().newInstance()
                                        ),
                                true
                        )
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResponseDTO> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.ok(JsonResponseDTO.ok(service.getEntityClass().getSimpleName() + " deletado com sucesso!"));
    }
}
