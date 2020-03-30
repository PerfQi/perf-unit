package com.perfxq.unit.demo.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class App implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;

    private String name;

    private String modifyTime;
}
