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
}
