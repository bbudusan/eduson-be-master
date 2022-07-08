package com.servustech.eduson.features.account.users.data;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class IndividualService {
  private final IndividualRepository individualRepository;
  public Optional<Individual> findById(Long id){
    return individualRepository.findById(id);
  }
  public Individual createWithId(Long id) {
    var ret = new Individual();
    ret.setUserId(id);
    // individualRepository.save(ret);
    return ret;
  }
  public void save(Individual individual) {
    individualRepository.save(individual);
  }
}
