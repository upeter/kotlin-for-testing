package com.conference.website.dto

import com.conference.website.domain.Talk

fun Talk.toDto():TalkDto = DtoConversions.toDto(this)