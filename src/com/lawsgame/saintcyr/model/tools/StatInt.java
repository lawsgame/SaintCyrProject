package com.lawsgame.saintcyr.model.tools;

public class StatInt {
    private int value;
    private Integer min;
    private Integer max;

    private StatInt(){
        this.value = 0;
        this.min = null;
        this.max = null;
    }

    public static StatInt create(){
        StatInt stat = new StatInt();
        return stat;
    }

    public static StatInt create(Integer min, Integer max){
        StatInt stat = new StatInt();
        stat.setMin(min);
        stat.setMax(max);
        stat.val(min.intValue());
        return stat;
    }

    public int val(){
        return this.value;
    }

    public float valf(){
        return (float)value;
    }

    public int val(int inputValue) {
        int leftover = 0;
        int updatedValue =inputValue;
        if(min != null && inputValue < min){
            updatedValue = min.intValue();
            leftover = inputValue - min.intValue();
        }else if(max != null && inputValue > max){
            updatedValue = max.intValue();
            leftover = inputValue - max.intValue();
        }
        this.value = updatedValue;
        return leftover;
    }

    public int add(int inputValue){
        return val(value + inputValue);
    }

    public int sub(int inputValue){
        return val(value - inputValue);
    }

    public Integer getMin() {
        return min;
    }

    public int setMin(Integer min) {
        this.min = min;
        return val(value);
    }

    public Integer getMax() {
        return max;
    }

    public int setMax(Integer max) {
        this.max = max;
        return val(value);
    }

    public String toString(){
        return value+"";
    }



}
