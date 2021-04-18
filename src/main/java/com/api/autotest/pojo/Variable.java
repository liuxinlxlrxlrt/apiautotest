package com.api.autotest.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Variable {
    private String name;
    private String value;
    private String remarks;
    private String reflectClass;
    private String reflectMethod;
    private String reflectValue;
}
