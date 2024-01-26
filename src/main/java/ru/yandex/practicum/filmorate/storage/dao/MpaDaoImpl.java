package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class MpaDaoImpl implements MpaDao {
    private JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getById(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from MPA where MPA_ID = ?", mpaRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Нет MPA с id = " + id);
        }
    }

    @Override
    public Collection<Mpa> getAll() {
        return jdbcTemplate.query("select * from MPA", mpaRowMapper());
    }

    private RowMapper<Mpa> mpaRowMapper() {
        return (rs, rowNum) -> new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME"));
    }
}
