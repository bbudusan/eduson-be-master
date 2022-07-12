package com.servustech.eduson.features.confirmationtoken;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.servustech.eduson.features.account.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "confirmation_tokens")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String value;

    @CreatedDate
    private ZonedDateTime createdOn;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    private String type;
    private Boolean used;

    public ConfirmationToken(String value, User account, String type) {
        this.value = value;
        this.user = account;
        this.type = type;
    }
}
