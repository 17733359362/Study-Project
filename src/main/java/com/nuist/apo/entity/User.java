package com.nuist.apo.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author wtx
 * @Date 2024/5/31
 */
@Data
@ToString
public class User implements Serializable {
    private static final long serialVersionUID = 2242721246659161303L;

    private Long id;
    private String name;
}
