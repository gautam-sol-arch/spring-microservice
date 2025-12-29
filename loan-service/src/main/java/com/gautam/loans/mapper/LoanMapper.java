package com.gautam.loans.mapper;

import com.gautam.loans.dto.LoanDto;
import com.gautam.loans.entity.Loan;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface LoanMapper {
    LoanDto toDto(Loan loan);

    @Mapping(target = "loanId", ignore = true)
    Loan toEntity(LoanDto loanDto);

    @Mapping(target = "loanId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(LoanDto loanDto, @MappingTarget Loan loan);
}
