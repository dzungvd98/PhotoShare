package com.dev.photoshare.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSearchRequest {
    private String searchKey;

    private String status;

    private String roleName;
}
