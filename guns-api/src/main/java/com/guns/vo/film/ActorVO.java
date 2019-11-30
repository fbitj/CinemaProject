package com.guns.vo.film;

import lombok.Data;

import java.io.Serializable;

@Data
public class ActorVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String imgAddress;
    private String directorName;
    private String roleName;

}
