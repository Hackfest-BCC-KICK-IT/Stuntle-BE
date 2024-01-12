package bcc.stuntle.mapper;

import bcc.stuntle.entity.AjukanBantuan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AjukanBantuanMapper {
    AjukanBantuanMapper INSTANCE = Mappers.getMapper(AjukanBantuanMapper.class);

    AjukanBantuan updateAjukanBantuan(AjukanBantuan src, @MappingTarget AjukanBantuan target);
}
