package de.samuelhuebner.shopit.database;

public enum EventType {
    CREATED_LIST,
    CREATED_POSITION,
    CLEARED_HISTORY,
    MODIFIED_LIST,
    MODIFIED_POSITION,
    MARKED_AS_DONE,
    MARKED_AS_UNDONE,
    SHARED_ITEM,
    DELETED_LIST,
    DELETED_POS,
    RESTORED_POS,
    SHARED_LIST;
}
