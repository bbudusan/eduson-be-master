package com.servustech.eduson.features.products.webinars;

import com.amazonaws.services.kms.model.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.servustech.eduson.features.account.UserService;
import com.servustech.eduson.features.products.webinars.dto.WchatMessageDto;
import com.servustech.eduson.features.products.webinars.dto.WchatDto;
import com.servustech.eduson.features.products.webinars.dto.Wchat2Dto;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@Service
public class WchatService {
  private final WchatRepository wchatRepository;
  private final UserService userService;

  public WchatMessage create(WchatMessageDto msg) {
    var sender = msg.getSenderId() == null ? null : userService.findById(msg.getSenderId());
    WchatMessage wchatMessage = WchatMessage.builder()
        .productId(msg.getProductId())
        .dest(msg.getDest())
        .sender(sender)
        .message(msg.getMessage())
        .timestamp(msg.getTimestamp())
        .session(msg.getSession())
        .hidden(false) // TODO chat moderating
        .build();
    return wchatRepository.save(wchatMessage);

  }

  public List<WchatMessageDto> getAll(String dest, Long productId, boolean all) {
    return (all == true ? 
        wchatRepository.findAllByDestAndProductId(dest, productId) : 
        wchatRepository.findAllByDestAndProductIdAndHidden(dest, productId, false))
      .stream().map(chat -> WchatMessageDto.builder()
        .id(chat.getId())
        .productId(chat.getProductId())
        .dest(chat.getDest())
        .senderId(chat.getSender() == null ? null : chat.getSender().getId())
        .timestamp(chat.getTimestamp())
        .senderName(chat.getSender() == null ? "Guest" : chat.getSender().getFullName())
        .message(chat.getMessage())
        .hidden(chat.getHidden())
        .session(chat.getSession())
        .build()).collect(Collectors.toList());
  }
  public List<WchatMessageDto> getAll(String dest, String session) {
      return wchatRepository.findAllByDestAndSession(dest, session)
      .stream().map(chat -> WchatMessageDto.builder()
        .id(chat.getId())
        .productId(chat.getProductId())
        .dest(chat.getDest())
        .senderId(chat.getSender() == null ? null : chat.getSender().getId())
        .timestamp(chat.getTimestamp())
        .senderName(chat.getSender() == null ? "Guest" : chat.getSender().getFullName())
        .message(chat.getMessage())
        .hidden(chat.getHidden())
        .session(chat.getSession())
        .build()).collect(Collectors.toList());
  }
  public Page<WchatDto> getAnonymous(String filterByName, Pageable pageable) {
    return wchatRepository.findFirstByDestAndSessionIsNotNullGroupBySessionOrderByTimestamp("h", pageable);
  }
  public Page<Wchat2Dto> getLoggedInUserChats(String filterByName, Pageable pageable) {
    return wchatRepository.findFirstByDestAndSessionIsNullGroupByProductIdOrderByTimestamp("h", pageable);
  }

  @Transactional
  public Boolean hide(Long id) {
    WchatMessage wchatMessage = wchatRepository.findById(id).orElseThrow(() -> new NotFoundException("message-w-id-not-exist"));
    wchatMessage.setHidden(!wchatMessage.getHidden());
    return wchatMessage.getHidden();
  }
  public void deleteAnonymous(String session) {
    var wchat = wchatRepository.findAllByDestAndSession("h", session);
    wchatRepository.deleteAll(wchat);
  }
}
