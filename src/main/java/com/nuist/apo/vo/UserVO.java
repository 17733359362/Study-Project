package com.nuist.apo.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author wtx
 * @Date 2024/5/31
 */
@Data
@ToString
public class UserVO implements Serializable {
    private static final long serialVersionUID = -1447847401808503458L;

    private Long id;
    private String name;
}
