package com.sharesphere.dto.response;

import com.sharesphere.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private Double rating;
    private Integer ratingCount;
    private String avatarUrl;
    private String address;
    private LocalDateTime createdAt;
}
