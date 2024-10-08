package com.potatotech.entitygenerator.model;

import lombok.Data;

@Data
public class FrontEndProperties {

    private String label = "";
    private int size = 12;
    private boolean hidden = true;
    private int order = 0;
    private String guidance = "";
    private String reference = "";
    private boolean enableFieldsFilter = false;
}
