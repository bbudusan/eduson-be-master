package com.servustech.eduson.features.permissions.subscriptions;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.products.webinars.WebinarRepository;
import com.servustech.eduson.features.categories.modules.ModuleWithContentRepository;
import com.servustech.eduson.features.products.courses.CourseRepository;
import com.servustech.eduson.features.products.liveEvents.LiveEventRepository;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionRepository;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionProductRepository;
import com.servustech.eduson.features.permissions.permissions.PermissionRepository;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionDto;
import com.servustech.eduson.features.permissions.subscriptions.BenefitDto;
import com.servustech.eduson.features.permissions.subscriptions.Subscription;
import com.servustech.eduson.features.permissions.permissions.PermissionDto;
import com.servustech.eduson.features.permissions.permissions.Permission;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionProduct;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionType;
import com.servustech.eduson.features.permissions.ProductType;
import com.servustech.eduson.features.permissions.Benefit;
import com.servustech.eduson.features.permissions.subscriptions.PeriodRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SubscriptionPeriodsService {
	
  private final SubscriptionPeriodRepository subscriptionPeriodRepository;
  private final PeriodRepository periodRepository;

  @Transactional
  public void save(Benefit benefit, ProductType productType, List<PeriodDto> periodDtoList) {
    // TODO at update, we must do so many things. we must check all the permissions to alter them in stripe.
    // subscriptionPeriodRepository.clearAll(benefit.getId(), productType);
    List<Period> periods = periodDtoList.stream().map(periodDto -> {
      Period period = null;
      if (periodDto.getId() != null) {
        period = getPeriod(periodDto.getId());
      } else {
        period = Period.builder()
          .name(periodDto.getName())
          .description(periodDto.getDescription())
          .interval(periodDto.getInterval())
          .intervalCount(periodDto.getIntervalCount())
          .build();
        period = periodRepository.save(period);
      }
      period.setName(periodDto.getName());
      period.setDescription(periodDto.getDescription());
      period.setInterval(periodDto.getInterval());
      period.setIntervalCount(periodDto.getIntervalCount());

      var subscriptionPeriod = getSubscriptionPeriod(benefit, period, periodDto, productType);
      subscriptionPeriod.setPrice(periodDto.getPrice());
      subscriptionPeriod.setPeriod(period);

      return period;
    }).collect(Collectors.toList());
  }
  public Period getPeriod(Long id) {
    return periodRepository.findById(id).orElseThrow(() -> new NotFoundException("period-w-id-not-exist"));
  }
  private SubscriptionPeriod getSubscriptionPeriod(Benefit benefit, Period period, PeriodDto periodDto, ProductType productType) {
    return subscriptionPeriodRepository
      .findByData(benefit.getId(), productType, period.getId())
      .orElseGet(() -> {
        var sp = SubscriptionPeriod
          .builder()
          .period(period)
          .price(periodDto.getPrice())
          .productType(productType)
          .productId(benefit.getId())
          .build();
        sp = subscriptionPeriodRepository.save(sp);
        return sp;
      });
  }
  public SubscriptionPeriod get(ProductType productType, Long productId, Long periodId) {
    return subscriptionPeriodRepository.findByData(productId, productType, periodId).orElseThrow(() -> new NotFoundException("subscriptionperiod-not-set"));
  }
  public void setStripePriceId(SubscriptionPeriod subscriptionPeriod, String priceStripeId) {
    subscriptionPeriod.setStripe(priceStripeId);
    subscriptionPeriodRepository.save(subscriptionPeriod);
  }
  public List<PeriodDto> getPeriods() {
    return periodRepository.findAll().stream().map(period -> PeriodDto
        .builder()
        .id(period.getId())
        .name(period.getName())
        .description(period.getDescription())
        .interval(period.getInterval())
        .intervalCount(period.getIntervalCount())
        .build()).collect(Collectors.toList());
  }

}
