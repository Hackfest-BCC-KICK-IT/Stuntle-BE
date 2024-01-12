package bcc.stuntle.util;

import bcc.stuntle.entity.CustomPage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.core.util.ObjectMapperFactory;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;

import java.util.List;

public class ObjectMapperUtils {

    public static final ObjectMapper mapper;

    static{
        mapper = ObjectMapperFactory
                .createJson()
                .registerModule(new JavaTimeModule())
                .setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    @SneakyThrows
    public static <T> T readValue(String str, Class<T> value){
        return mapper.readValue(str, value);
    }

    @SneakyThrows
    public static <T> List<T> readListValue(String str, Class<T> value){
        var factory = mapper.getTypeFactory();
        return mapper.readValue(str, factory.constructCollectionType(List.class, value));
    }

    @SneakyThrows
    public static <T> Page<T> readPageValue(String str, Class<T> value){
        var factory = mapper.getTypeFactory();
        var pageType = factory.constructParametricType(CustomPage.class, value);
        return mapper.readValue(str, pageType);
    }

    @SneakyThrows
    public static String writeValueAsString(Object obj){
        return mapper.writeValueAsString(obj);
    }
}
