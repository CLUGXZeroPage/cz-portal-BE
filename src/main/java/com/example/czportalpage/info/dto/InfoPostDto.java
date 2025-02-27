package com.example.czportalpage.info.dto;

//객체를 새로 만들때 사용하는 DTO

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InfoPostDto {

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Pattern(regexp = "^[가-힣]{1,6}$|^[a-zA-Z]{1,10}$", message = "한글은 최대 6자, 영어는 최대 10자까지 허용됩니다.")
    private String nickname;
}