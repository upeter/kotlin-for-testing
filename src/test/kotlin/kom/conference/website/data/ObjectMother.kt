package kom.conference.website.data

import kom.conference.website.dto.RatingDto
import kom.conference.website.dto.SpeakerDto
import kom.conference.website.dto.TagDto
import kom.conference.website.dto.ViewCountResponse
import java.time.LocalDateTime
import kom.conference.website.dto.CreateRatingRequest
import kom.conference.website.dto.CreateSpeakerRequest
import kom.conference.website.dto.ScheduleSlotRequest

fun sampleSpeakerDto() = SpeakerDto(
    id = 1L,
    name = "Ada Lovelace",
    email = "ada@example.com",
    company = "Analytical Engines",
    bio = "Pioneer in computing"
)

fun sampleTagDto() = TagDto(
    id = 1L,
    name = "java"
)

fun sampleRatingDto() = RatingDto(
    id = 1L,
    reviewerName = "Test Reviewer",
    score = 5,
    comment = "Excellent talk"
)

fun sampleViewCountResponse() = ViewCountResponse(
    talkId = 1L,
    views = 100L
)

fun sampleScheduleSlotRequest() = ScheduleSlotRequest(
    roomName = "Main Hall",
    startTime = LocalDateTime.of(2026, 3, 16, 9, 0),
    endTime = LocalDateTime.of(2026, 3, 16, 10, 0)
)

fun sampleCreateSpeakerRequest() = CreateSpeakerRequest(
    name = "Ada Lovelace",
    email = "ada@example.com",
    company = "Analytical Engines",
    bio = "Pioneer in computing"
)

fun sampleCreateRatingRequest() = CreateRatingRequest(
    reviewerName = "Test Reviewer",
    score = 5,
    comment = "Excellent talk"
)