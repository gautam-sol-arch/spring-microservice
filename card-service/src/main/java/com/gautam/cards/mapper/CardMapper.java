package com.gautam.cards.mapper;

import com.gautam.cards.dto.CardDto;
import com.gautam.cards.entity.Card;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CardMapper {
    CardDto toDto(Card card);

    @Mapping(target = "cardId", ignore = true)
    Card toEntity(CardDto cardDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "cardId", ignore = true)
    void updateEntityFromDto(CardDto cardDto, @MappingTarget Card card);

}
