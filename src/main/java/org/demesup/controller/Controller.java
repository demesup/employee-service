package org.demesup.controller;

import org.demesup.AppController;
import org.demesup.model.Field;
import org.demesup.model.Model;
import org.demesup.repository.Repository;
import org.utils.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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

    protected abstract <T extends Model> T getByFields();

    protected abstract <T extends Model> List<T> getListByFields();


    protected abstract <T extends Model> T getById();
}
