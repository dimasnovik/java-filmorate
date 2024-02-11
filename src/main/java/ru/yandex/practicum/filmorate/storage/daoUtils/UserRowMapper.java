package ru.yandex.practicum.filmorate.storage.daoUtils;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User(rs.getString("EMAIL"), rs.getString("LOGIN"),
                rs.getDate("BIRTHDAY").toLocalDate());
        user.setName(rs.getString("NAME"));
        user.setId(rs.getInt("USER_ID"));
        return user;
    }
}
