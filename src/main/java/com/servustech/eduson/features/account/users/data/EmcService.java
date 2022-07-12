package com.servustech.eduson.features.account.users.data;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class EmcService {
  private final EmcRepository emcRepository;
  public Optional<Emc> findById(Long id){
    return emcRepository.findById(id);
  }
  public Emc createWithId(Long id) {
    var ret = new Emc();
    ret.setUserId(id);
    emcRepository.save(ret); // TODO
    return ret;
  }
  public void save(Emc emc) {
    emcRepository.save(emc);
  }
}
