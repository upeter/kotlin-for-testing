package com.conference.website.data;

import com.conference.website.api.dto.CreateTalkRequest;
import com.conference.website.api.dto.ScheduleSlotRequest;
import com.conference.website.domain.TalkLevel;

//public class CreateTalkRequestBuilder {
//
//        private String title = "Supercharging JVM tests";
//        private String description = "Practical patterns to reduce noisy test code";
//        private TalkLevel level = TalkLevel.ADVANCED;
//        private int duration = 60;
//        private UUID speakerId = UUID.randomUUID();
//        private List<String> tags = List.of();
//        private List<String> prerequisites = List.of();
//        private ScheduleSlotRequest scheduleSlot =
//                new ScheduleSlotRequest(
//                        "Room B",
//                        LocalDateTime.of(2026, 4, 8, 14, 0),
//                        LocalDateTime.of(2026, 4, 8, 15, 0)
//                );
//
//        public CreateTalkRequestBuilder withTitle(String title) {
//                this.title = title;
//                return this;
//        }
//
//        public CreateTalkRequestBuilder withDuration(int duration) {
//                this.duration = duration;
//                return this;
//        }
//
//        public CreateTalkRequest build() {
//                return new CreateTalkRequest(
//                        title,
//                        description,
//                        level,
//                        duration,
//                        speakerId,
//                        tags,
//                        prerequisites,
//                        scheduleSlot
//                );
//        }
//}