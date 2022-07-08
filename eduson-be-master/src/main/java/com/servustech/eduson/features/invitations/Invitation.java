package com.servustech.eduson.features.invitation;

import com.servustech.eduson.features.account.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

import org.springframework.data.domain.Page;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "invitations")
public class Invitation {
  @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

  private String email;
  private String status;
	@OneToOne
	@JoinColumn(name = "invited_by", nullable = false)
  private User invitedBy;
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
  private User user;
}
