package bcc.stuntle.mapper;

import bcc.stuntle.entity.DataKehamilan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataKehamilanMapper {

    DataKehamilanMapper INSTANCE = Mappers.getMapper(DataKehamilanMapper.class);

    DataKehamilan update(DataKehamilan src, @MappingTarget DataKehamilan target);
}
