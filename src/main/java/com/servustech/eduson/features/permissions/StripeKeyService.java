package com.servustech.eduson.features.permissions;

import com.servustech.eduson.features.general.GeneralService;
// import com.servustech.eduson.features.general.GeneralDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StripeKeyService {
  private final GeneralService generalService;

  public String getApiKey() {
    var testMode = true;

    var testModeSetting = generalService.getKey("stripeTestMode", "", false);
    if (testModeSetting != null && testModeSetting.getContent().equals("no")) {
      testMode = false;
    }
    var setting = generalService.getKey(testMode ? "stripeTestKeySecret" : "stripeTestSecret", "", false);
    return setting.getContent();  // "sk_test_51InNBcGvE7shCYYHFuUq7CJUJkpPP2LqffQRcUMNk5w7IBa9VVQhe3UJlgYTro1Oqp7INtFSVtAVnvlQOg3tLJAX00Wy0tZjGm";
  }
  public List<String> getEndpointSecrets() {
    return generalService.page("endpointSecret", "", Pageable.unpaged(), true).getContent().stream().map(setting -> setting.getContent()).collect(Collectors.toList());
  }
}
