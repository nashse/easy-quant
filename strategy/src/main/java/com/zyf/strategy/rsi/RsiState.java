package com.zyf.strategy.rsi;

import com.zyf.common.model.enums.ExchangeEnum;

/**
 * rsi状态类
 * @author yuanfeng.z
 * @date 2020/8/18 17:37
 */
public class RsiState {

    public enum State {
        PREPARE("prepare"),
        MIDDLE("middle"),
        FINISH("finish");

        private String value;

        State(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private State state = State.PREPARE;

    public void setState(State s) {
        state = s;
//        if (State.PREPARE.equals(state)) {
//            state = State.MIDDLE;
//        } else if (State.MIDDLE.equals(state)) {
//            state = State.FINISH;
//        }
    }

    public State getState() {
        return state;
    }
}
