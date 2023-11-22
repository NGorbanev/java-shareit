package ru.practicum.shareit.utils;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ValidatonException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaginationTest {

    @Test
    public void ifFromIsZeroAndSizeIsNullResultIsOk() {
        Integer from = 0;
        Integer size = null;
        Pagination pagination = new Pagination(from, size);
        assertThat(pagination.getIndex()).isEqualTo(0);
        assertThat(pagination.getPageSize()).isEqualTo(1000);
        assertThat(pagination.getTotalPages()).isEqualTo(0);
    }

    @Test
    public void ifSizeIsLessThenZeroThenValidateException() {
        Integer from = 0;
        Integer size = -1;
        assertThrows(ValidatonException.class, () -> new Pagination(from, size));
    }

    @Test
    public void ifFromIsLessThanZeroThrowValidateException() {
        Integer from = -1;
        Integer size = 1;
        assertThrows(ValidatonException.class, () -> new Pagination(from, size));
    }

    @Test
    public void ifSizeIsLessThanFromResultIsOk() {
        Integer from = 2;
        Integer size = 1;
        Pagination pagination = new Pagination(from, size);
        assertThat(pagination.getIndex()).isEqualTo(1);
        assertThat(pagination.getPageSize()).isEqualTo(2);
        assertThat(pagination.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void ifSizeIsMoreThanFromResultIsOk() {
        Integer from = 1;
        Integer size = 2;
        Pagination pagination = new Pagination(from, size);
        assertThat(pagination.getIndex()).isEqualTo(1);
        assertThat(pagination.getPageSize()).isEqualTo(1);
        assertThat(pagination.getTotalPages()).isEqualTo(3);
    }

    @Test
    public void ifSizeIsEqualsFromResultIsOk() {
        Integer from = 1;
        Integer size = 1;
        Pagination pagination = new Pagination(from, size);
        assertThat(pagination.getIndex()).isEqualTo(1);
        assertThat(pagination.getPageSize()).isEqualTo(1);
        assertThat(pagination.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void ifSizeIsNotZeroAndFromIsZeroResultIsOk() {
        Integer from = 0;
        Integer size = 1;
        Pagination pagination = new Pagination(from, size);
        assertThat(pagination.getIndex()).isEqualTo(0);
        assertThat(pagination.getPageSize()).isEqualTo(1);
        assertThat(pagination.getTotalPages()).isEqualTo(1);
    }
}
