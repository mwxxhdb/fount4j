package com.fount4j.auth.entity;

import com.fount4j.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity(name = "permission")
public class Permission extends BaseEntity {
    @Id
    private long id;
    private String key;
    private String name;
}
