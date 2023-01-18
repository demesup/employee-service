package org.demesup.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.demesup.AppController;
import org.demesup.NoModelWithSuchParametersException;
import org.demesup.model.Model;
import org.demesup.model.field.Field;
import org.demesup.repository.Repository;
import org.hibernate.ObjectNotFoundException;
import org.utils.Read;
import org.utils.Utils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.demesup.AppController.Action.*;
import static org.demesup.ModelType.modelTypeMap;
import static org.utils.Patterns.askStringWhileDoesNotMatchToPattern;
import static org.utils.Read.inputEqualsYes;
import static org.utils.Read.readNumber;
import static org.utils.Utils.listInSeparatedLines;

@Slf4j
public abstract class Controller {
    public static Repository repository = new Repository();

    public abstract <T extends Model> T create();

    public abstract void delete();

    @SneakyThrows
    protected <T extends Model> void delete(Class<T> cl) {
        String entity = cl.getSimpleName();
        switch (Read.readNumber(3, "Enter:" +
                "\n\t0-delete one " + entity +
                "\n\t1-delete all from " + entity + " table " +
                "\n\t2-delete " + entity + "(s) by field(s)")) {
            case 0 -> {
                T model = getModel();
                repository.delete(model);
            }
            case 1 -> repository.deleteAll(cl);
            case 2 -> repository.deleteAllByFields(cl, getExistingValuesMap(cl, DELETE));
        }
    }

    @SneakyThrows
    protected <T extends Model> void update(Class<T> cl, Field[] fields) {
        String entity = cl.getSimpleName();
        switch (Read.readNumber(3, "Enter:" +
                "\n\t0-update one " + entity +
                "\n\t1-update all from " + entity + " table " +
                "\n\t2-update " + entity + "(s) by field(s)")) {
            case 0 -> {
                T model = search();
                repository.update(getUpdated(model, fields));
            }
            case 1 -> repository.updateAll(cl, getNewValuesMap(cl, UPDATE));
            case 2 -> repository.updateAllByFields(cl,
                    getExistingValuesMap(cl, UPDATE),
                    getNewValuesMap(cl, UPDATE));
        }
    }

    public abstract void read();

    public abstract void update();

    public <T extends Model> T search() {
        T model = getModel();
        log.debug(model.toString());
        return model;
    }

    static List<Integer> getIndexes(AppController.Action action, Field[] fields) throws IOException {
        log.debug(Utils.numberedArray(fields));
        return Arrays.stream(askStringWhileDoesNotMatchToPattern(
                Pattern.compile("^\\s?\\d(\\s\\d)?\\s?"),
                "Enter indexes of fields to " + action.name() + " by (separated by space). Example: 0 2 4 7")
                .split("\\s")).map(Integer::parseInt).toList();
    }

    @SneakyThrows
    protected <T extends Model> List<T> getListByFields(Class<T> cl) {
        Map<Field, List<String>> values = getExistingValuesMap(cl, SEARCH);
        List<T> list = repository.getAllByFields(cl, values);
        if (list.isEmpty()) throw new NoModelWithSuchParametersException(values.toString());
        return list;
    }

    public <T extends Model> Map<Field, List<String>> getExistingValuesMap(Class<T> cl, AppController.Action action) throws IOException {
        var fields = modelTypeMap.get(cl).getFields();
        var indexes = getIndexes(action, fields);
        return getValues(fields, indexes);
    }

    public static <T extends Model> Map<Field, String> getNewValuesMap(Class<T> cl, AppController.Action action) throws IOException {
        var fields = modelTypeMap.get(cl).getFields();
        var indexes = getIndexes(action, fields);
        return IntStream.range(0, fields.length)
                .filter(indexes::contains)
                .mapToObj(i -> fields[i])
                .collect(Collectors.toMap(Function.identity(), f -> f.valueFromUser().toString()));
    }

    protected Map<Field, List<String>> getValues(Field[] fields, List<Integer> indexes) {
        Map<Field, List<String>> map = new HashMap<>();
        IntStream.range(0, fields.length)
                .filter(indexes::contains)
                .mapToObj(i -> fields[i])
                .forEachOrdered(f -> map.put(f, getValues(f)));
        return map;
    }

    static List<String> getValues(Field f) {
        List<String> values = new ArrayList<>();
        try {
            do {
                values.add(f.valueFromUser().toString());
            } while (inputEqualsYes("Enter another value for field " + f + "?"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return values;
    }

    protected abstract <T extends Model> Optional<T> getById();

    protected abstract <T extends Model> Optional<T> getOneByFields();

    @SneakyThrows
    protected <T extends Model> Optional<T> getOneByFields(Class<T> cl) {
        var result = getListByFields(cl);
        if (result.size() == 1) return Optional.of(result.get(0));
        log.debug(listInSeparatedLines(result));
        int index = readNumber(result.size(), "Enter index");
        return Optional.of(result.get(index));
    }

    @SneakyThrows
    protected <T extends Model> T getModel() {
        try {
            Optional<T> optional =
                    inputEqualsYes("Do you know id?") ? getById() : getOneByFields();
            if (optional.isEmpty()) throw new NoModelWithSuchParametersException();
            return optional.get();
        } catch (ObjectNotFoundException e) {
            throw new NoModelWithSuchParametersException();
        }
    }

    @SneakyThrows
    protected <T extends Model> T getUpdated(T model, Field[] fields) {
        getIndexes(UPDATE, fields).stream().map(i -> fields[i]).forEach(f -> f.setter(model));
        return model;
    }
}
