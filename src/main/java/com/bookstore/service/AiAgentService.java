package com.bookstore.service;

import com.bookstore.dto.request.AiChatRequest;
import com.bookstore.dto.response.AiChatResponse;

public interface AiAgentService {
    AiChatResponse chat(AiChatRequest request);
}