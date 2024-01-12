package bcc.stuntle.mapper;

import bcc.stuntle.entity.GrupWhatsapp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WhatsappMapper {

    WhatsappMapper INSTANCE = Mappers.getMapper(WhatsappMapper.class);

    void update(GrupWhatsapp grupWhatsapp, @MappingTarget GrupWhatsapp targetGrupWhatsapp);
}
