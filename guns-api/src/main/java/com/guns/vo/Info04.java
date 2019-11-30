package com.guns.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Info04 implements Serializable {
    private static final long serialVersionUID = 1L;

    String biography;

    DirictorVO director;

    List<ActorVO> actors;
}
