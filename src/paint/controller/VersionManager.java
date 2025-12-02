package paint.controller;

import java.util.ArrayList;
import java.util.List;

public class VersionManager {

    private final List<CanvasVersionMemento> versions = new ArrayList<>();

    public void addVersion(CanvasVersionMemento m) {
        versions.add(m);
    }

    public CanvasVersionMemento getVersion(int index) {
        if (index < 0 || index >= versions.size()) return null;
        return versions.get(index);
    }

    public int size() {
        return versions.size();
    }
    
    //Added for UI
     public void removeVersion(int index) {
        if (index >= 0 && index < versions.size()) {
            versions.remove(index);
        }
    }
    
    public List<String> getVersionNames() {
        List<String> names = new ArrayList<>();
        for (CanvasVersionMemento m : versions) {
            names.add(m.getName());
        }
        return names;
    }
}
