/*
 * IGenericAdapter.java
 * Copyright (c) 2020 Ourcycle Sistemas.
 *
 * This software is confidential and Ourycle Sistemas property.
 * Its distribution or dissemination of its content is not permitted without
 * express authorization from Ourcycle Sistemas.
 * This archive contains proprietary information
 */

package dev.ourcycle.cm.util.dto;

import dev.ourcycle.cm.util.generic.entity.GenericEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public interface IGenericAdapter<ENTITY extends GenericEntity<ENTITY>, DTO extends GenericDTO<DTO>> {
    default DTO toDto(ENTITY entity) {
        return this.toDto(entity, false);
    }

    default DTO toDto(ENTITY entity, boolean loadCollections) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    default ENTITY toEntity(DTO dto) {
        return this.toEntity(dto, false);
    }

    default ENTITY toEntity(DTO dto, boolean loadCollections) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    default List<ENTITY> toEntities(List<DTO> dtos) {
        return this.toEntities(dtos, false);
    }

    default List<ENTITY> toEntities(List<DTO> dtos, boolean loadCollections) {
        return dtos.stream().map(dto -> this.toEntity(dto, loadCollections)).collect(Collectors.toList());
    }

    default List<DTO> toDtos(List<ENTITY> entities) {
        return this.toDtos(entities, false);
    }
    default List<DTO> toDtos(List<ENTITY> entities, boolean loadCollections) {
        return entities.stream().map(entity -> this.toDto(entity, loadCollections)).collect(Collectors.toList());
    }
}
