package com.servustech.eduson.features.account.mapper;

import com.servustech.eduson.features.account.User;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.servustech.eduson.features.account.users.dto.UserDto;
import com.servustech.eduson.features.account.lectors.dto.LectorDto;

@Mapper // (unmappedTargetPolicy = ReportingPolicy.IGNORE)
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

    User signUpRequestToUser(UserDto registerRequest);

    User signUpRequestToLector(LectorDto registerRequest);

    User signUpRequestToAdmin(UserDto registerRequest);

}
