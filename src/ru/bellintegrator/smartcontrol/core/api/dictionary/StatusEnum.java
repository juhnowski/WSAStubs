package ru.bellintegrator.smartcontrol.core.api.dictionary;

public enum StatusEnum {
    CREATED(0),
    QUEUE(1),
    APPLYING(2),
    DONE(3),
    ERROR(4);

    StatusEnum(int id) {
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
