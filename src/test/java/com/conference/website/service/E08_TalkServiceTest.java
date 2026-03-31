package com.conference.website.service;

import com.conference.website.data.builders.*;
import com.conference.website.domain.Speaker;
import com.conference.website.domain.Tag;
import com.conference.website.domain.Talk;
import com.conference.website.domain.TalkLevel;
import com.conference.website.dto.*;
import com.conference.website.repository.SpeakerRepository;
import com.conference.website.repository.TagRepository;
import com.conference.website.repository.TalkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.conference.website.data.E08_ObjectMotherKt.createSpeakerRequest;
import static com.conference.website.data.E08_ObjectMotherKt.createTalkRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class E08_TalkServiceTest {

    @Autowired
    private SpeakerService speakerService;

    @Autowired
    private SpeakerRepository speakerRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TalkRepository talkRepository;

    @Autowired
    private TalkService talkService;


    @Test
    void shouldCreateTalkAndSpeakerCorrectly() {
        //Arrange
        var createSpeakerRequest = new CreateSpeakerRequest(
                "Ada Lovelace",
                "ada@example.com",
                "Analytical Engines",
                "Pioneer in computing"
        );
        SpeakerDto savedSpeakerDto = speakerService.createSpeaker(createSpeakerRequest);

        var createTalkRequest = new CreateTalkRequest(
                "Supercharging JVM tests",
                "Practical patterns to reduce noisy test code",
                TalkLevel.ADVANCED,
                60,
                savedSpeakerDto,
                List.of(),
                List.of()
        );
        var savedTalkDto = talkService.createTalk(createTalkRequest);
        var expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id(), createTalkRequest);

        //Act
        var talks = talkService.listTalks();

        //Assert
        assertEquals(1, talks.size());
        assertEquals(savedTalkDto, expectedTalkDto);
        assertThat(savedTalkDto.ratings()).isEmpty();

        assertThat(expectedTalkDto.primarySpeaker())
                .extracting("name", "email", "company", "bio")
                .containsExactly(
                        "Ada Lovelace",
                        "ada@example.com",
                        "Analytical Engines",
                        "Pioneer in computing"
                );
    }

    @Test
    void shouldCreateTalkAndSpeakerCorrectly_UsingTestBuilders() {
        //Arrange
        var createSpeakerRequest = CreateSpeakerRequestBuilder.aCreateSpeakerRequest().build();
        SpeakerDto savedSpeakerDto = speakerService.createSpeaker(createSpeakerRequest);

        //show that if withPrimarySpeaker is not invoked, an error is thrown
        var createTalkRequest = CreateTalkRequestBuilder.aCreateTalkRequest()
                //.withPrimarySpeaker(savedSpeakerDto)
                .build();
        var savedTalkDto = talkService.createTalk(createTalkRequest);

        //Act
        var expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id(), createTalkRequest);

        //Assert
        assertEquals(savedTalkDto, expectedTalkDto);
        var talks = talkService.listTalks();
        assertEquals(1, talks.size());

    }


    @Test
    void shouldCreateTalkAndSpeakerCorrectly_UsingKotlinMethods() {
        //Arrange
        var speakerRequest = createSpeakerRequest();
        SpeakerDto savedSpeakerDto = speakerService.createSpeaker(speakerRequest);

        //show that if withPrimarySpeaker is not invoked an error is thrown
        var createTalkRequest = createTalkRequest(savedSpeakerDto);

        //Act
        var savedTalkDto = talkService.createTalk(createTalkRequest);

        //Assert
        var expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id(), createTalkRequest);
        assertEquals(savedTalkDto, expectedTalkDto);

        var talks = talkService.listTalks();
        assertEquals(1, talks.size());

    }


    @Test
    void shouldCreateTalkAndSpeakerCorrectly_NoCopy() {
        //Arrange
        var primarySpeakerRequest = CreateSpeakerRequestBuilder.aCreateSpeakerRequest().withCompany("Tst AG").build();

        //requires .from(...) methods for all builders
        var coSpeakerRequest = CreateSpeakerRequestBuilder.from(primarySpeakerRequest)
                .withName("Sec Undo")
                .withEmail("sec.undo@example.com").build();

        SpeakerDto savedSpeakerDto = speakerService.createSpeaker(primarySpeakerRequest);
        SpeakerDto savedCoSpeakerDto = speakerService.createSpeaker(coSpeakerRequest);

        var createTalkRequest = CreateTalkRequestBuilder.aCreateTalkRequest()
                .withPrimarySpeaker(savedSpeakerDto)
                .withCoSpeakers(List.of(savedCoSpeakerDto))
                .build();

        //Act
        var savedTalkDto = talkService.createTalk(createTalkRequest);

        //Assert
        var expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id(), createTalkRequest);
        assertEquals(savedTalkDto.primarySpeaker(), expectedTalkDto.primarySpeaker());
        assertThat(savedTalkDto.coSpeakers())
                .hasSize(1)
                .containsExactly(savedCoSpeakerDto);
        assertThat(List.of(savedTalkDto.primarySpeaker().company(), savedCoSpeakerDto.company())).contains("Tst AG");



    }

    @Test
    void shouldCreateMultipleTalksWithEntityBuildersAndTemporaryVariables() {
        //Arrange
        Speaker primarySpeakerTalkOne = SpeakerBuilder.aSpeaker()
                .withName("Ada Lovelace")
                .withEmail("ada@example.com")
                .withCompany("Analytical Engines")
                .withBio("Pioneer in computing")
                .build();

        Speaker coSpeakerTalkOne = SpeakerBuilder.from(primarySpeakerTalkOne)
                .withName("Grace Hopper")
                .withEmail("grace@example.com")
                .withCompany("US Navy")
                .withBio("COBOL pioneer")
                .build();

        Speaker primarySpeakerTalkTwo = SpeakerBuilder.aSpeaker()
                .withName("Linus Torvalds")
                .withEmail("linus@example.com")
                .withCompany("Kernel Inc")
                .withBio("Created Linux")
                .build();

        Tag kotlinTag = TagBuilder.aTag()
                .withName("kotlin")
                .build();

        Tag testingTag = TagBuilder.aTag()
                .withName("testing")
                .build();

        Tag springTag = TagBuilder.aTag()
                .withName("spring")
                .build();

        Talk talkEntityOne = TalkBuilder.aTalk()
                .withTitle("Kotlin DSL Power")
                .withAbstractText("Scope fixtures without temporary variables")
                .withLevel(TalkLevel.INTERMEDIATE)
                .withDurationMinutes(45)
                .withPrimarySpeaker(primarySpeakerTalkOne)
                .withCoSpeaker(coSpeakerTalkOne)
                .withTag(kotlinTag)
                .withTag(testingTag)
                .build();

        Talk talkEntityTwo = TalkBuilder.aTalk()
                .withTitle("Spring Testing at Scale")
                .withAbstractText("Keep setup readable while growing scenarios")
                .withLevel(TalkLevel.ADVANCED)
                .withDurationMinutes(60)
                .withPrimarySpeaker(primarySpeakerTalkTwo)
                .withTags(List.of(springTag))
                .build();

        TalkGraphPersistence.persistGraph(
                List.of(talkEntityOne, talkEntityTwo),
                speakerRepository,
                tagRepository,
                talkRepository
        );


        //Act
        List<TalkDto> createdTalks = talkService.listTalks();

        //Assert
        TalkDto createdTalkOne = createdTalks.getFirst();
        TalkDto createdTalkTwo = createdTalks.getLast();

        assertThat(createdTalks).hasSize(2);
        assertThat(createdTalks)
                .extracting(TalkDto::title)
                .containsExactlyInAnyOrder("Kotlin DSL Power", "Spring Testing at Scale");

        assertThat(createdTalkTwo.primarySpeaker().name()).isEqualTo("Ada Lovelace");
        assertThat(createdTalkTwo.coSpeakers()).extracting(SpeakerDto::name).containsExactly("Grace Hopper");
        assertThat(createdTalkTwo.tags()).extracting(TagDto::name).containsExactlyInAnyOrder("kotlin", "testing");

        assertThat(createdTalkOne.primarySpeaker().name()).isEqualTo("Linus Torvalds");
        assertThat(createdTalkOne.tags()).extracting(TagDto::name).containsExactly("spring");
    }
}
