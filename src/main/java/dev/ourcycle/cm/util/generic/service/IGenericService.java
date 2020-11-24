/*
 * IGenericService.java
 * Copyright (c) 2020 Ourcycle Sistemas.
 *
 * This software is confidential and Ourycle Sistemas property.
 * Its distribution or dissemination of its content is not permitted without
 * express authorization from Ourcycle Sistemas.
 * This archive contains proprietary information
 */

package dev.ourcycle.cm.util.generic.service;

import dev.ourcycle.cm.util.generic.entity.GenericEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface IGenericService<T extends GenericEntity<T>> {
    long count();

    List<T> findAll();

    List<T> findAll(Object value);

    Iterable<T> findAll(Sort sort);

    Page<T> findAll(Object searchObject, Integer page, Integer size, String paramSort, String sortDirection);

    <S extends T> List<S> findAll(Example<S> example);

    <S extends T> List<S> findAll(Example<S> example, Sort sort);

    List<T> findAllById(Iterable<UUID> ids);

    Optional<T> findById(UUID id);

    T save(T entity);

    <S extends T> List<S> saveAll(Iterable<S> entities);

    void flush();

    <S extends T> S saveAndFlush(S entity);

    void delete(T entity);

    void deleteById(UUID id);

    void deleteAll();

    void deleteInBatch(Iterable<T> entities);

    /**
     * Deletes all entities in a batch call.
     */
    void deleteAllInBatch();

    T getOne(UUID id);

    Class<T> getEntityClass();

}
