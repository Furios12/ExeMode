package org.Exestudios.exeMode.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StaffSpyManager {
    private final Set<UUID> spies = new HashSet<>();

    public boolean toggle(UUID staff) {
        if (spies.contains(staff)) {
            spies.remove(staff);
            return false;
        } else {
            spies.add(staff);
            return true;
        }
    }

    public boolean isSpy(UUID staff) {
        return spies.contains(staff);
    }
}