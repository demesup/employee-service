package org.demesup.repository;

import org.demesup.model.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Repository {
    <T extends Model> void save(T model);

    <T extends Model> void update(T model);

    <T extends Model> void updateAll(Class<T> cl, Map<String, String> newValues);

    <T extends Model> void updateAllByFields(Class<T> cl,
                                             Map<String, List<String>> oldValues,
                                             Map<String, String> newValues);

    <T extends Model> Optional<T> getById(Class<T> cl, long id);

    <T extends Model> List<T> getAllByFields(Class<T> cl, Map<String, List<String>> values);

    <T extends Model> List<T> getAll(Class<T> cl);

    <T extends Model> void delete(T model);

    <T extends Model> void deleteAllByFields(Class<T> cl, Map<String, List<String>> values);

    <T extends Model> void deleteAll(Class<T> cl);
}
