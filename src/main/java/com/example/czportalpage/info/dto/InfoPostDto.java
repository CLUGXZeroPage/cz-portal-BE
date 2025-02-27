package com.example.czportalpage.info.dto;

//객체를 새로 만들때 사용하는 DTO

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InfoPostDto {

    @NotBlank
    @Size(max = 20)
    private String username;
    @NotBlank
    @Size(max = 10)
    private String nickname;
}