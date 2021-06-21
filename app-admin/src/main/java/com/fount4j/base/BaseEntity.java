package com.fount4j.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author Morven 2021-05-07
 */
public class BaseEntity {
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private Date createTime;
    @JsonIgnore
    @LastModifiedDate
    @Column(name = "update_time")
    private Date updateTime;
}
