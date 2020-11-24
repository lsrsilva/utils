/*
 * GenericService.java
 * Copyright (c) 2020 Ourcycle Sistemas.
 *
 * This software is confidential and Ourycle Sistemas property.
 * Its distribution or dissemination of its content is not permitted without
 * express authorization from Ourcycle Sistemas.
 * This archive contains proprietary information
 */

package dev.ourcycle.cm.util.generic.service.impl;

import dev.ourcycle.cm.util.DefaultSortProperty;
import dev.ourcycle.cm.util.generic.entity.GenericEntity;
import dev.ourcycle.cm.util.generic.repository.IGenericRepository;
import dev.ourcycle.cm.util.generic.service.IGenericService;
import dev.ourcycle.cm.util.jsonResponse.exception.HttpStatusException;
import dev.ourcycle.cm.util.utils.Utils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import javax.transaction.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class GenericService<T extends GenericEntity<T>> implements IGenericService<T> {

    private final IGenericRepository<T, UUID> repository;

    public GenericService(IGenericRepository<T, UUID> repository) {
        this.repository = repository;
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAll(Object searchTerm) {
        if (searchTerm != null) {
            return repository.findAll(createSpec(searchTerm));
        }
        return repository.findAll();
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    public Page<T> findAll(Object value, Integer page, Integer size, String sortProperty, String sortDirection) {
        try {
            if (sortProperty == null || sortProperty.equals("undefined") || sortProperty.equals("null")) {
                sortProperty = getDefaultSortProperty();
            }

            if (size == null) {
                size = 10;
            }

            if (page == null) {
                page = 0;
            }
            Sort.Direction direction = sortDirection != null && sortDirection.equalsIgnoreCase("asc") ?
                    Sort.Direction.ASC : Sort.Direction.DESC;
            PageRequest pageRequest = PageRequest.of(page, size, direction, sortProperty);
            if (value != null) {
                return repository.findAll(createSpec(value), pageRequest);
            }
            return repository.findAll(pageRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpStatusException("Erro interno no servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<T> findAllById(Iterable<UUID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public T save(T entity) {
        makeValidations(entity);
        beforeSave(entity);
        return repository.save(entity);
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void flush() {
        repository.flush();
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        return repository.saveAndFlush(entity);
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public void delete(T entity) {
        this.repository.delete(entity);
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public void deleteAll() {
        this.repository.deleteAll();
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public void deleteById(UUID id) {
        this.repository.deleteById(id);
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public void deleteInBatch(Iterable<T> entities) {
        repository.deleteInBatch(entities);
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public void deleteAllInBatch() {
        repository.deleteAllInBatch();
    }

    @Override
    public T getOne(UUID id) {
        return repository.getOne(id);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return repository.findAll(example);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return repository.findAll(example, sort);
    }

    @Override
    public Optional<T> findById(UUID id) {
        return repository.findById(id);
    }

    @SuppressWarnings("unchecked")
    public Class<T> getEntityClass() {
        final ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }

    @Transactional
    protected void beforeSave(T entity) {
        if (entity.getId() != null) {
            beforeUpdate(entity);
        } else {
            entity.setCreatedAt(new Date());
        }

    }

    @Transactional
    protected void beforeUpdate(T editedEntity) {
        Optional<T> old = findById(editedEntity.getId());
        T oldEntity = old.orElse(null);
        if (old.isPresent()) {
            if (editedEntity.getCreatedAt() == null) {
                editedEntity.setCreatedAt(oldEntity.getCreatedAt());
            }
            editedEntity.setUpdatedAt(new Date());
            Utils.setNullValues(editedEntity, oldEntity);
        } else {
            if (editedEntity.getCreatedAt() == null) {
                editedEntity.setCreatedAt(new Date());
            }
        }
    }

    protected void makeValidations(T entity) throws HttpStatusException {

    }

    public String getDefaultSortProperty() {
        String paramSort = "";

        Field[] fields = getEntityClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(DefaultSortProperty.class)) {
                paramSort = field.getName();
                break;
            }
        }

        return paramSort;
    }

    public Specification<T> createSpec(Object searchTerm) {
        Field[] fields = getEntityClass().getDeclaredFields();
        Specification<T> spec = null;
        if (searchTerm instanceof LinkedHashMap) {
            @SuppressWarnings("unchecked")
            LinkedHashMap<String, String> lhk = (LinkedHashMap<String, String>) searchTerm;
            AtomicReference<Specification<T>> finalSpec = new AtomicReference<>(spec);
            lhk.keySet().forEach(
                    key -> finalSpec.set(createSpec(finalSpec.get(), lhk.get(key), key, true))
            );
            spec = finalSpec.get();
        } else {
            for (Field field : fields) {
                if (field.getType().equals(String.class) ||
                        (field.getType().getGenericSuperclass() != null && field.getType().getGenericSuperclass().equals(Number.class))
                                && searchTerm instanceof Number
                        || field.getType().equals(Date.class) && searchTerm instanceof Date
                        || field.getType().equals(Boolean.class) && searchTerm instanceof Boolean
                ) {
                    spec = createSpec(spec, searchTerm, field.getName());
                }
            }
        }
        return spec;
    }

    public Specification<T> createSpec(Specification<T> spec, Object searchValue, String searchProperty) {
        return createSpec(spec, searchValue, searchProperty, false);
    }

    public Specification<T> createSpec(Specification<T> spec, Object searchValue, String searchProperty, boolean withAnd) {
        return spec == null ?
                spec(searchValue, searchProperty) :
                withAnd ? spec.and(spec(searchValue, searchProperty)) :
                        spec.or(spec(searchValue, searchProperty));
    }

    private Specification<T> spec(Object searchValue, String searchProperty) {
        return (root, query, builder) -> {
            if (searchValue instanceof String) {
                return builder.like(root.get(searchProperty), "%".concat((String) searchValue).concat("%"));
            }
            return builder.equal(root.get(searchProperty), searchValue);
        };
    }

}
