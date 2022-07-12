package com.servustech.eduson.features.confirmationtoken;

import com.servustech.eduson.exceptions.InvalidConfirmTokenException;
import com.servustech.eduson.security.constants.AuthConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveToken(ConfirmationToken confirmationToken) {
        confirmationToken.setCreatedOn(ZonedDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
    }

    public ConfirmationToken findConfirmationTokenByEmail(String email, String type) {
        return confirmationTokenRepository.findLastByUserEmailAndType(email, type)
                .orElseThrow(() -> new InvalidConfirmTokenException("invalid-confirmation-token"));
    }

    @Transactional
    public void deleteTokenAfterConfirmation(String confirmationToken) {
        confirmationTokenRepository.deleteConfirmationTokenByValue(confirmationToken);
    }

    public ConfirmationToken validateToken(String email, String confirmationTokenRequest, String type) {

        ConfirmationToken confirmationToken = findConfirmationTokenByEmail(email, type);
        if (type.equals("PR")) {
            // check if created_on is too old TODO
        }

        if (!confirmationToken.getValue().equals(confirmationTokenRequest)) {
            throw new InvalidConfirmTokenException("invalid-confirmation-token");
        }

        return confirmationToken;
    }
}
