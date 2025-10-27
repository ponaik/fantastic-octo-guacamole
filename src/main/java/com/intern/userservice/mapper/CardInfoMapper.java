package com.intern.userservice.mapper;

import com.intern.userservice.dto.CardInfoCreateDto;
import com.intern.userservice.dto.CardInfoResponse;
import com.intern.userservice.model.CardInfo;
import com.intern.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {
    @Mapping(source = "user.id", target = "userId")
    CardInfoResponse toCardInfoResponse(CardInfo card);

    @Mapping(source = "userId", target = "user")
    CardInfo fromCardInfoResponse(CardInfoResponse dto);

    CardInfoCreateDto toCardInfoCreateDto(CardInfo card);
    CardInfo fromCardInfoCreateDto(CardInfoCreateDto dto);

    default User map(Long userId) {
        if (userId == null) return null;
        User u = new User();
        u.setId(userId);
        return u;
    }

}
