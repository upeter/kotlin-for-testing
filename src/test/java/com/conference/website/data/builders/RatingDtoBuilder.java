package com.conference.website.data.builders;

import com.conference.website.dto.RatingDto;

public class RatingDtoBuilder {

    private Long id = 1L;
    private String reviewerName = "Test Reviewer";
    private Integer score = 5;
    private String comment = "Excellent talk";

    public static RatingDtoBuilder aRatingDto() {
        return new RatingDtoBuilder();
    }

    public RatingDtoBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public RatingDtoBuilder withReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
        return this;
    }

    public RatingDtoBuilder withScore(Integer score) {
        this.score = score;
        return this;
    }

    public RatingDtoBuilder withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public RatingDto build() {
        return new RatingDto(id, reviewerName, score, comment);
    }
}
