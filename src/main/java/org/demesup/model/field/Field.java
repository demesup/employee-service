package org.demesup.model.field;

import org.demesup.model.Model;
public interface Field {
    <T> T valueFromUser();
    <T extends Model> void setter(T model);

}
