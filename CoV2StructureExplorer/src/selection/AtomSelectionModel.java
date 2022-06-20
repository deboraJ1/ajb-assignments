package selection;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import model.atoms.AtomI;

import java.util.Collection;

public class AtomSelectionModel implements SelectionModel<AtomI>{

    private final ObservableSet<AtomI> selectedAtoms;

    public AtomSelectionModel(){
        this.selectedAtoms = FXCollections.observableSet();
    }

    @Override
    public boolean select(AtomI atomI) {
        if(atomI != null){
            this.selectedAtoms.add(atomI);
            return true;
        }
        return false;
    }

    @Override
    public boolean setSelected(AtomI atomI, boolean select) {
        if(atomI != null && selectedAtoms.size() > 0){
            if(selectedAtoms.contains(atomI)){
                //part of set but shall be unselected
                if(!select) {
                    selectedAtoms.remove(atomI);
                }
            }
            else{
                //not yet part but shall be selected
                if(select){
                    selectedAtoms.add(atomI);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean selectAll(Collection<AtomI> list) {
        clearSelection();
        if(list != null && !list.isEmpty()){
            this.selectedAtoms.addAll(list);
            return true;
        }
        return false;
    }

    @Override
    public void clearSelection() {
        this.selectedAtoms.clear();
    }

    @Override
    public boolean clearSelection(AtomI atomI) {
        if(atomI != null){
            return this.selectedAtoms.remove(atomI);
        }
        return false;
    }

    @Override
    public boolean clearSelection(Collection<AtomI> list) {
        if(list != null && list.size() > 0) {
            this.selectedAtoms.removeAll(list);
        }
        return false;
    }

    @Override
    public ObservableSet<AtomI> getSelectedItems() {
        return this.selectedAtoms;
    }

    @Override
    public IntegerProperty getSelectedItemsSizeProperty(){
        return new SimpleIntegerProperty(this.selectedAtoms.size());
    }
}
