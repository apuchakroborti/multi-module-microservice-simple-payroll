package com.apu.payslip.utils;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Converter {


    private static ModelMapper modelMapper = new ModelMapper();

    public static <U, V> V convertClass(U mapperObject, Class<V> targetClass) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(mapperObject, targetClass);
    }

    public static <Source, Dest> void copyProperty(Source source, Dest target) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.map(source, target);
    }

    public static <U, V> List<V> toDtoList(List<U> mapperObjects, Class<V> targetClass) {
        List<V> dtoObjects = mapperObjects
                .stream()
                .map(u -> convertClass(u, targetClass))
                .collect(Collectors.toList());

        return dtoObjects;
    }

    public static <U, V> List<V> toDtoList(Iterable<U> mapperObjects, Class<V> targetClass) {
        List<V> dtoObjects = new ArrayList<>();

        mapperObjects.forEach(object -> {
            dtoObjects.add(convertClass(object, targetClass));
        });

        return dtoObjects;
    }

    public static <U, V> Page<V> toDtoPage(Page<U> mapperObjects, Class<V> targetClass) {
        Page<V> dtoObjects = mapperObjects
                .map(u -> convertClass(u, targetClass));
        return dtoObjects;
    }
}