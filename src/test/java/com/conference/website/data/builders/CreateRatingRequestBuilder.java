package com.conference.website.data.builders;

import com.conference.website.dto.CreateRatingRequest;

public class CreateRatingRequestBuilder {

    private String reviewerName = "Test Reviewer";
    private Integer score = 5;
    private String comment = "Excellent talk";

    public static CreateRatingRequestBuilder aCreateRatingRequest() {
        return new CreateRatingRequestBuilder();
    }

    public CreateRatingRequestBuilder withReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
        return this;
    }

    public CreateRatingRequestBuilder withScore(Integer score) {
        this.score = score;
        return this;
    }

    public CreateRatingRequestBuilder withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public CreateRatingRequest build() {
        return new CreateRatingRequest(reviewerName, score, comment);
    }
}
