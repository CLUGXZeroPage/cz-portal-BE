package com.example.czportalpage.info.service;

import com.example.czportalpage.common.exception.handler.GeneralHandler;
import com.example.czportalpage.common.response.status.ErrorCode;
import com.example.czportalpage.info.dto.InfoPostDto;
import com.example.czportalpage.info.dto.InfoRequestDto;
import com.example.czportalpage.info.entity.Info;
import com.example.czportalpage.info.repository.InfoRepository;
import com.example.czportalpage.info.service.jsonParse.LevelData;
import com.example.czportalpage.info.service.jsonParse.userInfoRoot;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.czportalpage.info.service.jsonParse.jsonFetcher.fetchJson;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfoService {
    private final InfoRepository infoRepository;

    @Transactional
    public Long createInfo(InfoPostDto infoPostDto) {

        log.info("유저 정보를 받았습니다 : {}", infoPostDto.getUsername());

        // 1. username이 null이거나 비어 있다면 예외 발생
        if (infoPostDto.getUsername() == null || infoPostDto.getUsername().trim().isEmpty()) {
            throw new GeneralHandler(ErrorCode._BAD_REQUEST);
        }

        // 2. username 중복 검사
        Optional<Info> existingInfo = infoRepository.findByUsername(infoPostDto.getUsername());
        if (existingInfo.isPresent()) {
            throw new GeneralHandler(ErrorCode._BAD_REQUEST);
        }

        // 3. 새로운 Info 객체 생성 및 기본 정보 설정
        Info newInfo = new Info();
        newInfo.setUsername(infoPostDto.getUsername());
        newInfo.setNickname(infoPostDto.getNickname());

        // 4. solved.ac API를 호출하여 실제 유저 정보가 존재하는지 확인
        try {
            String urlString = "https://solved.ac/api/v3/search/user?query=" + newInfo.getUsername();
            String userInfo = fetchJson(urlString);

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                userInfoRoot userInfoRoot = objectMapper.readValue(userInfo, userInfoRoot.class);
                // 검색 결과가 없는 경우 예외 발생
                if (userInfoRoot.getItems() == null || userInfoRoot.getItems().isEmpty()) {
                    throw new GeneralHandler(ErrorCode.MEMBER_NOT_FOUND);
                }
                // 첫 번째 항목의 정보를 기반으로 초기 정보 설정
                int rating = userInfoRoot.getItems().get(0).getRating();
                int solvedCount = userInfoRoot.getItems().get(0).getSolvedCount();
                newInfo.setInitRating(String.valueOf(rating));
                newInfo.setInitSolvedCount(String.valueOf(solvedCount));

                // 현재 정보에도 동일하게 설정 (null 변환 방지)
                newInfo.setCurrentRating(String.valueOf(rating));
                newInfo.setCurrentSolvedCount(String.valueOf(solvedCount));
            } catch (IOException e) {
                e.printStackTrace();
                throw new GeneralHandler(ErrorCode._BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralHandler(ErrorCode._BAD_REQUEST);
        }

        // 5. solved.ac의 문제 통계 API 호출 및 레벨별 해결 수 저장
        try {
            String urlString = "https://solved.ac/api/v3/user/problem_stats?handle=" + newInfo.getUsername();
            String levelData = fetchJson(urlString);
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                List<LevelData> levels = objectMapper.readValue(levelData, new TypeReference<List<LevelData>>() {});
                if (levels == null || levels.isEmpty()) {
                    throw new GeneralHandler(ErrorCode._BAD_REQUEST);
                }
                StringBuilder solvedCountByLevelArray = new StringBuilder();
                for (LevelData level : levels) {
                    solvedCountByLevelArray.append(level.getSolved()).append(",");
                }
                newInfo.setInitSolvedCountByLevelArray(solvedCountByLevelArray.toString());
                newInfo.setCurrentSolvedCountByLevelArray(solvedCountByLevelArray.toString());
            } catch (IOException e) {
                e.printStackTrace();
                throw new GeneralHandler(ErrorCode._BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralHandler(ErrorCode._BAD_REQUEST);
        }

        // 6. 저장소에 Info 저장
        Info savedInfo = infoRepository.save(newInfo);
        return infoRepository.save(savedInfo).getInfoId();
    }

    @Scheduled(fixedRate = 3600000) // 1시간 마다 업데이트
    public void updateInfo(){

        log.info("업데이트를 시작합니다");

        for(Info info : infoRepository.findAll()) {
            try {
                String urlString = "https://solved.ac/api/v3/search/user?query=" + info.getUsername();  // 사용하고자 하는 url로 변경
                String userInfo = fetchJson(urlString);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    userInfoRoot userInfoRoot = objectMapper.readValue(userInfo, userInfoRoot.class);
                    info.setCurrentRating(String.valueOf(userInfoRoot.getItems().get(0).getRating()));
                    info.setCurrentSolvedCount(String.valueOf(userInfoRoot.getItems().get(0).getSolvedCount()));

                    log.info("업데이트 된 유저 정보 : {}", info.getUsername());
                    log.info("{}", info.getCurrentRating());
                    log.info("{}", info.getCurrentSolvedCount());
                    log.info("-----------------------------------------------");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String urlString = "https://solved.ac/api/v3/user/problem_stats?handle=" + info.getUsername();  // 사용하고자 하는 url로 변경
                String levelData = fetchJson(urlString);
                ObjectMapper objectMapper = new ObjectMapper();

                try {
                    List<LevelData> levels = objectMapper.readValue(levelData, new TypeReference<List<LevelData>>() {});
                    StringBuilder solvedCountByLevelArray = new StringBuilder();
                    for (LevelData level : levels) {
                        solvedCountByLevelArray.append(level.getSolved()).append(",");
                    }
                    info.setCurrentSolvedCountByLevelArray(solvedCountByLevelArray.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            infoRepository.save(info);
        }
    }

    public InfoRequestDto findById(Long infoId){
        Info info = infoRepository.findById(infoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Info with ID " + infoId + " not found"));
        return new InfoRequestDto(info);
    }

    public List<InfoRequestDto> getAllInfos() {
        // 데이터베이스에서 모든 Info 엔티티 가져오기
        List<Info> infos = infoRepository.findAll();
        // List<Info>를 List<InfoDto>로 변환
        return infos.stream().map(InfoRequestDto::new).collect(Collectors.toList());
    }
}
