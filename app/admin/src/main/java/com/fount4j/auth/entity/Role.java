package com.fount4j.auth.entity;

import com.fount4j.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity(name = "role")
public class Role extends BaseEntity {
    @Id
    private long id;
    /**
     * 角色名称
     * <p>Role name</p>
     */
    private String name;
    /**
     * 反转角色，收集到所有的权限后，再移除反转角色中的权限
     * <p>Reverted role, after gather all permissions of a user, remove the permissions in the reverted roles</p>
     */
    private boolean reverted;
}
