package com.testservice.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.testservice.domain.User;

/**
 * UserService is the service for loading {@link User} instance from the database.
 * 
 * @author taras
 *
 */
@Component
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(UserService.class);

    /**
     * Loads {@link User} instance from database.
     * 
     * @param name name of User instance
     * @param password password of User instance
     * @return User instance
     */
    public User load(String name, String password) {
        try {
            return jdbcTemplate.queryForObject("select * from User where name=? and password=?",
                    new Object[] { name, password }, new BeanPropertyRowMapper<User>(User.class));
        } catch (DataAccessException e) {
            LOGGER.error("Unable to load user with name=" + name, e);
            return null;
        }
    }
}