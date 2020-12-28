package de.samuelhuebner.shopit.database;

public class HistoryEvent {
    private final String eventText;
    private final EventType type;

    public HistoryEvent(String eventText, EventType type) {
        this.eventText = eventText;
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public String getEventText() {
        return eventText;
    }
}
