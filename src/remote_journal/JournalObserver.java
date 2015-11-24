package remote_journal;

/**
 * Observer of journal manager.
 * If journal was changed journal manager informs his observer about that.
 * Observer informs clients about that journal was changed.
 */
public interface JournalObserver {
    /**
     * Informs client about updating.
     * @param login login of client that will be informed.
     */
    void updateUserInterface(String login);
    /**
     * Informs notification system about updating.
     * @param login login of client whose notification system will be updated.
     */
    void updateNotificationSystem(String login);
}
