package com.guns.vo.film;

import lombok.Data;

import java.io.Serializable;

@Data
public class SourceInfoVO implements Serializable {
    private static final long serialVersionUID = 1439240249332256681L;

    private String sourceId;

    private Boolean active = false;

    private String sourceName;
}
