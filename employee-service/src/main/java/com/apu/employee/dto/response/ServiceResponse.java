package com.apu.employee.dto.response;

import com.apu.employee.dto.Pagination;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ServiceResponse<T> implements Serializable{

    private Metadata meta;

    private T data;

    private Pagination pagination;

    public ServiceResponse(Metadata meta, T data) {
        this.meta = meta;
        this.data = data;
    }
}