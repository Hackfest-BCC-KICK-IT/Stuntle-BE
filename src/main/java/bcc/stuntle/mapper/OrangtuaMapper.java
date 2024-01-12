package bcc.stuntle.mapper;

import bcc.stuntle.entity.Orangtua;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrangtuaMapper {

    OrangtuaMapper INSTANCE = Mappers.getMapper(OrangtuaMapper.class);

    Orangtua update(Orangtua dto, @MappingTarget Orangtua orangtua);
}
