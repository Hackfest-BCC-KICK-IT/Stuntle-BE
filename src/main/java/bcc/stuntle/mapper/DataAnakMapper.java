package bcc.stuntle.mapper;

import bcc.stuntle.entity.DataAnak;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataAnakMapper {

    DataAnakMapper INSTANCE = Mappers.getMapper(DataAnakMapper.class);

    DataAnak update(DataAnak src, @MappingTarget DataAnak target);
}
