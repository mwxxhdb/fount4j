package com.fount4j.base.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResult<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> BaseResult<T> success() {
        return new BaseResult<>(true, null, null);
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<>(true, data, null);
    }

    public static <T> BaseResult<T> success(T data, String message) {
        return new BaseResult<>(true, data, message);
    }

    public static <T> BaseResult<T> fail(String message) {
        return new BaseResult<>(false, null, message);
    }

    public void ifSuccess(Consumer<T> consumer) {
        if (success) {
            consumer.accept(data);
        }
    }

    public <U> BaseResult<U> map(Function<T, U> converter) {
        if (success) {
            return new BaseResult<>(true, converter.apply(data), message);
        }
        return new BaseResult<>(false, null, message);
    }

}
