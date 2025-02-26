package com.example.czportalpage.info.service.jsonParse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class userInfoItem {

    @Getter @Setter
    private String handle;
    @Getter @Setter
    private String bio;
    @Getter @Setter
    private boolean verified;
    @Getter @Setter
    private String badgeId;
    @Getter @Setter
    private String backgroundId;
    @Getter @Setter
    private String profileImageUrl;
    @Getter @Setter
    private Integer solvedCount;
    @Getter @Setter
    private Integer voteCount;

    private int userClass;
    @Getter @Setter
    private String classDecoration;
    @Getter @Setter
    private Integer rivalCount;
    @Getter @Setter
    private Integer reverseRivalCount;
    @Getter @Setter
    private Integer tier;
    @Getter @Setter
    private Integer rating;
    @Getter @Setter
    private Integer ratingByProblemsSum;
    @Getter @Setter
    private Integer ratingByClass;
    @Getter @Setter
    private Integer ratingBySolvedCount;
    @Getter @Setter
    private Integer ratingByVoteCount;
    @Getter @Setter
    private Integer arenaTier;
    @Getter @Setter
    private Integer arenaRating;
    @Getter @Setter
    private Integer arenaMaxTier;
    @Getter @Setter
    private Integer arenaMaxRating;
    @Getter @Setter
    private Integer arenaCompetedRoundCount;
    @Getter @Setter
    private Integer maxStreak;
    @Getter @Setter
    private Integer coins;
    @Getter @Setter
    private Integer stardusts;
    @Getter @Setter
    private String joinedAt;
    @Getter @Setter
    private String bannedUntil;
    @Getter @Setter
    private String proUntil;
    @Getter @Setter
    private Integer rank;

    @JsonProperty("class")
    public int getUserClass() {
        return userClass;
    }

    @JsonProperty("class")
    public void setUserClass(int userClass) {
        this.userClass = userClass;
    }
}