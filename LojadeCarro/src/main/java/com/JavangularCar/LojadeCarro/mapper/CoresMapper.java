package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.CoresRequest;
import com.JavangularCar.LojadeCarro.dto.response.CoresResponse;
import com.JavangularCar.LojadeCarro.entity.Cores;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoresMapper {
    Cores toEntity(CoresRequest request);
    CoresResponse toRecord(Cores cores);
}
