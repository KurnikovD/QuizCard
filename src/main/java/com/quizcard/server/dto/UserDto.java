package com.quizcard.server.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {
    private String telegramId;
    private String username;
    private String password;
}
