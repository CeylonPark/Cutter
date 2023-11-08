package com.ceylon.cutter.data;

public class CutterAbility {
    private final String ability;
    private final int multiple;
    private final int index;
    private int count;
    private final boolean[] results;

    public CutterAbility(String ability, int multiple, int index) {
        this.ability = ability;
        this.multiple = multiple;
        this.index = index;
        this.count = 0;
        this.results = new boolean[9];
    }

    public String getAbilityName() {
        return this.ability;
    }

    public int getAbility() {
        int count = 0;
        for(boolean result : this.results) {
            if(result) count++;
        }
        return count * this.multiple;
    }

    public int getIndex() {
        return this.index;
    }

    public int getCount() {
        return this.count;
    }

    public boolean isCut() {
        return this.count == 9;
    }

    public void addCount(boolean result) {
        this.results[count++] = result;
    }

    public boolean[] getResults() {
        return this.results;
    }
}
