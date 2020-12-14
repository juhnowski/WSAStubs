package ru.bellintegrator.smartcontrol.core.api.dictionary;

public enum ObjectTypeEnum {
    CUSTOM_CATEGORY(0),
    POLICY(1),
    COMMAND(2);

    ObjectTypeEnum(int id) {
        this._id = id;
    }

    private int _id;

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }
}
