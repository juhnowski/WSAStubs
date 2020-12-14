package ru.bellintegrator.smartcontrol.core.api.dictionary;

public enum ActionEnum {

    /**
     * Activate action
     */
    ACTIVATE(0),
    /**
     * Deactivate action
     */
    DEACTIVATE(1),
    CREATE(2),
    UPDATE(3),
    DELETE(4);

    ActionEnum(int id) {
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
