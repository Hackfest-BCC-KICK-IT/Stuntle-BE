package bcc.stuntle.util;

import bcc.stuntle.constant.DateTimeConstant;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class QueryUtils {

    public static <T> Optional<Query> createQuerySearch(@NonNull T obj, boolean showDeletedData){
        log.info("accepting obj = {} with showDeletedData = {} in QueryUtils", obj, showDeletedData);
        Class<?> metaClass = obj.getClass();
        Field[] fields = metaClass.getDeclaredFields();
        List<Criteria> criterias = new ArrayList<>();
        try {
            for(Field f: fields){
                try {
                    f.setAccessible(true);
                    Annotation annotation = f.getAnnotation(Column.class);
                    if (annotation != null) {
                        Object fieldValue = f.get(obj);
                        String columnName = (String) annotation.annotationType().getMethod("value").invoke(annotation);
                        if(!(columnName.equalsIgnoreCase(DateTimeConstant.CREATED_AT) || columnName.equalsIgnoreCase(DateTimeConstant.UPDATED_AT))){
                            if(columnName.equalsIgnoreCase(DateTimeConstant.DELETED_AT) && !showDeletedData){
                                criterias.add(Criteria.where(columnName).isNull());
                            } else {
                                if(fieldValue != null){
                                    criterias.add(Criteria.where(columnName).like(String.format("%%%s%%", fieldValue)).ignoreCase(true));
                                }
                            }
                        }
                    }
                } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                } finally {
                    f.setAccessible(false);
                }
            }
            Criteria criteria = Criteria.empty();
            for (var tempCriteria: criterias){
                criteria = criteria.and(tempCriteria);
            }
            return Optional.of(Query.query(criteria));
        } catch(Exception ex){
            log.error("error when transforming obj into Query with message {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
