package com.github.jenkaby.service.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Optional;

@Slf4j
@Service
public class MeasurementService {

    public <T> MeasuredValue<T> measure(ThrowableSupplier<T> supplier) {
        StopWatch timer = new StopWatch();
        timer.start();
        try {
            T object = supplier.get();
            timer.stop();
            return new MeasuredValue<>(object, timer.getTotalTimeNanos());
        } catch (Throwable e) {
            log.error("Error occurred {}. {}", e.getClass(), e.getMessage());
            timer.stop();
            return new MeasuredValue<>(e, timer.getTotalTimeNanos());
        }
    }

    @AllArgsConstructor
    public static class MeasuredValue<T> {

        @Nullable
        private final T returned;
        @Nullable
        private final RuntimeException rte;
        @Getter
        private final long nanos;

        public MeasuredValue(@NonNull Throwable throwable, long nanos) {
            this(null, throwable, nanos);
        }

        public MeasuredValue(@Nullable T returned, long nanos) {
            this(returned, null, nanos);
        }

        private MeasuredValue(@Nullable T returned, @Nullable Throwable throwable, long nanos) {
            this.returned = returned;
            this.rte = Optional.ofNullable(throwable).map(RuntimeException::new).orElse(null);
            this.nanos = nanos;
        }

        @Nullable
        public T value() throws Throwable {
            unwrapAndThrowException();
            return returned;
        }

        private void unwrapAndThrowException() throws Throwable {
            if (rte != null) {
                throw rte.getCause();
            }
        }
    }

    public interface ThrowableSupplier<T> {

        T get() throws Throwable;
    }
}
