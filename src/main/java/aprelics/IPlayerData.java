package aprelics;

public interface IPlayerData {
    default void aprelics_setIsVolcanicSlamming(boolean value) {}
    default boolean aprelics_getIsVolcanicSlamming(){return false;}

    default void aprelics_setIsRevengeArmed(boolean value) {}
    default boolean aprelics_getIsRevengeArmed() { return false; };

    default void aprelics_setRevengeArmed(boolean b) {}
    void aprelics_setLastRevengeTime(long time);
    long aprelics_getLastRevengeTime();

    void aprelics_setCooldown(int ticks);
    int aprelics_getCooldown();

    int aprelics_getRevengeDuration();
    void aprelics_setRevengeDuration(int duration);
}