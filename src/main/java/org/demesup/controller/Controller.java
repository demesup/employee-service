package org.demesup.controller;

import lombok.SneakyThrows;
import org.demesup.AppController;
import org.demesup.ModelType;
import org.demesup.NoModelWithSuchParametersException;
import org.demesup.model.field.Field;
import org.demesup.model.Model;
import org.demesup.repository.Repository;
import org.utils.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.demesup.AppController.Action.SEARCH;
import static org.utils.Patterns.askStringWhileDoesNotMatchToPattern;

public abstract class Controller {
    public static Repository repository = new Repository();

    public abstract void create();

    public abstract void delete();

    public abstract void read();

    public abstract void update();

    public abstract <T extends Model> T search();

    static List<Integer> getIndexes(AppController.Action action, Field[] fields) throws IOException {
        System.out.println(Utils.numberedArray(fields));
        return Arrays.stream(askStringWhileDoesNotMatchToPattern(
                Pattern.compile("^\\s?\\d(\\s\\d)?\\s?"),
                "Enter indexes of fields to " + action.name() + " by (separated by space). Example: 0 2 4 7")
                .split("\\s")).map(Integer::parseInt).toList();
    }

    protected abstract <T extends Model> Optional<T> getByFields();

    @SneakyThrows
    protected <T extends Model> List<T> getListByFields(ModelType type, Class<T> cl) {
        var fields = type.getFields();
        var indexes = getIndexes(SEARCH, fields);
        Map<String, String> map = IntStream.range(0, fields.length)
                .filter(indexes::contains)
                .mapToObj(f-> fields[f])
                .collect(Collectors.toMap(
                        field -> field.toString().toLowerCase(),
                        field -> field.valueFromUser().toString()));
        List<T> list = repository.getByFields(type, cl ,map);
        if (list.isEmpty()) throw new NoModelWithSuchParametersException(map.toString());
        return list;
    }


    protected abstract <T extends Model> Optional<T> getById();
}
