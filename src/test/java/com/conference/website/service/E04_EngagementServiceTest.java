package com.conference.website.service;

import com.conference.website.data.builders.CreateSpeakerRequestBuilder;
import com.conference.website.data.builders.CreateTalkRequestBuilder;
import com.conference.website.dto.EngagementCountDto;
import com.conference.website.dto.EngagementUpdateRequest;
import com.conference.website.dto.SpeakerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class E04_EngagementServiceTest {

   @Autowired
   private SpeakerService speakerService;

   @Autowired
   private TalkService talkService;

   @Autowired
   private EngagementService engagementService;


   @Test
   void shouldRecordEngagementAndReadCounts() {
      //Arrange
      var createSpeakerRequest = CreateSpeakerRequestBuilder
         .aCreateSpeakerRequest().build();
      SpeakerDto savedSpeakerDto = speakerService
         .createSpeaker(createSpeakerRequest);
      var createTalkRequest = CreateTalkRequestBuilder
         .aCreateTalkRequest().withPrimarySpeaker(savedSpeakerDto).build();
      var talk = talkService.createTalk(createTalkRequest);

      var engagement1 = new EngagementUpdateRequest(true, true, false);
      var engagement2 = new EngagementUpdateRequest(false, true, true);

      //Act
      StepVerifier.create( //<- complex testing abstraction
            Mono.zip( //<- Mono magic to combine multiple Mono
                  engagementService.recordEngagement(talk.id(), engagement1),
                  engagementService.recordEngagement(talk.id(), engagement2)
               )
               .flatMap(recorded ->
                  engagementService.getCurrentEngagement(talk.id())
                  .map(current ->
                     //accumulated results from nested calls
                     List.of(recorded.getT1(), recorded.getT2(), current)))
         )
         //Assert
         .assertNext(result -> {
            assertThat(result).hasSize(3);
            EngagementCountDto current = result.get(2); //what is 2?
            assertThat(current.views()).isEqualTo(1L);
            assertThat(current.likes()).isEqualTo(2L);
            assertThat(current.attends()).isEqualTo(1L);
         })
         .verifyComplete(); //<- won't run if verifyComplete() is not called
   }

}
