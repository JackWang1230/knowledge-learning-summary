package cn.wr.collect.sync.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MutationEvent implements Serializable {
    private static final long serialVersionUID = 8747424385484160818L;
    private List<MutationResult> mutationResults = new ArrayList<>();

    public void add(MutationResult mutationResult) {
        mutationResults.add(mutationResult);
    }

    public List<MutationResult> getMutationResults() {
        return mutationResults;
    }

    public void setMutationResults(List<MutationResult> mutationResults) {
        this.mutationResults = mutationResults;
    }
}
