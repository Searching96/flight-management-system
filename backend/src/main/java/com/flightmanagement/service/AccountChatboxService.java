package com.flightmanagement.service;

import com.flightmanagement.dto.AccountChatboxDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AccountChatboxService {

    List<AccountChatboxDto> getAllAccountChatboxes();

    AccountChatboxDto getAccountChatboxById(Integer accountId, Integer chatboxId);

    List<AccountChatboxDto> getAccountChatboxesByAccountId(Integer accountId);

    AccountChatboxDto createAccountChatbox(AccountChatboxDto accountChatboxDto);

    AccountChatboxDto updateAccountChatbox(Integer accountId, Integer chatboxId, AccountChatboxDto accountChatboxDto);

    void deleteAccountChatbox(Integer accountId, Integer chatboxId);

    void updateLastVisitTime(Integer accountId, Integer chatboxId, LocalDateTime lastVisitTime);

    Long getUnreadMessageCount(Integer accountId, Integer chatboxId);

    void updateLastVisitTime(Integer accountId, Integer chatboxId);

    Map<Integer, Long> getUnreadCountsForAllChatboxes(Integer accountId);
}
