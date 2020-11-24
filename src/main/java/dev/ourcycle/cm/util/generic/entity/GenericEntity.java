/*
 * GenericEntity.java
 * Copyright (c) 2020 Ourcycle Sistemas.
 *
 * This software is confidential and Ourycle Sistemas property.
 * Its distribution or dissemination of its content is not permitted without
 * express authorization from Ourcycle Sistemas.
 * This archive contains proprietary information
 */

package dev.ourcycle.cm.util.generic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.ourcycle.cm.util.utils.CustomJsonDateDeserializer;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
public abstract class GenericEntity<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -8362232075708470363L;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", locale = "pt-BR", timezone = "Brazil/East")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", updatable = false)
    protected Date createdAt;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", locale = "pt-BR", timezone = "Brazil/East")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    public Date getCreatedAt() {
        return createdAt;
    }

    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    public void setCreatedAt(Date createdAt) {
        if (getId() == null) {
            createdAt = new Date();
        }
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public abstract UUID getId();

    public abstract void setId(UUID id);

    public boolean updateValues(T actual) {
        boolean canUpdate = false;

        verifyCanUpdate(actual, canUpdate);

        return canUpdate;
    }

    protected void verifyCanUpdate(T actual, boolean canUpdate) {}


}
