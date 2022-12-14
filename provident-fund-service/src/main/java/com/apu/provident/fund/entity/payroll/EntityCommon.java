package com.apu.provident.fund.entity.payroll;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public class EntityCommon implements Serializable {
    @Column(name = "CREATED_BY" )
    private Long createdBy;

    @Column(name = "CREATE_TIME" )
    private LocalDateTime createTime;

    @Column(name = "EDITED_BY" )
    private Long editedBy;

    @Column(name = "EDIT_TIME" )
    private LocalDateTime editTime;

    @Version
    @Column(name = "INTERNAL_VERSION")
    protected Long version;
}
