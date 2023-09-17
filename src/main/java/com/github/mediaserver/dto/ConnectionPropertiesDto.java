package com.github.mediaserver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionPropertiesDto {
    private String role;
    private String type;
    private Boolean onlyPlayWithSubscribers;

}
