package com.servustech.eduson.config;

import com.servustech.eduson.features.products.courses.Advert;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.MutablePair;
import java.util.Hashtable;

@Configuration
public class StreamHashConfig {
  @Bean
  public Hashtable<Long, MutablePair<MutablePair<Process,Process>,Advert>> courseProcesses() {
    return new Hashtable<Long, MutablePair<MutablePair<Process,Process>,Advert>>();
  }
  @Bean
  public Hashtable<Long, MutableTriple<MutablePair<Process,Process>,MutablePair<Long,Long>,Advert>> webinarProcesses() {
    return new Hashtable<Long, MutableTriple<MutablePair<Process,Process>,MutablePair<Long,Long>,Advert>>();
  }
}
