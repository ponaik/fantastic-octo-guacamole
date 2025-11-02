package com.intern.userservice.mapper;

import com.intern.userservice.dto.UserCreateDto;
import com.intern.userservice.dto.UserResponse;
import com.intern.userservice.dto.UserUpdateDto;
import com.intern.userservice.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = { CardInfoMapper.class })
public interface UserMapper {
    UserResponse toUserResponse(User user);
    User fromUserResponse(UserResponse dto);

    UserCreateDto toUserCreateDto(User user);
    User fromUserCreateDto(UserCreateDto dto);

    UserUpdateDto toUserUpdateDto(User user);
    User fromUserUpdateDto(UserUpdateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserUpdateDto request, @MappingTarget User user);
}
