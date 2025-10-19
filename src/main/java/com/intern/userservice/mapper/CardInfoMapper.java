package com.intern.userservice.mapper;

import com.intern.userservice.dto.CardInfoCreateDto;
import com.intern.userservice.dto.CardInfoResponse;
import com.intern.userservice.model.CardInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {
    CardInfoResponse toCardInfoResponse(CardInfo card);
    CardInfo fromCardInfoResponse(CardInfoResponse dto);

    CardInfoCreateDto toCardInfoCreateDto(CardInfo card);
    CardInfo fromCardInfoCreateDto(CardInfoCreateDto dto);
}
