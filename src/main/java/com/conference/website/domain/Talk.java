package com.conference.website.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "talks")
public class Talk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4000)
    private String abstractText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TalkLevel level;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "primary_speaker_id", nullable = false)
    private Speaker primarySpeaker;

    @ManyToMany
    @JoinTable(
            name = "talk_co_speakers",
            joinColumns = @JoinColumn(name = "talk_id"),
            inverseJoinColumns = @JoinColumn(name = "speaker_id")
    )
    private Set<Speaker> coSpeakers = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "talk_tags",
            joinColumns = @JoinColumn(name = "talk_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "talk", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_slot_id", unique = true)
    private ScheduleSlot scheduleSlot;

    @Version
    private Long version;

    protected Talk() {
    }

    public Talk(String title, String abstractText, TalkLevel level, Integer durationMinutes, Speaker primarySpeaker) {
        this.title = title;
        this.abstractText = abstractText;
        this.level = level;
        this.durationMinutes = durationMinutes;
        this.primarySpeaker = primarySpeaker;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public void addRating(Rating rating) {
        rating.setTalk(this);
        ratings.add(rating);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public TalkLevel getLevel() {
        return level;
    }

    public void setLevel(TalkLevel level) {
        this.level = level;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Speaker getPrimarySpeaker() {
        return primarySpeaker;
    }

    public void setPrimarySpeaker(Speaker primarySpeaker) {
        this.primarySpeaker = primarySpeaker;
    }

    public Set<Speaker> getCoSpeakers() {
        return coSpeakers;
    }

    public void setCoSpeakers(Set<Speaker> coSpeakers) {
        this.coSpeakers = coSpeakers;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public ScheduleSlot getScheduleSlot() {
        return scheduleSlot;
    }

    public void setScheduleSlot(ScheduleSlot scheduleSlot) {
        this.scheduleSlot = scheduleSlot;
    }

    public Long getVersion() {
        return version;
    }
}
