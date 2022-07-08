package com.servustech.eduson.features.products.webinars;

import java.time.ZonedDateTime;

import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.account.UserService;
import com.servustech.eduson.features.products.webinars.dto.WchatMessageDto;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;

import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;

@Controller
@AllArgsConstructor
public class WebSocketChatController {

  private final UserService userService;
  private final WchatService wchatService;

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/resume/{dest}")
  @SendTo("/start/initial/{dest}/")
  public WchatMessageDto processMessage(
    @Header(required = false, name = "username") String username, 
    @DestinationVariable("dest") String dest, 
    @Payload WchatMessageDto msg) {
    var user = msg.getSenderId() == null ? null : userService.findById(msg.getSenderId());
    System.out.println("dest");
    System.out.println(dest);
    if (user != null && !user.getUsername().equals(username)) {
      System.out.println(user.getUsername());
      System.out.println(username);
      throw new CustomException("message-in-others-name");
    }
    // TODO check if the user has access to the webinar and if yes, if the webinar
    // is live.
    msg.setTimestamp(ZonedDateTime.now());
    // TODO we should get productId and dest from the path instead if payload, as these were checked at subscribe
    WchatMessage wchatMessage = wchatService.create(msg);

    msg.setId(wchatMessage.getId());
    msg.setSession(wchatMessage.getSession());
    msg.setSenderName(user == null ? "Guest" : user.getFullName());

    // TODO we should get productId and dest from the path instead if payload, as these were checked at subscribe
    System.out.println(msg);
    if (msg.getSession() != null && msg.getSession().length() == 20) {
      messagingTemplate.convertAndSend("/start/initial/" + msg.getDest() + "/" + msg.getSession(), msg);
    } else {
      messagingTemplate.convertAndSend("/start/initial/" + msg.getDest() + "/" + msg.getProductId(), msg);
    }
    // var aaa = messageConverter.toMessage(msg, null);
    return msg;
  }
}