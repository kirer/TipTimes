package com.tiptimes.signal;


import java.util.HashMap;

/**
 * 信号
 */
public class Signal {

    private int distributionCount;//信号分发数量
    public String target;//信号目标标识
    public HashMap<String, Object> params;

    public static class Builder {
        private String target;
        private HashMap<String, Object> params = new HashMap<>();

        public Builder setTarget(String target) {
            this.target = target;
            return this;
        }

        public Builder setParams(HashMap<String, Object> params) {
            this.params = params;
            return this;
        }

        public Builder addParams(String key, Object value) {
            this.params.put(key, value);
            return this;
        }

        public Signal Build() {
            Signal signal = new Signal();
            signal.target = target;
            signal.params = params;
            return signal;

        }
    }

    public int getDistribution() {
        return distributionCount;
    }

    public void Distribution() {
        this.distributionCount++;
    }
}
