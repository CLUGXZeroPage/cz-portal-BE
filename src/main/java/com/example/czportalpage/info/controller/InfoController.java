package com.example.czportalpage.info.controller;

import com.example.czportalpage.common.response.ApiResponse;
import com.example.czportalpage.info.dto.InfoPostDto;
import com.example.czportalpage.info.dto.InfoRequestDto;
import com.example.czportalpage.info.service.InfoService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Getter @Setter
@RestController
@RequestMapping("/api/infos")
@RequiredArgsConstructor
public class InfoController {

    private final InfoService infoService;

    @PostMapping("/post")
    public ApiResponse<String> postInfo(@RequestBody @Validated InfoPostDto infoPostDto){

        Long infoId = infoService.createInfo(infoPostDto);

        return ApiResponse.onSuccess(infoId + "성공!");
    }

    @PatchMapping("/patch")
    public ApiResponse<String> patchInfo() {
        infoService.updateInfo();
        return ApiResponse.onSuccess("업데이트 시작!");
    }
    @GetMapping("/all")
    public ApiResponse<List<InfoRequestDto>> getAllInfos() {

        List<InfoRequestDto> infos = infoService.getAllInfos();

        return ApiResponse.onSuccess(infos);
    }
}