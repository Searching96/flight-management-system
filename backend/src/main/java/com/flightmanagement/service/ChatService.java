package com.flightmanagement.service;

import com.flightmanagement.dto.ChatboxDto;

import java.util.List;

public interface ChatService {

    List<ChatboxDto> getAllChatboxes();
}