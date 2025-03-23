package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ShoppingCartAIDTO implements Serializable {
    private String setmealName;
    private String dishName;
    private String dishFlavor;
}
