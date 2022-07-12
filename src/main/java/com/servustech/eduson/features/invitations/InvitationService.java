package com.servustech.eduson.features.invitation;

import com.amazonaws.services.kms.model.NotFoundException;

import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.users.dto.UserDto;
import com.servustech.eduson.features.account.UserService;
import com.servustech.eduson.features.account.mapper.UserMapper;
import com.servustech.eduson.security.auth.AuthService;
import com.servustech.eduson.features.account.AccountStatus;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import lombok.AllArgsConstructor;

import javax.transaction.Transactional;
import java.util.stream.Collectors;
import java.util.List;

@Service
@AllArgsConstructor
public class InvitationService {

  private final UserService userService;
  private final InvitationRepository invitationRepository;
  private final UserMapper userMapper;
	private final AuthService authService;

  public Page<InvitationDto> getInvitedBy(User user, Pageable pageable) {
    var page = invitationRepository.findAllByInvitedBy(user, pageable);
    var list = page.getContent().stream().map(e -> convert(e, user, e.getUser())).collect(Collectors.toList());
    return new PageImpl<>(list, pageable, page.getTotalElements());
  }
  public Page<InvitationDto> getSponsorsOf(User user, Pageable pageable) {
    var page = invitationRepository.findAllByUser(user, pageable);
    var list = page.getContent().stream().map(e -> convert(e, e.getInvitedBy(), user)).collect(Collectors.toList());
    return new PageImpl<>(list, pageable, page.getTotalElements());
  }
  @Transactional
  public InvitationDto invite(User user, InvitationDto invitationDto) {
    User invitedUser = null;
    try {
      invitedUser = userService.findByEmail(invitationDto.getEmail());
    } catch (NotFoundException e) {  
      invitedUser = userMapper.signUpRequestToUser(UserDto.builder()
        .firstName("xx")
        .lastName("yy")
        .username(invitationDto.getEmail())
        .email(invitationDto.getEmail())
        .password("<none>")
      .build());
      invitedUser.setAccountStatus(AccountStatus.STARTED);
      invitedUser = authService.saveUser(invitedUser);
      authService.sendWelcomeEmail(invitedUser);
    }
    // if email does not exist, create the user and send an invitation email for him.
    // And maybe send the sponsoring invitation even if it exists.
    // TODO
    Invitation invitation = Invitation.builder()
      .invitedBy(user)
      .email(invitationDto.getEmail())
      .user(invitedUser)
      .status("INITIALIZED")
    .build();
    invitation = invitationRepository.save(invitation);
    return convert(invitation, user, invitedUser);
  }
  @Transactional
  public InvitationDto accept(User user, InvitationDto invitationDto) {
    var invitation = invitationRepository.findById(invitationDto.getId()).orElseThrow(() -> new NotFoundException("invitation-not-found"));
    invitation.setStatus("ACCEPTED");
    return convert(invitation, invitation.getInvitedBy(), user);
  }
  @Transactional
  public InvitationDto reject(User user, InvitationDto invitationDto) {
    var invitation = invitationRepository.findById(invitationDto.getId()).orElseThrow(() -> new NotFoundException("invitation-not-found"));
    invitation.setStatus("REJECTED");
    return convert(invitation, invitation.getInvitedBy(), user);
  }

  private InvitationDto convert(Invitation invitation, User user, User invitedUser) {
    boolean visible = user.isAdmin() || invitation.getStatus().equals("ACCEPTED");
    return InvitationDto.builder()
      .id(invitation.getId())
      .invitedBy(user.getId())
      .invitedByFullName(user.getFullName())
      .email(invitation.getEmail())
      .status(invitation.getStatus())
      .userId(visible ? invitedUser.getId() : null)
      .fullName(visible ? invitedUser.getFullName() : null)
      .file(visible ? invitedUser.getProfileImage() : null)
    .build();
  }

  public Invitation findById2(Long id) {
    return invitationRepository.findById(id).orElseThrow(() -> new NotFoundException("invitation-not-found"));
  }

  public List<Invitation> findAllByInvitedBy(User user) {
    return invitationRepository.findAllByInvitedBy(user, null).getContent();
  }
  
}


