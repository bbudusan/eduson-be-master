package com.servustech.eduson.features.account.users.data;
import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.account.users.dto.VatDataDto;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ch.digitalfondue.vatchecker.EUVatChecker;
import ch.digitalfondue.vatchecker.EUVatCheckResponse;

@AllArgsConstructor
@Component
public class LegalService {
  private final LegalRepository legalRepository;
  public Optional<Legal> findById(Long id){
    return legalRepository.findById(id);
  }
  public Legal createWithId(Long id) {
    var ret = new Legal();
    ret.setUserId(id);
    return ret;
  }
  public void save(Legal legal) {
    VatDataDto vatDataDto = checkVat(legal.getCui(), null);
    if (vatDataDto.getIsValid()) {
      // legal.setCompany(vatDataDto.getCompanyName());
      // legal.setAddress(vatDataDto.getCompanyAddress());
      legalRepository.save(legal);
    } else {
      throw new CustomException("invalid-vat-number");
    }
  }
  public VatDataDto checkVat(String code, String country) {
    Pattern pattern = Pattern.compile("^([a-zA-Z]{2})?(\\d+)$");
    Matcher matcher = pattern.matcher(code);
    if (matcher.find()) {
      code = matcher.group(2);
      if (country == null && matcher.group(1) != null && !matcher.group(1).equals("")) {
        country = matcher.group(1);
      }
    }
    if (country == null) {
      country = "RO";
    }
    EUVatCheckResponse resp = EUVatChecker.doCheck(country, code);
    return VatDataDto.builder()
      .cui(country + code)
      .companyName(resp.getName())
      .companyAddress(resp.getAddress())
      .isValid(resp.isValid())
    .build();
  }
}
